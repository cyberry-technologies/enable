package enable.enableexecutionservice.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import enable.enableexecutionservice.Dto.ProcessDto;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.ProcessFileInstance;
import enable.enableexecutionservice.Repository_Abstraction.ProcessFileRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessFileHelper {
    private final ProcessFileRepository processFileRepository;
    private final DtoFilterHelper dtoFilterHelper;

    public ProcessFileHelper(ProcessFileRepository processFileRepository, DtoFilterHelper dtoFilterHelper) {
        this.processFileRepository = processFileRepository;
        this.dtoFilterHelper = dtoFilterHelper;
    }

    public ProcessFileDto getProcessFileById(Long id) {
        return modelToDto(processFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProcessFile not found with ID: " + id)));
    }

    public ProcessFileDto addNew(String processFileString) {
        //Check processFileString
        if (stringToDto(processFileString) == null) {
            throw new RuntimeException("Error parsing JSON in ProcessFile");
        }

        return modelToDto(processFileRepository.save(new ProcessFileInstance(null, processFileString)));
    }

    public void deleteById(Long id) {
        // Delete processFile where id == processFileId
        processFileRepository.deleteById(id);
    }

    public TaskDto includeProcessForTask(TaskDto taskDto) {
        // Check parameters
        if (taskDto == null) {
            throw new IllegalArgumentException("taskDto cannot be null");
        }
        if (taskDto.getId() == null) {
            throw new IllegalArgumentException("id of executionDto cannot be null");
        }
        if (taskDto.getProcessFileId() == null) {
            throw new IllegalArgumentException("processFileId of taskDto with id:" + taskDto.getId() + ", cannot be null");
        }
        if (taskDto.getProcessId() == null) {
            throw new IllegalArgumentException("processId of taskDto with id:" + taskDto.getId() + ", cannot be null");
        }

        ProcessFileDto processFileDto = getProcessFileById(taskDto.getProcessFileId());

        ProcessDto filter = new ProcessDto();
        filter.setId(taskDto.getProcessId());
        List<ProcessDto> foundProcesses = dtoFilterHelper.filterProcesses(processFileDto.getProcesses(), filter);

        taskDto.setProcess(foundProcesses.get(0));
        return taskDto;
    }

    public List<TaskDto> includeProcessForTaskList(List<TaskDto> list) {
        // Check parameters
        if (list == null) {
            throw new IllegalArgumentException("list cannot be null");
        }

        List<TaskDto> result = new ArrayList<>();

        for (TaskDto task: list) {
            result.add(this.includeProcessForTask(task));
        }

        return result;
    }


    private ProcessFileDto modelToDto(ProcessFileInstance processFileInstance) {
        // Check parameters
        if (processFileInstance == null) {
            throw new IllegalArgumentException("processFileInstance cannot be null");
        }
        if (processFileInstance.getId() == null) {
            throw new IllegalArgumentException("id of processFileInstance cannot be null");
        }
        if (processFileInstance.getJsonString() == null) {
            throw new IllegalArgumentException("jsonString of processFileInstance cannot be null");
        }

        ProcessFileDto result = stringToDto(processFileInstance.getJsonString());
        result.setId(processFileInstance.getId());
        return result;
    }

    private ProcessFileDto stringToDto(String processFileString) {
        if (processFileString == null) {
            throw new IllegalArgumentException("processFileString cannot be null");
        }

        try {
            // Parse JSON string from ProcessFile to retrieve processes and connections
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(processFileString, ProcessFileDto.class);
        } catch (IOException e) {
            // Handle JSON parsing exception
            throw new RuntimeException("Error parsing JSON in ProcessFile", e);
        }
    }
}
