package enable.enableexecutionservice.Service_Abstraction;

import enable.enableexecutionservice.Dto.TaskDto;

import java.util.List;

public interface ITaskService {
    List<TaskDto> getRunningTasksOfExecution(Long executionId);

    List<TaskDto> getTasksOfExecution(Long executionId);

    TaskDto getTask(Long id);

    TaskDto completeTask(Long id, Long userId);

    TaskDto interruptTask(Long id, Long userId);

    TaskDto terminateTask(Long id, Long userId);

    TaskDto claimTask(Long id, Long userId);
}