package dev.pulceo.pna.model;

import dev.pulceo.pna.model.tasks.JobState;
import dev.pulceo.pna.model.tasks.Task;
import dev.pulceo.pna.model.tasks.TaskType;

public class Job {

    private String createdAt;
    private TaskType taskType;
    private int recurrenceInMinutes;
    private JobState jobState;
    private Task task;


}
