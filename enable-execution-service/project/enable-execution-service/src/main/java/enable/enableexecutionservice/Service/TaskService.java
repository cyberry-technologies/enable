package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.GetTasksRequestBody;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.Task;
import enable.enableexecutionservice.Repository_Abstraction.TaskRepository;
import enable.enableexecutionservice.Service_Abstraction.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final ProcessFileHelper processFileHelper;
    private final ExecutionEngineServiceHelper executionEngineServiceHelper;

    public TaskService(TaskRepository taskRepository, ProcessFileHelper processFileHelper, ExecutionEngineServiceHelper executionEngineServiceHelper) {
        this.taskRepository = taskRepository;
        this.processFileHelper = processFileHelper;
        this.executionEngineServiceHelper = executionEngineServiceHelper;
    }

    @Override
    public List<TaskDto> getTasksOfExecution(Long executionId) {
        // Check parameters
        if (executionId == null) {
            throw new IllegalArgumentException("executionId cannot be null");
        }

        List<TaskDto> result = taskRepository.findAll().stream()
                .filter(task -> Objects.equals(task.getExecutionId(), executionId))
                .map(TaskDto::new).toList();

        return processFileHelper.includeProcessForTaskList(result);
    }

    /*
    private Boolean formatAsTaskTree;
    private Boolean formatWithProcesses;
    private Boolean formatWithParticipants;

    private Long id;
    private Integer status; // default=all, 0=running, 1=completed, 2=interrupted, 3=skipped, 4=terminated
    private Long executionId;
    private Long parentTaskId;
    private Long creatorId;
    private Long claimedByUserId;
    private Integer processType; // default=all, 0=standard, 1=human, 2=code, 3=external
    */
    @Override
    public List<TaskDto> getTasks(GetTasksRequestBody requestBody) {
        Stream<Task> stream = taskRepository.findAll().stream();

        if (requestBody.getId() != null) {
            stream = stream.filter(task -> (Objects.equals(task.getId(), requestBody.getId())));
        }
        if (requestBody.getStatus() != null) {
            stream = stream.filter(task -> (Objects.equals(task.getStatus(), requestBody.getStatus())));
        }
        if (requestBody.getExecutionId() != null) {
            stream = stream.filter(task -> (Objects.equals(task.getExecutionId(), requestBody.getExecutionId())));
        }
        if (requestBody.getParentTaskId() != null) {
            stream = stream.filter(task -> (Objects.equals(task.getParentTaskId(), requestBody.getParentTaskId())));
        }
        if (requestBody.getClaimedByUserId() != null) {
            stream = stream.filter(task -> (Objects.equals(task.getParentTaskId(), requestBody.getClaimedByUserId())));
        }

        List<TaskDto> list = stream.map(TaskDto::new).toList();

        if (list.size() > 0) {
            if (requestBody.getProcessType() != null || (requestBody.getFormatWithProcesses() != null && requestBody.getFormatWithProcesses())) {
                list = processFileHelper.includeProcessForTaskList(list);

                if (requestBody.getProcessType() != null) {
                    list.removeIf(task -> !Objects.equals(task.getProcess().getType(), requestBody.getProcessType()));
                }
            }
            if (requestBody.getFormatWithParticipants() != null && requestBody.getFormatWithParticipants()) {
                throw new IllegalArgumentException();
            }
            if (requestBody.getFormatAsTaskTree() != null && requestBody.getFormatAsTaskTree()) {
                list = this.buildTaskTree(list);
            }
        }
        return list;
    }

    private List<TaskDto> buildTaskTree(List<TaskDto> tasks) {
        List<TaskDto> topLayerTasks = new ArrayList<>();

        for (TaskDto task : tasks) {
            if (findTaskById(task.getParentTaskId(), tasks) == null) {
                topLayerTasks.add(task);
            }
        }

        for (TaskDto task : topLayerTasks) {
            task.includeAllSubtasksFromTaskList(tasks);
        }

        return topLayerTasks;
    }

    private TaskDto findTaskById(Long id, List<TaskDto> tasks) {
        for (TaskDto task : tasks) {
            if (Objects.equals(task.getId(), id)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public TaskDto getTask(Long id) {
        // Check parameters
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        TaskDto result = new TaskDto(taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id)));

        return processFileHelper.includeProcessForTask(result);
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
        List<TaskDto> runningTasks = taskRepository.findAll().stream()
                .filter(task ->
                        Objects.equals(task.getExecutionId(), taskToConclude.getExecutionId())
                        && task.getStatus() == 0)
                .map(TaskDto::new).toList();

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
