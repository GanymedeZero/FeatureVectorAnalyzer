package com.rankminer.featurevectoranalyzer;



import java.io.File;
import java.io.PrintWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.configuration.DbConfiguration;
import com.rankminer.featurevectoranalyzer.configuration.SCPConfig;


/**
 * 
 * Launches application
 * @author Amit
 * 
 *
 */
public final class ApplicationLauncher {

	private static Configuration configuration;
	
	private static final String[] commands = {"exit","extract", "scp-copy", "export-metadata","translate", "usage","quit"};
	
	public static void writeConfiguration() throws JAXBException {
		File file = new File("configuration.xml");
		Configuration config = new Configuration();
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
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		System.out.println("Generating sample configuration file - " + file.getName() );
		jaxbMarshaller.marshal(config, file);
		
	}
	

	/**
	 * 
	 * @param fileName
	 * @return Configuration
	 * @throws JAXBException 
	 */
	public static Configuration readConfigurationFile(String fileName) throws JAXBException {
		File file = new File(fileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
		Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
		configuration = (Configuration)jaxbMarshaller.unmarshal(file);
		return configuration;
	}
	
	
	
	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args
	 *            - command line arguments
	 */
	public static void main(final String args[]) {
		//ApplicationLauncher l = new ApplicationLauncher();
		try {
			ConsoleReader console = new ConsoleReader(System.in, System.out);
            console.setPrompt("prompt> ");
            console.addCompleter(new StringsCompleter(commands));
            console.setBellEnabled(false);
            String line;
            PrintWriter out = new PrintWriter(System.out);
            while ((line = console.readLine("prompt> ")) != null) {
            	out.println("======>\"executing " + line + "\"");
            	out.flush();
            	if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
            		break;
                } else if(line.equalsIgnoreCase("usage")) {
            		showUsage(out);
            	} else if(line.contains("export-metadata")) {
            		extractMetadata(line);
            	} else if(line.contains("scp-copy")) {
            		scpCopy();
            	} else if(line.contains("extract")) {
            		extractFeatureVector(line);
            	} else if(line.contains("translate")) {
            		translateFVXmlToCsv(line);
            	}
            }
            
/*			l.setConfiguration(l.readConfigurationFile(args[0]));
			FeatureVectorFileProcessor processor =  new FeatureVectorFileProcessor(l.getConfiguration());
			processor.processFile(args[1]);
*/		} catch (Exception e) {
			System.out.println("Unable to read configuration. Exiting program");
			System.exit(0);
		}		
	}


	private static void translateFVXmlToCsv(String command) {
		// TODO Auto-generated method stub
		FeatureVectorConverter converter = new FeatureVectorConverter();
		converter.convertFeatureVectorXmlToCsv(command.split(" ")[2], command.split(" ")[3]);	
	}


	private static void extractFeatureVector(String command) {
			
	}


	private static void scpCopy() throws JAXBException {
		SCPHandler scpHandler = new SCPHandler(readConfigurationFile("configuration.xml"));
		scpHandler.downloadFiles();
	}


	private static void extractMetadata(String command) {
		FeatureVectorFileProcessor processor;
		try {
			processor = new FeatureVectorFileProcessor(readConfigurationFile("configuration.xml"));
			processor.processFile(command.split(" ")[1]);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private static void showUsage(PrintWriter out) {
		out.println("This application is driven by configuration file.\n"
				+ "use tab to see what options are supported\n"
				+ "export-metadata fileName.csv\n. Reads the csv file and populates metadata table"
				+ "scp-copy [Copies audio files from remote machine locally]\n"
				+ "extract calls the console-connector application to extract the feature vector blob to xml\n"
				+ "translate takes the featurevector xml and converts to csv format. takes two arguments 1. xml file name 2. csv file name\n"				
				+ "quit or exit to exit the application");
		out.flush();
		
	}


	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
