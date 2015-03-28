package com.rankminer.featurevectoranalyzer.commands;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher.TaskType;
import com.rankminer.featurevectoranalyzer.commands.dci.CopyFileTask;
import com.rankminer.featurevectoranalyzer.commands.dci.DailyGenTask;
import com.rankminer.featurevectoranalyzer.commands.dci.ExportMetaDataTask;
import com.rankminer.featurevectoranalyzer.commands.dci.ExtractFeatureVectorTask;
import com.rankminer.featurevectoranalyzer.commands.dci.ScpTask;
import com.rankminer.featurevectoranalyzer.commands.dci.TranslateFVToCsvTask;

public class DciTaskFactory implements  TaskFactory {

	@Override
	public TaskInterface createTask(TaskType type) {
		switch(type) {
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
