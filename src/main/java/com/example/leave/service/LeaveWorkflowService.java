package com.example.leave.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.leave.api.LeaveDecision;
import com.example.leave.api.LeaveRequestCreate;
import com.example.leave.model.LeaveRequest;
import com.example.leave.model.LeaveStatus;
import com.example.leave.repo.LeaveRequestRepository;

@Service
public class LeaveWorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final LeaveRequestRepository repository;

    public LeaveWorkflowService(RuntimeService runtimeService,
                                TaskService taskService,
                                LeaveRequestRepository repository) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repository = repository;
    }

    @Transactional
    public LeaveRequest submitLeave(LeaveRequestCreate request) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployeeId(request.getEmployeeId());
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setStatus(LeaveStatus.SUBMITTED);
        leaveRequest = repository.save(leaveRequest);

        Map<String, Object> variables = new HashMap<>();
        variables.put("leaveRequestId", leaveRequest.getId());
        variables.put("employeeId", leaveRequest.getEmployeeId());
        variables.put("reason", leaveRequest.getReason());

        ProcessInstance instance = runtimeService.startProcessInstanceByKey("leaveApproval", variables);
        leaveRequest.setProcessInstanceId(instance.getId());
        return repository.save(leaveRequest);
    }

    public List<Task> getPendingApprovalTasks() {
        return taskService.createTaskQuery()
                .processDefinitionKey("leaveApproval")
                .taskCandidateGroup("managers")
                .list();
    }

    @Transactional
    public LeaveRequest approveLeave(LeaveDecision decision) {
        Task task = taskService.createTaskQuery().taskId(decision.getTaskId()).singleResult();
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", true);
        if (decision.getComment() != null && !decision.getComment().isBlank()) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), decision.getComment());
        }

        taskService.complete(task.getId(), variables);
        LeaveRequest leaveRequest = repository.findByProcessInstanceId(task.getProcessInstanceId())
            .orElseThrow(() -> new IllegalStateException("Leave request not found"));
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        return repository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest rejectLeave(LeaveDecision decision) {
        Task task = taskService.createTaskQuery().taskId(decision.getTaskId()).singleResult();
        if (task == null) {
            throw new IllegalArgumentException("Task not found");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", false);
        if (decision.getComment() != null && !decision.getComment().isBlank()) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), decision.getComment());
        }

        taskService.complete(task.getId(), variables);
        LeaveRequest leaveRequest = repository.findByProcessInstanceId(task.getProcessInstanceId())
            .orElseThrow(() -> new IllegalStateException("Leave request not found"));
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        return repository.save(leaveRequest);
    }
}
