package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.ConnectionDto;
import enable.enableexecutionservice.Dto.ProcessDto;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.Execution;
import enable.enableexecutionservice.Model.ProcessFileInstance;
import enable.enableexecutionservice.Model.Task;
import enable.enableexecutionservice.Repository_Abstraction.ExecutionRepository;
import enable.enableexecutionservice.Repository_Abstraction.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExecutionEngineServiceHelper {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private ProcessFileHelper processFileHelper;


    public TaskDto completeTask(Long id, Long userId) {
        // Find the task
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Execution not found with id: " + executionId);
        }
        Task task = optionalTask.get();

        // Check status
        if (task.getStatus() != 0) {
            throw new IllegalArgumentException("Task is already concluded. Can only conclude task with status 0 (Running).");
        }

        // Complete task
        task.setStatus(1);
        task.setConcludedByUserId(userId);
        Long extractionStartId = id;

        // Conclude children
        ArrayList<Long> parentIdsToParse = new ArrayList<>();
        parentIdsToParse.add(task.getId());

        while (!parentIdsToParse.isEmpty()) {
            List<Task> runningChildTasks = new ArrayList<>(taskRepository.findByParentTaskId(parentIdsToParse.get(0)).stream()
                    .filter(childTask -> (childTask.getStatus() == 0)) // Only get running tasks with processId
                    .toList());

            for (int i = 0; i < runningChildTasks.size(); i++) {
                Task childTask = runningChildTasks.get(i);
                childTask.setStatus(status);
                runningChildTasks.set(i, childTask);
                parentIdsToParse.add(runningChildTasks.get(i).getId());
            }

            parentIdsToParse.remove(0);
        }

        // Conclude parents

        // Interrupt task
        // Check if all parentConnections to complete
        TaskDto taskDto = getTaskModel(id);
        Task task = taskDto.toTask();
        task.setStatus(status);
        task.setConcludedByUserId(userId);
        taskRepository.save(task);



        extractNextTasks(extractionStartId);
        return new TaskDto(task);
    }

    public int extractNextTasks(Long previousTaskId) {
        // Find the execution
        Optional<Execution> optionalExecution = executionRepository.findById(executionId);
        if (optionalExecution.isEmpty()) {
            throw new IllegalArgumentException("Execution not found with id: " + executionId);
        }
        Execution execution = optionalExecution.get();

        // Find the associated process file
        Optional<ProcessFileInstance> optionalProcessFile = processFileRepository.findById(execution.getProcessFileId());
        if (optionalProcessFile.isEmpty()) {
            throw new IllegalArgumentException("ProcessFile not found for execution id: " + executionId);
        }
        ProcessFileInstance processFileInstance = optionalProcessFile.get();

        ProcessFileDto processFileDto = ProcessFileHelper.modelToDto(processFileInstance);

        // Find connections where isParentConnection == false and originProcessId == previousProcessId
        List<ConnectionDto> relevantConnections = processFileDto.getConnections().stream()
                .filter(connection -> !connection.getIsParentConnection() && connection.getOriginProcessId().equals(previousProcessId))
                .toList();

        // Create new tasks for the destination processes of relevant connections
        int numNewTasks = 0;
        for (ConnectionDto connection : relevantConnections) {
            ProcessDto process = ProcessFileHelper.getProcessById(processFileDto, connection.getDestinationProcessId());
            if (process != null) {
                Task newTask = new Task();
                newTask.setExecutionId(executionId);
                newTask.setProcessId(process.getId());
                newTask.setName(process.getName());
                newTask.setDescription(process.getDescription());
                newTask.setStatus(0); // Assuming status 0 represents standard
                newTask.setCreatedDateTime(new Date());
                taskRepository.save(newTask);
                numNewTasks++;
            }
        }

        // Update execution status to completed if no new tasks were created
        if (numNewTasks == 0) {
            execution.setStatus(1);
            executionRepository.save(execution);
        }

        return numNewTasks;
    }

    private TaskDto getTaskDto(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            return new TaskDto(optionalTask.get());
        } else {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
    }

    private Task getTaskModel(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            return optionalTask.get();
        } else {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
    }

    private Task updateTaskStatus(Long id, int status) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(status);
            return taskRepository.save(task);
        } else {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }

    }
}
