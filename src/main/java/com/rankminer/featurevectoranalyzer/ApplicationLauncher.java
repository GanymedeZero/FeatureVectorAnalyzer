package com.rankminer.featurevectoranalyzer;



import java.io.File;

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

	private Configuration configuration;
	
	private static final String[] commands = {"extract", "scp-copy", "export-features"};
	
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
	public Configuration readConfigurationFile(String fileName) throws JAXBException {
		File file = new File(fileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
		Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
		Configuration config = (Configuration)jaxbMarshaller.unmarshal(file);
		return config;
	}
	
	
	
	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args
	 *            - command line arguments
	 */
	public static void main(final String args[]) {
		ApplicationLauncher l = new ApplicationLauncher();
		try {
			ConsoleReader console = new ConsoleReader(System.in, System.out);
            console.setPrompt("prompt> ");
            console.addCompleter(new StringsCompleter(commands));
            console.setBellEnabled(false);
			l.setConfiguration(l.readConfigurationFile(args[0]));
			FeatureVectorFileProcessor processor =  new FeatureVectorFileProcessor(l.getConfiguration());
			processor.processFile(args[1]);
		} catch (Exception e) {
			System.out.println("Unable to read configuration. Exiting program");
			System.exit(0);
		}		
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
