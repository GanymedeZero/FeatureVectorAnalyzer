package com.rankminer.featurevectoranalyzer.utils;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;

/**
 * Class to handle email
 * @author achavan
 *
 */
public class EmailHandler {
	
	public static void emailEvent(String event, String subject) {
		try {
			Email email = new SimpleEmail();
			email.setHostName(ApplicationLauncher.configurations.getEmailConfiguration().getHostName());
			email.setSmtpPort(ApplicationLauncher.configurations.getEmailConfiguration().getSmtpPort());
			email.setAuthenticator(new DefaultAuthenticator(ApplicationLauncher.configurations.getEmailConfiguration().getUserName(), 
					ApplicationLauncher.configurations.getEmailConfiguration().getPassword()));
			email.setSSLOnConnect(true);
			email.setFrom(ApplicationLauncher.configurations.getEmailConfiguration().getSenderEmail());
			email.setSubject(subject);
			email.setMsg(event);
			email.addTo(findReceiverEmails(ApplicationLauncher.configurations.getEmailConfiguration().getReceiverEmail()));
			email.send();
		}catch(Exception e) {
			ApplicationLauncher.logger.severe("Problems with email client "+ e.getMessage());
		}
		
	}
	
	/**
	 * Function splits the email string using the split(":") function if there are multiple email ids.
	 * @param email
	 * @return
	 */
	private static String[] findReceiverEmails(String email) {
		String[] emailIds = null;
		if(email.contains(";")) {
			emailIds = email.split(";");
		} else {
			emailIds = new String[1];
			emailIds[0] = email;
		}
		return emailIds;
	}
}
