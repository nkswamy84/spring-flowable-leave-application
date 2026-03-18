package com.example.leave.api;

public class TaskSummary {

    private String id;
    private String name;
    private String processInstanceId;

    public TaskSummary(String id, String name, String processInstanceId) {
        this.id = id;
        this.name = name;
        this.processInstanceId = processInstanceId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }
}
