package com.rankminer.featurevectoranalyzer;
import java.io.File;
import java.io.PrintWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

import com.rankminer.featurevectoranalyzer.commands.DciTaskFactory;
import com.rankminer.featurevectoranalyzer.commands.RpmTaskFactory;
import com.rankminer.featurevectoranalyzer.commands.TaskFactory;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.configuration.Configurations;
import com.rankminer.featurevectoranalyzer.configuration.DbConfiguration;
import com.rankminer.featurevectoranalyzer.configuration.SCPConfig;
import com.rankminer.featurevectoranalyzer.utils.EmailHandler;


/**
 * 
 * Launches application
 * @author Amit
 * 
 *
 */
public final class ApplicationLauncher {

	public static Configurations configurations;
	public static Logger logger = Logger.getLogger(ApplicationLauncher.class.getSimpleName());
	
	static {
		try {
			FileHandler handler = new FileHandler("rankminer-app%g.log", 100000000, 9999, true);
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			logger.setLevel(Level.INFO);
		} catch (Exception e) {
		  //TODO email 
		}
	}
	
	
	public enum TaskType {
        EXPORTMETADATA(1), SCPCOPY(2), TRANSLATEXMLTOCSV(3), EXTRACTFV(4), COPYFILE(5), DAILYGEN(6);
        private int value;

        private TaskType(int value) {
                this.setValue(value);
        }

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	};   

	
	/**
	 * Creates TaskFactory based on the environment.
	 * @param environment [e.g - 'dci', 'rpm']
	 * @return TaskFactory
	 */
	public static TaskFactory getFactory(String environment) {
		TaskFactory factory = null;
		if(environment.trim().contains("dci")) {
			factory = new DciTaskFactory();
		} else if(environment.trim().contains("rpm")) {
			factory = new RpmTaskFactory();
		}
		return factory;
	}
	
	private static final String[] commands = {"daily-gen","exit","extract", "scp-copy","copy","show-env","export-metadata","translate", "usage","quit"};
	
	public static void writeConfiguration() throws JAXBException {
		File file = new File("configurationt.xml");
		Configurations c = new Configurations();
		
		Configuration config = new Configuration();
		config.setEnvironment("dci");
		SCPConfig ftp = new SCPConfig();
		ftp.setDestinationFolder("/opt/rankminer/to_process");
		ftp.setHostName("10.01.22.22");
		ftp.setPassword("admin");
		ftp.setUserName("admin");
		config.setSCPConfig(ftp);
		
		DbConfiguration dbConfig = new DbConfiguration();
		dbConfig.setDbName("rankminer");
		dbConfig.setUserName("admin");
		dbConfig.setPassword("admin");
		dbConfig.setHostName("server");
		config.setDbConfiguration(dbConfig);
		
		c.getConfigurationList().add(config);
		
		config = new Configuration();
		config.setEnvironment("rpm");
	    ftp = new SCPConfig();
		ftp.setDestinationFolder("/opt/rankminer/to_process");
		ftp.setHostName("10.01.22.22");
		ftp.setPassword("admin");
		ftp.setUserName("admin");
		config.setSCPConfig(ftp);
		
	    dbConfig = new DbConfiguration();
		dbConfig.setDbName("rankminer");
		dbConfig.setUserName("admin");
		dbConfig.setPassword("admin");
		dbConfig.setHostName("server");
		config.setDbConfiguration(dbConfig);
		
		c.getConfigurationList().add(config);
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Configurations.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		System.out.println("Generating sample configuration file - " + file.getName() );
		jaxbMarshaller.marshal(c, file);
		
	}
	

