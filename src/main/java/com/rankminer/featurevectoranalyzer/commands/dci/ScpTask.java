package com.rankminer.featurevectoranalyzer.commands.dci;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.dao.MetaDataDao;
import com.rankminer.featurevectoranalyzer.model.MetaDataModel;
import com.rankminer.featurevectoranalyzer.utils.EmailHandler;

/**
 * SCP copies the files from remote server
 * 
 * @author achavan
 *
 */
public class ScpTask implements TaskInterface {

	@Override
	public void executeCommand(String[] args, Configuration configuration) {
		downloadFiles(configuration);
	}

	/**
	 * Method calls the MetaDataDao and gets all entries which has rec_status=1
	 * or 2. Then prepares file path of audio file to be copied and then scps
	 * the file locally.
	 */
	public void downloadFiles(Configuration configuration) {
		try {
			MetaDataDao dao = new MetaDataDao(configuration);
			List<MetaDataModel> modelList = dao.getMetaDataModelByRecStatus(configuration.getSCPConfig().getRecStatusCode());
			Map<Integer, String> filePaths = new HashMap<Integer, String>();
			for (MetaDataModel model : modelList) {
				// f_path + "/"+ office_no + file_num + appl + ".vox"
				filePaths.put(
						model.getMd_id(),
						model.getFilePath() + File.separator
								+ model.getOfficeNo() + model.getFileNumber()
								+ model.getAapl() + ".vox");
				System.out.println(model.getFilePath() + File.separator
						+ model.getOfficeNo() + model.getFileNumber()
						+ model.getAapl() + ".vox");

			}
			scpFiles(filePaths, configuration);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Downloads the audio files from a remote server onto the local machine in
	 * the specified directory. The process looks at the metadata table and
	 * selects entries which have rec_status of 1 or 2.
	 * 
	 * @param filePaths
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void scpFiles(Map<Integer, String> filePaths,
			Configuration configuration) throws IOException {
		SSHClient client = null;
		int fileCounter = 0;
		Map<Integer, String> errorEntries = new HashMap<Integer, String>();
		Map<Integer, String> successEntries = new HashMap<Integer, String>();
		try {
			client = new SSHClient();
			client.addHostKeyVerifier(new PromiscuousVerifier());
			client.connect(InetAddress.getByName(configuration.getSCPConfig()
					.getHostName()));
			client.authPassword(configuration.getSCPConfig().getUserName(),
					configuration.getSCPConfig().getPassword());
			SCPFileTransfer transfer = client.newSCPFileTransfer();

			for (Map.Entry<Integer, String> filePath : filePaths.entrySet()) {
				try {
					ApplicationLauncher.logger.info("Copying file "
							+ filePath
							+ " to destination "
							+ configuration.getSCPConfig()
									.getDestinationFolder());
					transfer.download(filePath.getValue(), configuration
							.getSCPConfig().getDestinationFolder());
					
					fileCounter++;
					successEntries.put(filePath.getKey(), filePath.getValue());
				} catch (Exception e) {
					ApplicationLauncher.logger.severe("Problem occured during scp of file "
							+ filePath.getValue());
					errorEntries.put(filePath.getKey(), filePath.getValue());
				}
			}
		} catch (Exception e) {
			ApplicationLauncher.logger.severe("Environment: "+configuration.getEnvironment()+"Problem during SCP of file. Error -" +e.getMessage());
			EmailHandler.emailEvent("Problem occured during SCP of file", "Re["+ configuration.getEnvironment() + "]: Problem occured during scp task.");
		} finally {
			ApplicationLauncher.logger.info("Environment ["+ configuration.getEnvironment()+ "]scp Copied " + fileCounter + " files.");
			client.disconnect();
			
		}

		StringBuilder sb = new StringBuilder();
		for(String error : errorEntries.values()) {
			sb.append(error + " \n");
		}
		EmailHandler.emailEvent(sb.toString(), "Re["+configuration.getEnvironment() +"]: Problem occured in SCP copy");
		updateMetaDataStatus(successEntries, errorEntries, configuration);
	}

	
	/**
	 * Update the metadata table entries for files which were successfully
	 * downloaded or failed to be downloaded.
	 * 
	 * @param successEntries
	 * @param errorEntries
	 */
	private void updateMetaDataStatus(Map<Integer, String> successEntries,
			Map<Integer, String> errorEntries, Configuration configuration) {

		MetaDataDao dao = new MetaDataDao(configuration);
		dao.updateMetaDataRecordScpCode(configuration.getMetadataConfig()
				.getScpSuccessCode(), new ArrayList(successEntries.keySet()));
		dao.updateMetaDataRecordScpCode(configuration.getMetadataConfig()
				.getScpFailureCode(), new ArrayList(errorEntries.keySet()));
	}
}
