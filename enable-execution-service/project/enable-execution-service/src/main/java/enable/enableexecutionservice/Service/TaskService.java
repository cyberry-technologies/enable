package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.ExecutionDto;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.Task;
import enable.enableexecutionservice.Repository_Abstraction.ExecutionRepository;
import enable.enableexecutionservice.Repository_Abstraction.TaskRepository;
import enable.enableexecutionservice.Service_Abstraction.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService implements ITaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private ProcessFileHelper processFileHelper;

    @Autowired
    private ExecutionEngineServiceHelper executionEngineServiceHelper;

    @Override
    public List<TaskDto> getRunningTasksOfExecution(Long executionId) {
        // Check parameters
        if (executionId == null) {
            throw new IllegalArgumentException("executionId cannot be null");
        }

        // Find execution
        ExecutionDto executionDto = new ExecutionDto(executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found with ID: " + executionId)));

        List<TaskDto> result = taskRepository.findAll().stream()
                .filter(task -> (Objects.equals(task.getExecutionId(), executionId)) && (task.getStatus() == 0)) // Only get running tasks
                .map(TaskDto::new).toList();

        return processFileHelper.includeProcessForTaskList(executionDto.getProcessFileId(), result);
    }

    @Override
    public List<TaskDto> getTasksOfExecution(Long executionId) {
        // Check parameters
        if (executionId == null) {
            throw new IllegalArgumentException("executionId cannot be null");
        }

        // Find execution
        ExecutionDto executionDto = new ExecutionDto(executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found with ID: " + executionId)));

        List<TaskDto> result = taskRepository.findAll().stream()
                .filter(task -> Objects.equals(task.getExecutionId(), executionId))
                .map(TaskDto::new).toList();

        return processFileHelper.includeProcessForTaskList(executionDto.getProcessFileId(), result);
    }

    @Override
    public TaskDto getTask(Long id) {
        // Check parameters
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        TaskDto result = new TaskDto(taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id)));

        return processFileHelper.includeProcessForTask(result.getProcessFileId(), result);
    }



    @Override
    public TaskDto completeTask(Long id, Long userId) {
        return concludeTask(id, userId, 1);
    }

    @Override
    public TaskDto interruptTask(Long id, Long userId) {
        return concludeTask(id, userId, 2);
    }

    @Override
    public TaskDto terminateTask(Long id, Long userId) {
        return concludeTask(id, userId, 4);
    }

    @Override
    public TaskDto claimTask(Long id, Long userId) {
        // Check parameters
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        TaskDto taskDto = getTask(id);
        taskDto.setClaimedByUserId(userId);
        Task result = taskRepository.save(taskDto.toTask());
        return new TaskDto(result);
    }

    private TaskDto concludeTask(Long id, Long userId, int status) {
        // Check parameters
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        TaskDto taskToConclude = this.getTask(id);
        ProcessFileDto processFileDto = processFileHelper.getProcessFileById(taskToConclude.getProcessFileId());
        List<TaskDto> runningTasks = this.getRunningTasksOfExecution(taskToConclude.getExecutionId());

        List<Task> concludedTasks = executionEngineServiceHelper
                .concludeTask(
                        taskToConclude,
                        status,
                        userId,
                        processFileDto,
                        runningTasks)
                .stream().map(TaskDto::toTask).toList();
        taskRepository.saveAll(concludedTasks);

        TaskDto highestParentTask = new TaskDto(concludedTasks.get(0));

        executionEngineServiceHelper.createNextTasks(highestParentTask, processFileDto);

        return getTask(id);
    }
}
