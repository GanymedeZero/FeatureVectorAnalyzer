package com.rankminer.featurevectoranalyzer.commands.dci;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.FeatureVectorConverter;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;

public class TranslateFVToCsvTask implements TaskInterface {

	@Override
	public void executeCommand(String[] args, Configuration configuration) {
		FeatureVectorConverter converter = new FeatureVectorConverter(configuration);
		if(args.length < 3) {
			ApplicationLauncher.logger.severe("Insufficient arguments passed to the TranslateFVToCsv task");
			return;
		}
		converter.convertFeatureVectorXmlToCsv(args[1], args[2]);
	}

}
