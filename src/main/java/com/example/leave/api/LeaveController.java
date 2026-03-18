package com.example.leave.api;

import java.util.List;

import jakarta.validation.Valid;

import org.flowable.task.api.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave.model.LeaveRequest;
import com.example.leave.repo.LeaveRequestRepository;
import com.example.leave.service.LeaveWorkflowService;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveWorkflowService workflowService;
    private final LeaveRequestRepository repository;

    public LeaveController(LeaveWorkflowService workflowService, LeaveRequestRepository repository) {
        this.workflowService = workflowService;
        this.repository = repository;
    }

    @PostMapping
    public LeaveRequest submitLeave(@Valid @RequestBody LeaveRequestCreate request) {
        return workflowService.submitLeave(request);
    }

    @GetMapping
    public List<LeaveRequest> listLeaves() {
        return repository.findAll();
    }

    @GetMapping("/tasks")
    public List<TaskSummary> listTasks() {
        return workflowService.getPendingApprovalTasks()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @PostMapping("/approve")
    public LeaveRequest approve(@Valid @RequestBody LeaveDecision decision) {
        return workflowService.approveLeave(decision);
    }

    @PostMapping("/reject")
    public LeaveRequest reject(@Valid @RequestBody LeaveDecision decision) {
        return workflowService.rejectLeave(decision);
    }

    private TaskSummary toSummary(Task task) {
        return new TaskSummary(task.getId(), task.getName(), task.getProcessInstanceId());
    }
}
