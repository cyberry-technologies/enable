package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.ExecutionDto;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.Execution;
import enable.enableexecutionservice.Model.Task;
import enable.enableexecutionservice.Repository_Abstraction.ExecutionRepository;
import enable.enableexecutionservice.Repository_Abstraction.TaskRepository;
import enable.enableexecutionservice.Service_Abstraction.IExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Service
public class ExecutionService implements IExecutionService {

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProcessFileHelper processFileHelper;

    @Override
    public List<ExecutionDto> getExecutionsByUserId(Long userId) {
        List<ExecutionDto> result = new ArrayList<>();

        //TODO: implement user functionality
        for (Execution execution: executionRepository.findAll()) {
            ExecutionDto executionDto = this.includeMainTask(new ExecutionDto(execution));
            result.add(executionDto);
        }

        return result;
    }

    @Override
    public ExecutionDto getExecution(Long id) {
        ExecutionDto result = new ExecutionDto(executionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found with ID: " + id)));

        return includeMainTask(result);
    }

    @Override
    public ExecutionDto createExecutionFromProcessFile(Long userId, String processFileString) {
        // Add ProcessFile to the database
        ProcessFileDto processFileDto = processFileHelper.addNew(processFileString);

        // Create a new Execution
        Execution execution = new Execution();
        execution.setProcessFileId(processFileDto.getId());
        execution.setCreatedByUserId(userId);
        execution.setCreatedDateTime(new Date());
        execution.setMainTaskId(null);
        execution = executionRepository.save(execution);

        // Create main Task
        Task mainTask = new Task();
        mainTask.setExecutionId(execution.getId());
        mainTask.setProcessFileId(processFileDto.getId());
        mainTask.setProcessId(processFileDto.getMainProcessId());
        mainTask.setStatus(0);
        mainTask.setConcludedByUserId(null);
        mainTask.setClaimedByUserId(null);
        mainTask.setCreatedDateTime(new Date());
        mainTask = taskRepository.save(mainTask);

        // Set main Task id in Execution
        execution.setMainTaskId(mainTask.getId());
        execution = executionRepository.save(execution);

        return new ExecutionDto(execution);
    }

    @Override
    public Boolean deleteExecution(Long id) {
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

    private ExecutionDto includeMainTask(ExecutionDto executionDto) {
        executionDto.setMainTask(new TaskDto(
                taskRepository.findById(executionDto.getMainTaskId())
                        .orElseThrow(() -> new IllegalArgumentException("Main task not found for execution with ID: " + executionDto.getId()))));

        return executionDto;
    }
}

