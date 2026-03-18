# Leave Approval Workflow (Flowable + MongoDB)

This Spring Boot project implements an employee leave approval workflow using Flowable for process management and MongoDB for leave request persistence.

## Requirements
- Java 17
- Maven 3.9+

## Environment variables
Set the MongoDB connection environment variables before running:

```
export MONGODB_URI="<<MONGODB CONNECTION STRING GOES HERE>>"
export MONGODB_DATABASE="<<MONGO_DB>>"
```

## Run the application
```
gradle bootRun
```

Note: The Maven pom.xml file is deprecated. Use Gradle for builds.

## API endpoints
- `POST /api/leaves` submit a leave request
- `GET /api/leaves` list leave requests
- `GET /api/leaves/tasks` list pending manager approval tasks
- `POST /api/leaves/approve` approve a task
- `POST /api/leaves/reject` reject a task

### Example request payloads
Submit leave:
```
{
  "employeeId": "E-1001",
  "startDate": "2026-02-01",
  "endDate": "2026-02-03",
  "reason": "Vacation"
}
```

Approve/reject:
```
{
  "taskId": "<task-id>",
  "comment": "Looks good"
}
```

## Notes
- Flowable stores process/runtime data in the embedded H2 database by default. Leave requests are stored in MongoDB configured by the environment variables above.
