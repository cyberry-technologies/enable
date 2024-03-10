package enable.enableexecutionservice.Service_Abstraction;

import enable.enableexecutionservice.Dto.TaskDto;

import java.util.List;

public interface ITaskService {
    List<TaskDto> getRunningTasks(Long executionId);

    List<TaskDto> getTasks (Long executionId);

    TaskDto getTask(Long id);

    TaskDto includeProcess(TaskDto taskDto);

    TaskDto completeTask(Long id, Long userId);

    TaskDto interruptTask(Long id, Long userId);

    TaskDto terminateTask(Long id, Long userId);

    TaskDto claimTask(Long id, Long userId);
}