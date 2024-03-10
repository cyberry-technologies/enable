package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.ConnectionDto;
import enable.enableexecutionservice.Dto.ProcessDto;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExecutionEngineServiceHelper {

    @Autowired
    private DtoFilterHelper dtoFilterHelper;

    public List<TaskDto> concludeTask(TaskDto task, Integer status, Long userId, ProcessFileDto processFile, List<TaskDto> runningTasksInExecution) {
        // Check status
        if (task.getStatus() != 0) {
            throw new IllegalArgumentException("Task is already concluded. Can only conclude task with status 0 (Running).");
        }

        // Create result
        List<TaskDto> result = new ArrayList<>();

        // Complete task
        task.setStatus(status);
        task.setConcludedByUserId(userId);
        result.add(task);

        // If status is Completed or Terminated, then conclude parents and add to start of result
        if (status == 1 || status == 4) {
            result.addAll(0, this.concludeParents(task, status, userId, runningTasksInExecution, processFile.getConnections()));
        }

        // Remove concluded tasks from running tasks
        runningTasksInExecution.removeAll(result);

        // Conclude children by moving back down again from the highest parent
        /// If status is Completed set all to Skipped, otherwise to status value (Interrupted or Terminated)
        Integer childrenStatus = (status != 1) ? status : 3;
        List<TaskDto> concludedChildren = this.concludeChildren(result.get(0).getId(), childrenStatus, userId, runningTasksInExecution);
        result.addAll(concludedChildren);

        return result;
    }

    private List<TaskDto> concludeChildren(Long taskId, Integer status, Long userId, List<TaskDto> runningTasksInExecution) {
        List<TaskDto> result = new ArrayList<>();

        ArrayList<Long> parentIdsToParse = new ArrayList<>();
        parentIdsToParse.add(taskId);

        while (!parentIdsToParse.isEmpty()) {
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

                // Add id of task to parse next
                parentIdsToParse.add(childTask.getId());
            }

            parentIdsToParse.remove(0);
        }

        return result;
    }

    // Either with completed or terminated. Ordered from highest to lowest parent
    private List<TaskDto> concludeParents(TaskDto task, Integer status, Long userId, List<TaskDto> runningTasksInExecution, List<ConnectionDto> connections) {
        List<TaskDto> result = new ArrayList<>();

        Long childTaskIdToParse = task.getId();

        while (childTaskIdToParse != null) {
            ConnectionDto connectionFilter = new ConnectionDto();
            connectionFilter.setIsParentConnection(true);
            connectionFilter.setOriginProcessId(childTaskIdToParse);
            List<ConnectionDto> parentConnectionsFromChildTask = dtoFilterHelper.filterConnections(connections, connectionFilter);

            // If connections exist, then it's the end of the parent process
            if(parentConnectionsFromChildTask.size() > 0) {
                TaskDto taskFilter = new TaskDto();
                taskFilter.setId(task.getParentTaskId());
                TaskDto parentTaskToConclude = dtoFilterHelper.filterTasks(runningTasksInExecution, taskFilter).get(0);

                // Get and change task
                parentTaskToConclude.setStatus(status);
                parentTaskToConclude.setConcludedByUserId(userId);

                // Add task to result
                result.add(0, parentTaskToConclude);

                // Add id of task to parse next
                childTaskIdToParse = parentTaskToConclude.getId();
            }
            else {
                childTaskIdToParse = null;
            }
        }

        return result;
    }


    public List<TaskDto> extractNextTasks(TaskDto previousTask, ProcessFileDto processFile) {
        List<TaskDto> result = new ArrayList<>();

        // Start by getting followup processes
        List<ProcessDto> followupProcesses = this.extractFollowupProcesses(processFile, previousTask.getProcessId());

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

            tasksToParse.add(followupTask);
        }

        // Parse tasks and all children
        while (tasksToParse.size() > 0) {
            TaskDto taskToParse = tasksToParse.get(0);

            List<ProcessDto> childStartProcesses = this.extractChildStartProcesses(processFile, taskToParse.getProcessId());

            for (ProcessDto childProcess : childStartProcesses) {
                TaskDto childTask = new TaskDto();
                childTask.setExecutionId(taskToParse.getExecutionId());
                childTask.setProcessFileId(processFile.getId());
                childTask.setProcessId(childProcess.getId());
                childTask.setParentTaskId(taskToParse.getParentTaskId());
                childTask.setStatus(0); // Running
                childTask.setCreatedDateTime(new Date());

                tasksToParse.add(childTask);
            }

            result.add(taskToParse);
            tasksToParse.remove(0);
        }

        return result;
    }

    private List<ProcessDto> extractFollowupProcesses(ProcessFileDto processFile, Long previousProcessId) {
        List<ProcessDto> result = new ArrayList<>();

        // Find connections where isParentConnection == false and originProcessId == previousProcessId
        ConnectionDto connectionFilter = new ConnectionDto();
        connectionFilter.setIsParentConnection(false);
        connectionFilter.setOriginProcessId(previousProcessId);
        List<ConnectionDto> relevantConnections = dtoFilterHelper.filterConnections(processFile.getConnections(), connectionFilter);

        for (ConnectionDto connection : relevantConnections) {
            ProcessDto processFilter = new ProcessDto();
            processFilter.setId(connection.getDestinationProcessId());
            result.add(dtoFilterHelper.filterProcesses(processFile.getProcesses(), processFilter).get(0));
        }

        return result;
    }

    private List<ProcessDto> extractChildStartProcesses(ProcessFileDto processFile, Long parentProcessId) {
        List<ProcessDto> result = new ArrayList<>();

        // Find connections where isParentConnection == true and originProcessId == parentProcessId
        ConnectionDto connectionFilter = new ConnectionDto();
        connectionFilter.setIsParentConnection(true);
        connectionFilter.setOriginProcessId(parentProcessId);
        List<ConnectionDto> relevantConnections = dtoFilterHelper.filterConnections(processFile.getConnections(), connectionFilter);

        for (ConnectionDto connection : relevantConnections) {
            ProcessDto processFilter = new ProcessDto();
            processFilter.setId(connection.getDestinationProcessId());
            result.add(dtoFilterHelper.filterProcesses(processFile.getProcesses(), processFilter).get(0));
        }

        return result;
    }
}
