package com.rankminer.featurevectoranalyzer;



import org.apache.log4j.Logger;


/**
 * 
 * Launches application
 * @author Amit
 * 
 *
 */
public final class ApplicationLauncher {

	private static final Logger LOGGER = Logger
			.getLogger(ApplicationLauncher.class);

	public ApplicationLauncher() {
	}

	
	/**
	 * Load the Spring Integration Application Context
	 *
	 * @param args
	 *            - command line arguments
	 */
	public static void main(final String args[]) {
		ApplicationLauncher l = new ApplicationLauncher();
		System.out.println("Hello");
	}
}
