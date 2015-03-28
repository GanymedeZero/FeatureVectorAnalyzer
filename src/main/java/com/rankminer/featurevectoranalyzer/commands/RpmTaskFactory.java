package com.rankminer.featurevectoranalyzer.commands;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher.TaskType;
import com.rankminer.featurevectoranalyzer.commands.rpm.DailyGenTask;
import com.rankminer.featurevectoranalyzer.commands.rpm.CopyFileTask;
import com.rankminer.featurevectoranalyzer.commands.rpm.ExportMetaDataTask;
import com.rankminer.featurevectoranalyzer.commands.rpm.ExtractFeatureVectorTask;
import com.rankminer.featurevectoranalyzer.commands.rpm.ScpTask;
import com.rankminer.featurevectoranalyzer.commands.rpm.TranslateFVToCsvTask;

public class RpmTaskFactory implements TaskFactory {

	public TaskInterface createTask(TaskType type) {
		switch (type) {
			case EXPORTMETADATA:
				return new ExportMetaDataTask();
	
			case SCPCOPY:
				return new ScpTask();
	
			case EXTRACTFV:
				return new ExtractFeatureVectorTask();
	
			case TRANSLATEXMLTOCSV:
				return new TranslateFVToCsvTask();
			
			case COPYFILE:
				return new CopyFileTask();
				
			case DAILYGEN:
				return new DailyGenTask();
			default:
				break;
		}
		return null;
	}

}
