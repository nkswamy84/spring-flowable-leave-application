package com.example.leave.api;

import jakarta.validation.constraints.NotBlank;

public class LeaveDecision {

    @NotBlank
    private String taskId;

    private String comment;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
