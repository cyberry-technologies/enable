package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.ExecutionDto;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.Execution;
import enable.enableexecutionservice.Repository_Abstraction.ExecutionRepository;
import enable.enableexecutionservice.Repository_Abstraction.TaskRepository;
import enable.enableexecutionservice.Service_Abstraction.IExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Objects;

@Service
public class ExecutionService implements IExecutionService {

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProcessFileHelper processFileHelper;

    @Autowired
    private ExecutionEngineServiceHelper executionEngineServiceHelper;

    @Override
    public List<ExecutionDto> getExecutionsByUserId(Long userId) {
        // Check parameters
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        //TODO: implement participant functionality
        List<ExecutionDto> executionsOfUser = executionRepository.findAll().stream()
                .filter(execution -> Objects.equals(execution.getCreatedByUserId(), userId))
                .map(ExecutionDto::new).toList();

        return this.includeMainTaskForExecutionList(executionsOfUser);
    }

    @Override
    public ExecutionDto getExecution(Long id) {
        // Check parameters
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        // Find
        ExecutionDto result = new ExecutionDto(executionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found with ID: " + id)));

        // Include main task
        return this.includeMainTaskOfExecution(result);
    }

    @Override
    public ExecutionDto createExecutionFromProcessFile(Long userId, String processFileString) {
        // Check parameters
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (processFileString == null) {
            throw new IllegalArgumentException("processFileString cannot be null");
        }

        // Add ProcessFile to the database
        ProcessFileDto processFileDto = processFileHelper.addNew(processFileString);

        // Create and save new Execution
        Execution execution = new Execution();
        execution.setProcessFileId(processFileDto.getId());
        execution.setCreatedByUserId(userId);
        execution.setCreatedDateTime(new Date());
        execution = executionRepository.save(execution);

        // Get and save starting tasks of execution
        List<TaskDto> startingTasks = executionEngineServiceHelper.extractExecutionStartTasks(new ExecutionDto(execution), processFileDto);

        // Set main mask id in Execution
        execution.setMainTaskId(startingTasks.get(0).getId());
        Execution result = executionRepository.save(execution);

        return this.getExecution(result.getId());
    }

    @Override
    public Boolean deleteExecution(Long id) {
        // Check parameters
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        Execution execution = executionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found with ID: " + id));

        // Delete tasks by execution ID
        taskRepository.deleteByExecutionId(id);

        // Save processFileId for the last step
        Long processFileId = execution.getProcessFileId();

        // Delete execution with id == id
        executionRepository.deleteById(id);

        // Delete processFile where id == processFileId
        processFileHelper.deleteById(processFileId);

        return true;
    }

    private ExecutionDto includeMainTaskOfExecution(ExecutionDto executionDto) {
        // Check parameters
        if (executionDto == null) {
            throw new IllegalArgumentException("executionDto cannot be null");
        }
        if (executionDto.getId() == null) {
            throw new IllegalArgumentException("id of executionDto cannot be null");
        }
        if (executionDto.getMainTaskId() == null) {
            throw new IllegalArgumentException("mainTaskId of executionDto with id:" + executionDto.getId() + ", cannot be null");
        }
        if (executionDto.getProcessFileId() == null) {
            throw new IllegalArgumentException("processFileId of executionDto with id:" + executionDto.getId() + ", cannot be null");
        }

        TaskDto mainTask = new TaskDto(taskRepository.findById(executionDto.getMainTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Main task not found for execution with ID: " + executionDto.getId())));

        // Include process of main task
        mainTask = processFileHelper.includeProcessForTask(executionDto.getProcessFileId(), mainTask);

        executionDto.setMainTask(mainTask);

        return executionDto;
    }

    private List<ExecutionDto> includeMainTaskForExecutionList(List<ExecutionDto> list) {
        // Check parameters
        if (list == null) {
            throw new IllegalArgumentException("list cannot be null");
        }

        List<ExecutionDto> result = new ArrayList<>();

        for (ExecutionDto executionDto: list) {
            result.add(this.includeMainTaskOfExecution(executionDto));
        }

        return result;
    }
}

