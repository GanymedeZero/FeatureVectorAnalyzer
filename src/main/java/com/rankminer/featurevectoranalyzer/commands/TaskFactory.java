package com.rankminer.featurevectoranalyzer.commands;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher.TaskType;

public interface TaskFactory {
	
	TaskInterface createTask(TaskType type);

}
