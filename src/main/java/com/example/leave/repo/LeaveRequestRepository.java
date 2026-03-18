package com.example.leave.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import com.example.leave.model.LeaveRequest;

public interface LeaveRequestRepository extends MongoRepository<LeaveRequest, String> {
	Optional<LeaveRequest> findByProcessInstanceId(String processInstanceId);
}
