package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.*;
import enable.enableexecutionservice.Repository_Abstraction.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExecutionEngineServiceHelper {
    // TODO: remove for production
    private static final Logger logger = LoggerFactory.getLogger(ExecutionEngineServiceHelper.class);

    @Autowired
    private DtoFilterHelper dtoFilterHelper;

    @Autowired
    private TaskRepository taskRepository;

    public List<TaskDto> concludeTask(TaskDto task, Integer status, Long userId, ProcessFileDto processFile, List<TaskDto> runningTasksInExecution) {
        // Check parameters
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (processFile == null) {
            throw new IllegalArgumentException("processFile cannot be null");
        }
        if (processFile.getId() == null) {
            throw new IllegalArgumentException("id of processFile cannot be null");
        }
        if (processFile.getConnections() == null) {
            throw new IllegalArgumentException("connections of processFile with id:" + processFile.getId() + ", cannot be null");
        }
        if (runningTasksInExecution == null) {
            throw new IllegalArgumentException("runningTasksInExecution cannot be null");
        }

        // Check status
        if (task.getStatus() != 0) {
            throw new IllegalArgumentException("Task is already concluded. Can only conclude task with status 0 (Running).");
        }

        // Create result
        List<TaskDto> result = new ArrayList<>();

        // Conclude task
        task.setStatus(status);
        task.setConcludedByUserId(userId);
        result.add(task);

        logger.info("concludeTask > result size after adding task to conclude: {}", result.size());

        // If status is Completed or Terminated, then conclude parents and add to start of result
        if (status == 1 || status == 4) {
            result.addAll(0, this.concludeParents(task, status, userId, runningTasksInExecution, processFile.getConnections()));
            logger.info("concludeTask > result size after adding result of conclude parents: {}", result.size());
        }

        logger.info("concludeTask > runningTasksInExecution size before removing result: {}", runningTasksInExecution.size());
        // Remove concluded tasks from running tasks
        // Iterate over the result list
        Iterator<TaskDto> iterator = runningTasksInExecution.iterator();
        while (iterator.hasNext()) {
            TaskDto runningTask = iterator.next();

            for (TaskDto concludedTask : result) {
                if (runningTask.getId().equals(concludedTask.getId())) {
                    iterator.remove();
                }
            }
        }
        logger.info("concludeTask > runningTasksInExecution size after removing result: {}", runningTasksInExecution.size());

        // Conclude children by moving back down again from the highest parent
        /// If status is Completed set all to Skipped, otherwise to status value (Interrupted or Terminated)
        Integer childrenStatus = status;

        if (status == 1) {
            childrenStatus = 3;
        }

        List<TaskDto> concludedChildren = this.concludeChildren(result.get(0).getId(), childrenStatus, userId, runningTasksInExecution);
        result.addAll(concludedChildren);
        logger.info("concludeTask > result size after adding result of concludeChildren: {}", result.size());

        return result;
    }

    // Either with completed or terminated. Ordered from highest to lowest parent
    private List<TaskDto> concludeParents(TaskDto task, Integer status, Long userId, List<TaskDto> runningTasksInExecution, List<ConnectionDto> connections) {
        // Check parameters
        if (task == null) {
            throw new IllegalArgumentException("task cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (runningTasksInExecution == null) {
            throw new IllegalArgumentException("runningTasksInExecution cannot be null");
        }
        if (connections == null) {
            throw new IllegalArgumentException("connections cannot be null");
        }

        List<TaskDto> result = new ArrayList<>();

        Long childProcessIdToParse = task.getProcessId();

        logger.info("concludeTask > concludeParents > runningTasksInExecution size before parsing: {}", runningTasksInExecution.size());

        while (childProcessIdToParse != null) {
            logger.info("concludeTask > concludeParents > childProcessIdToParse: {}", childProcessIdToParse);

            ConnectionDto connectionFilter = new ConnectionDto();
            connectionFilter.setIsParentConnection(true);
            connectionFilter.setOriginProcessId(childProcessIdToParse);
            Long finalChildProcessIdToParse = childProcessIdToParse;
            List<ConnectionDto> parentConnectionsFromChildTask = dtoFilterHelper.filterConnections(connections, connectionFilter)
                    .stream().filter(connection -> !Objects.equals(connection.getParentProcessId(), finalChildProcessIdToParse)).toList();

            logger.info("concludeTask > concludeParents > parentConnectionsFromChildTask size: {}", parentConnectionsFromChildTask.size());

            // If connections exist, then it's the end of the parent process
            if(parentConnectionsFromChildTask.size() > 0) {
                logger.info("concludeTask > concludeParents > parentConnectionsFromChildTask first item id: {}", parentConnectionsFromChildTask.get(0).getId());
                logger.info("concludeTask > concludeParents > parentConnectionsFromChildTask first item parentProcessId: {}", parentConnectionsFromChildTask.get(0).getParentProcessId());
                logger.info("concludeTask > concludeParents > parentConnectionsFromChildTask first item originProcessId: {}", parentConnectionsFromChildTask.get(0).getOriginProcessId());
                logger.info("concludeTask > concludeParents > parentConnectionsFromChildTask first item destinationProcessId: {}", parentConnectionsFromChildTask.get(0).getDestinationProcessId());

                TaskDto taskFilter = new TaskDto();
                taskFilter.setProcessId(parentConnectionsFromChildTask.get(0).getDestinationProcessId());
                TaskDto parentTaskToConclude = dtoFilterHelper.filterTasks(runningTasksInExecution, taskFilter).get(0);

                logger.info("concludeTask > concludeParents > concluded parent task (parentTaskToConclude) id: {}", parentTaskToConclude.getId());

                // Get and change task
                parentTaskToConclude.setStatus(status);
                parentTaskToConclude.setConcludedByUserId(userId);

                // Add task to result
                result.add(0, parentTaskToConclude);

                // Add id of task to parse next
                childProcessIdToParse = parentTaskToConclude.getId();
            }
            else {
                childProcessIdToParse = null;
            }
        }
        return result;
    }

    private List<TaskDto> concludeChildren(Long taskId, Integer status, Long userId, List<TaskDto> runningTasksInExecution) {
        // Check parameters
        if (taskId == null) {
            throw new IllegalArgumentException("taskId cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (runningTasksInExecution == null) {
            throw new IllegalArgumentException("runningTasksInExecution cannot be null");
        }

        List<TaskDto> result = new ArrayList<>();

        ArrayList<Long> parentIdsToParse = new ArrayList<>();
        parentIdsToParse.add(taskId);

        logger.info("concludeTask > concludeChildren > taskId: {}", taskId);
        logger.info("concludeTask > concludeChildren > parentIdsToParse size after adding taskId: {}", parentIdsToParse.size());

        while (!parentIdsToParse.isEmpty()) {
            logger.info("concludeTask > concludeChildren > parentIdToParse: {}", parentIdsToParse.get(0));

            TaskDto filter = new TaskDto();
            filter.setStatus(0);
            filter.setParentTaskId(parentIdsToParse.get(0));
            List<TaskDto> runningChildTasks = dtoFilterHelper.filterTasks(runningTasksInExecution, filter);

            for (TaskDto childTask : runningChildTasks) {
                // Get and change task
                childTask.setStatus(status);
                childTask.setConcludedByUserId(userId);

                // Add task to result
                result.add(childTask);
                logger.info("concludeTask > concludeChildren > added childTask with id: {}", childTask.getId());
                logger.info("concludeTask > concludeChildren > result size after adding childTask: {}", result.size());

                // Add id of task to parse next
                parentIdsToParse.add(childTask.getId());
                logger.info("concludeTask > concludeChildren > parentIdsToParse size after adding childTask id: {}", parentIdsToParse.size());
            }

            parentIdsToParse.remove(0);
            logger.info("concludeTask > concludeChildren > parentIdsToParse size after removing first id: {}", parentIdsToParse.size());
        }

        return result;
    }


    public List<TaskDto> createNextTasks(TaskDto previousTask, ProcessFileDto processFile) {
        // Check parameters
        if (previousTask == null) {
            throw new IllegalArgumentException("previousTask cannot be null");
        }
        if (previousTask.getProcessId() == null) {
            throw new IllegalArgumentException("processId of previousTask with id:" + previousTask.getId() + ", cannot be null");
        }
        if (previousTask.getExecutionId() == null) {
            throw new IllegalArgumentException("executionId of previousTask with id:" + previousTask.getId() + ", cannot be null");
        }
        if (previousTask.getParentTaskId() == null) {
            throw new IllegalArgumentException("parentTaskId of previousTask with id:" + previousTask.getId() + ", cannot be null");
        }
        if (processFile == null) {
            throw new IllegalArgumentException("processFile cannot be null");
        }
        if (processFile.getId() == null) {
            throw new IllegalArgumentException("id of processFile cannot be null");
        }

        logger.info("createNextTasks > previousTask id: {}", previousTask.getId());

        // Start by getting followup processes
        List<ProcessDto> followupProcesses = this.extractFollowupProcesses(processFile, previousTask.getProcessId());

        logger.info("createNextTasks > followupProcesses size after extracting: {}", followupProcesses.size());

        // Turn followup processes into tasks and add to list for tasks to parse
        List<TaskDto> tasksToParse = new ArrayList<>();

        for (ProcessDto followupProcess : followupProcesses) {
            TaskDto followupTask = new TaskDto();
            followupTask.setExecutionId(previousTask.getExecutionId());
            followupTask.setProcessFileId(processFile.getId());
            followupTask.setProcessId(followupProcess.getId());
            followupTask.setParentTaskId(previousTask.getParentTaskId());
            followupTask.setStatus(0); // Running
            followupTask.setCreatedDateTime(new Date());

            // Save and added task with id to tasksToParse
            followupTask = new TaskDto(taskRepository.save(followupTask.toTask()));
            tasksToParse.add(followupTask);

            logger.info("createNextTasks > added followupTask with id: {}", followupTask.getId());
            logger.info("createNextTasks > followupTask has process: {}", followupProcess.getName());
        }
        logger.info("createNextTasks > tasksToParse size after adding followupProcesses: {}", tasksToParse.size());

        // Parse tasks and all children
        return parseAndCreateTasksAndChildren(processFile, tasksToParse);
    }

    private List<TaskDto> parseAndCreateTasksAndChildren(ProcessFileDto processFile, List<TaskDto> tasksToParse) {
        // Check parameters
        if (processFile == null) {
            throw new IllegalArgumentException("processFile cannot be null");
        }
        if (processFile.getId() == null) {
            throw new IllegalArgumentException("id of processFile cannot be null");
        }
        if (tasksToParse == null) {
            throw new IllegalArgumentException("tasksToParse cannot be null");
        }

        List<TaskDto> result = new ArrayList<>();

        logger.info("createNextTasks > parseAndCreateTasksAndChildren > tasksToParse size before parsing: {}", tasksToParse.size());

        while (tasksToParse.size() > 0) {
            TaskDto taskToParse = tasksToParse.get(0);
            result.add(taskToParse);
            tasksToParse.remove(0);

            logger.info("createNextTasks > parseAndCreateTasksAndChildren > taskToParse: {}", taskToParse.getId());

            List<ProcessDto> childStartProcesses = this.extractChildStartProcesses(processFile, taskToParse.getProcessId());

            logger.info("createNextTasks > parseAndCreateTasksAndChildren > childStartProcesses size after extracting: {}", childStartProcesses.size());

            for (ProcessDto childProcess : childStartProcesses) {
                TaskDto childTask = new TaskDto();
                childTask.setExecutionId(taskToParse.getExecutionId());
                childTask.setProcessFileId(processFile.getId());
                childTask.setProcessId(childProcess.getId());
                childTask.setParentTaskId(taskToParse.getId());
                childTask.setStatus(0); // Running
                childTask.setCreatedDateTime(new Date());

                // Save and add task with id to tasksToParse
                childTask = new TaskDto(taskRepository.save(childTask.toTask()));

                logger.info("createNextTasks > parseAndCreateTasksAndChildren > Added child task with processId: {}", childTask.getProcessId());

                tasksToParse.add(childTask);
            }
        }
        return result;
    }

    private List<ProcessDto> extractFollowupProcesses(ProcessFileDto processFile, Long previousProcessId) {
        // Check parameters
        if (processFile == null) {
            throw new IllegalArgumentException("processFile cannot be null");
        }
        if (processFile.getId() == null) {
            throw new IllegalArgumentException("id of processFile cannot be null");
        }
        if (processFile.getConnections() == null) {
            throw new IllegalArgumentException("connections of processFile with id:" + processFile.getId() + ", cannot be null");
        }
        if (processFile.getProcesses() == null) {
            throw new IllegalArgumentException("processes of processFile with id:" + processFile.getId() + ", cannot be null");
        }
        if (previousProcessId == null) {
            throw new IllegalArgumentException("tasksToParse cannot be null");
        }

        List<ProcessDto> result = new ArrayList<>();

        logger.info("createNextTasks > extractFollowupProcesses > previousProcessId: {}", previousProcessId);

        // Find connections where isParentConnection == false and originProcessId == previousProcessId
        ConnectionDto connectionFilter = new ConnectionDto();
        connectionFilter.setIsParentConnection(false);
        connectionFilter.setOriginProcessId(previousProcessId);
        List<ConnectionDto> relevantConnections = dtoFilterHelper.filterConnections(processFile.getConnections(), connectionFilter);

        for (ConnectionDto connection : relevantConnections) {
            ProcessDto processFilter = new ProcessDto();
            processFilter.setId(connection.getDestinationProcessId());
            ProcessDto processToAdd = dtoFilterHelper.filterProcesses(processFile.getProcesses(), processFilter).get(0);
            result.add(processToAdd);

            logger.info("createNextTasks > extractFollowupProcesses > added process: {}", processToAdd.getName());
        }

        logger.info("createNextTasks > extractFollowupProcesses > result size after adding processes: {}", result.size());
        return result;
    }

    public List<TaskDto> extractExecutionStartTasks(ExecutionDto executionDto, ProcessFileDto processFile) {
        // Check parameters
        if (executionDto == null) {
            throw new IllegalArgumentException("executionDto cannot be null");
        }
        if (executionDto.getId() == null) {
            throw new IllegalArgumentException("id of executionDto cannot be null");
        }
        if (processFile == null) {
            throw new IllegalArgumentException("processFile cannot be null");
        }
        if (processFile.getId() == null) {
            throw new IllegalArgumentException("id of processFile cannot be null");
        }
        if (processFile.getMainProcessId() == null) {
            throw new IllegalArgumentException("mainProcessId of processFile with id:" + processFile.getId() + ", cannot be null");
        }

        // Turn main process into task and add to list for tasks to parse
        TaskDto mainTask = new TaskDto();
        mainTask.setExecutionId(executionDto.getId());
        mainTask.setProcessFileId(processFile.getId());
        mainTask.setProcessId(processFile.getMainProcessId());
        mainTask.setStatus(0); // Running
        mainTask.setCreatedDateTime(new Date());
        // Save and add task with id to tasksToParse
        mainTask = new TaskDto(taskRepository.save(mainTask.toTask()));

        List<TaskDto> tasksToParse = new ArrayList<>();
        tasksToParse.add(mainTask);

        // Parse tasks and all children
        return parseAndCreateTasksAndChildren(processFile, tasksToParse);
    }

    private List<ProcessDto> extractChildStartProcesses(ProcessFileDto processFile, Long parentProcessId) {
        // Check parameters
        if (processFile == null) {
            throw new IllegalArgumentException("processFile cannot be null");
        }
        if (processFile.getId() == null) {
            throw new IllegalArgumentException("id of processFile cannot be null");
        }
        if (processFile.getConnections() == null) {
            throw new IllegalArgumentException("connections of processFile with id:" + processFile.getId() + ", cannot be null");
        }
        if (processFile.getProcesses() == null) {
            throw new IllegalArgumentException("processes of processFile with id:" + processFile.getId() + ", cannot be null");
        }
        if (parentProcessId == null) {
            throw new IllegalArgumentException("tasksToParse cannot be null");
        }

        List<ProcessDto> result = new ArrayList<>();

        logger.info("createNextTasks > parseAndCreateTasksAndChildren > extractChildStartProcesses > parentProcessId: {}", parentProcessId);

        // Find connections where isParentConnection == true and originProcessId == parentProcessId
        ConnectionDto connectionFilter = new ConnectionDto();
        connectionFilter.setIsParentConnection(true);
        connectionFilter.setOriginProcessId(parentProcessId);
        connectionFilter.setParentProcessId(parentProcessId);
        List<ConnectionDto> relevantConnections = dtoFilterHelper.filterConnections(processFile.getConnections(), connectionFilter);

        for (ConnectionDto connection : relevantConnections) {
            ProcessDto processFilter = new ProcessDto();
            processFilter.setId(connection.getDestinationProcessId());
            ProcessDto processToAdd = dtoFilterHelper.filterProcesses(processFile.getProcesses(), processFilter).get(0);
            result.add(processToAdd);
            logger.info("createNextTasks > parseAndCreateTasksAndChildren > extractChildStartProcesses > added process with name: {}", processToAdd.getName());
        }

        return result;
    }
}
