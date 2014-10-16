package com.rankminer.featurevectoranalyzer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.rankminer.featurevectoranalyzer.configuration.SCPConfig;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

/**
 * Class responsible for scp files from remote server to local directory.
 * 
 * @author achavan
 *
 */
public class SCPHandler {

	public void downloadFiles(List<String> filePaths, SCPConfig config) throws UnknownHostException, IOException {
		SSHClient client = new SSHClient();
		client.addHostKeyVerifier(new PromiscuousVerifier());
		client.connect(InetAddress.getByName(config.getHostName()));
		client.authPassword(config.getUserName(), config.getPassword());
		
		SCPFileTransfer transfer =  client.newSCPFileTransfer();
		for(String filePath : filePaths) {
			transfer.download(filePath, config.getDestinationFolder());	
		}
		
		
		client.disconnect();
	}
}
