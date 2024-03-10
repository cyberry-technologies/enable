package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.Task;
import enable.enableexecutionservice.Repository_Abstraction.TaskRepository;
import enable.enableexecutionservice.Service_Abstraction.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProcessFileHelper processFileHelper;

    @Autowired
    private ExecutionEngineServiceHelper executionEngineServiceHelper;

    @Override
    public List<TaskDto> getRunningTasks(Long executionId) {
        return taskRepository.findByExecutionId(executionId).stream()
                .filter(task -> task.getStatus() == 0) // Only get running tasks
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasks(Long executionId) {
        return taskRepository.findByExecutionId(executionId).stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto getTask(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            return new TaskDto(optionalTask.get());
        } else {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
    }

    @Override
    public TaskDto includeProcess(TaskDto taskDto) {
        taskDto.setProcess(processFileHelper.getProcessById(taskDto.getProcessFileId(), taskDto.getProcessId()));
        return taskDto;
    }

    @Override
    public TaskDto completeTask(Long id, Long userId) {
        return executionEngineServiceHelper.completeTask(id, userId, 1);
    }

    @Override
    public TaskDto interruptTask(Long id, Long userId) {
        return executionEngineServiceHelper.completeTask(id, userId, 2);
    }

    @Override
    public TaskDto terminateTask(Long id, Long userId) {
        return executionEngineServiceHelper.completeTask(id, userId, 3);
    }

    @Override
    public TaskDto claimTask(Long id, Long userId) {
        TaskDto taskDto = getTask(id);
        taskDto.setClaimedByUserId(userId);
        Task result = taskRepository.save(taskDto.toTask());
        return new TaskDto(result);
    }
}