	/**
	 * 
	 * @param fileName
	 * @return Configurations
	 * @throws JAXBException 
	 */
	public static  void readConfigurationFile(String fileName) throws JAXBException {
		File file = new File(fileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(Configurations.class);
		Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
	    configurations = (Configurations)jaxbMarshaller.unmarshal(file);
	}
	
	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args
	 *            - command line arguments
	 * @throws JAXBException 
	 */
	public static void main(final String args[]) throws JAXBException {
		try {
			logger.info("Reading configurations from configuration.xml file");
			readConfigurationFile("configuration.xml");
			ConsoleReader console = new ConsoleReader(System.in, System.out);
            console.setPrompt("prompt> ");
            console.addCompleter(new StringsCompleter(commands));
            console.setBellEnabled(false);
            String command;
            PrintWriter out = new PrintWriter(System.out);
            while ((command = console.readLine("prompt> ")) != null) {
            	out.println("======>\"executing " + command + "\"");
            	logger.info("Executing ---> " + command);
            	out.flush();
            	if (command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("exit")) {
            		break;
                } else if(command.equalsIgnoreCase("usage")) {
            		showUsage(out);
            	} else if(command.contains("export-metadata")) {
            		executeCommand(command,TaskType.EXPORTMETADATA);
            		break;
            	} else if(command.contains("scp-copy")) {
            		executeCommand(command, TaskType.SCPCOPY);
            		break;
            	} else if(command.contains("extract")) {
            		executeCommand(command, TaskType.EXTRACTFV);
            		break;
            	} else if(command.contains("translate")) {
            		executeCommand(command, TaskType.TRANSLATEXMLTOCSV);
            		break;
            	} else if(command.contains("show-env")) {
            		showEnvironment();
            	} else if(command.contains("copy")) {
            		executeCommand(command, TaskType.COPYFILE);
            		break;
            	} else if(command.contains("daily-gen")) {
            		executeCommand(command, TaskType.DAILYGEN);
            		break;
            	}
            }
		} catch (Exception e) {
			System.out.println("Unable to read configuration. Exiting program");
			logger.severe("Problem reading configuration.xml. Program will shut down. Error -- " + e.getMessage());
			EmailHandler.emailEvent("Problem reading configuration.xml. Program will shut down", "Re: Problems reading configuration.xml");
			System.exit(0);
		}		
	}

	private static void showEnvironment() {
		for(Configuration config :configurations.getConfigurationList()) {
			System.out.println("\nEnvironment " + config.getEnvironment());
		}		
	}

	/**
	 * Method returns the correct configuration object based on the environment and factory type initialized.
	 * @param factory
	 * @return Configuration
	 */
	private static Configuration getConfiguration(String environment) {
		Configuration config = null;
		if(environment.contains("dci")) {
			config = configurations.getConfiguration("dci");
		}else if(environment.contains("rpm")) {
			config = configurations.getConfiguration("rpm");
		}
		return config;
	}
	
	/**
	 * Returns the environment as a string
	 * @param args
	 * @return Environment for which the command was executed
	 */
	private static String getEnvironmentStr(String args[]) {
		String env = null;
		if(args.length >= 2 && (args[1].contains("dci") || args[1].contains("rpm"))) {
			env = args[1];
		} else {
			EmailHandler.emailEvent("Environment must be passed as the argument to commands. Currently support for dci and rpm environment only",
									"Re: Environment variable not passed to command.");
			logger.severe("Environment must be passed as the argument to commands. Currently support for dci and rpm environment only");
		}
		return env;
	}
	

	/**
	 * Function executes the task based on input tasktype.
	 * @param command
	 * @param type
	 */
	private static void executeCommand(String command, TaskType type) {
		String args[] = command.split(" ");
		String environment = getEnvironmentStr(args);
		if(environment == null) {
			return;
		} 
		TaskFactory factory = ApplicationLauncher.getFactory(environment);
		TaskInterface task = factory.createTask(type);
		task.executeCommand(args, getConfiguration(environment));
	}

	/**
	 * Shows usage of the program
	 * @param out
	 */
	private static void showUsage(PrintWriter out) {
		out.println("This application is driven by configuration file.\n"
				+ "use tab to see what options are supported\n"
				+ "daily-gen will process ranklist.csv file for that day"
				+ "export-metadata {environment} {fileName.csv}\n. Reads the csv file and populates metadata table"
				+ "scp-copy {environment} [Copies audio files from remote machine locally]\n"
				+ "extract {environment} calls the console-connector application to extract the feature vector blob to xml\n"
				+ "translate {environment} takes the featurevector xml and converts to csv format. takes two arguments 1. xml file name 2. csv file name\n"	
				+ "show-environment lists the environments configured for the application. See details for configuration.xml file"
				+ "quit or exit to exit the application");
		out.println("\n");
		out.flush();		
	}
	
}
