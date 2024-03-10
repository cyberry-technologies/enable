package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.ProcessDto;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.Task;
import enable.enableexecutionservice.Repository_Abstraction.TaskRepository;
import enable.enableexecutionservice.Service_Abstraction.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProcessFileHelper processFileHelper;

    @Autowired
    private ExecutionEngineServiceHelper executionEngineServiceHelper;

    @Autowired
    private DtoFilterHelper dtoFilterHelper;

    @Override
    public List<TaskDto> getRunningTasksOfExecution(Long executionId) {
        List<TaskDto> result = taskRepository.findByExecutionId(executionId).stream()
                .filter(task -> task.getStatus() == 0) // Only get running tasks
                .map(TaskDto::new).toList();

        ProcessFileDto processFile = processFileHelper.getProcessFileById(result.get(0).getProcessFileId());

        result = this.includeProcessForTaskList(processFile, result);

        return result;
    }

    @Override
    public List<TaskDto> getTasksOfExecution(Long executionId) {
        List<TaskDto> result = taskRepository.findByExecutionId(executionId).stream()
                .map(TaskDto::new)
                .collect(Collectors.toList());

        ProcessFileDto processFile = processFileHelper.getProcessFileById(result.get(0).getProcessFileId());

        return this.includeProcessForTaskList(processFile, result);
    }

    @Override
    public TaskDto getTask(Long id) {
        TaskDto result = new TaskDto(taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id)));

        ProcessFileDto processFile = processFileHelper.getProcessFileById(result.getProcessFileId());

        return this.includeProcessForTask(processFile, result);
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
        TaskDto taskDto = getTask(id);
        taskDto.setClaimedByUserId(userId);
        Task result = taskRepository.save(taskDto.toTask());
        return new TaskDto(result);
    }

    private TaskDto includeProcessForTask(ProcessFileDto processFile, TaskDto task) {
        ProcessDto filter = new ProcessDto();
        filter.setId(task.getProcessId());
        List<ProcessDto> foundProcesses = dtoFilterHelper.filterProcesses(processFile.getProcesses(), filter);

        task.setProcess(foundProcesses.get(0));
        return task;
    }

    private List<TaskDto> includeProcessForTaskList(ProcessFileDto processFile, List<TaskDto> list) {
        List<TaskDto> result = new ArrayList<>();

        for (TaskDto task: list) {
            result.add(this.includeProcessForTask(processFile, task));
        }

        return result;
    }

    private TaskDto concludeTask(Long id, Long userId, int status) {
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

        List<Task> extractedTasks = executionEngineServiceHelper
                .extractNextTasks(
                        highestParentTask,
                        processFileDto)
                .stream().map(TaskDto::toTask).toList();
        taskRepository.saveAll(extractedTasks);

        return getTask(id);
    }
}
