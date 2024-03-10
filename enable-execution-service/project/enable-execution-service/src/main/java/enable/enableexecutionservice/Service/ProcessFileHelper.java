package enable.enableexecutionservice.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import enable.enableexecutionservice.Dto.ConnectionDto;
import enable.enableexecutionservice.Dto.ProcessDto;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Model.ProcessFileInstance;
import enable.enableexecutionservice.Repository_Abstraction.ProcessFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessFileHelper {
    @Autowired
    private ProcessFileRepository processFileRepository;

    public ProcessFileDto addNew(String processFileString) {
        // Add ProcessFile to the database
        ProcessFileInstance processFileInstance = new ProcessFileInstance(null, stringToDto(processFileString).getFileName(), processFileString);
        processFileInstance = processFileRepository.save(processFileInstance);

        return modelToDto(processFileInstance);
    }

    public void deleteById(Long id) {
        // Delete processFile where id == processFileId
        processFileRepository.deleteById(id);
    }

    public ProcessFileDto modelToDto(ProcessFileInstance processFileInstance) {
        ProcessFileDto result = stringToDto(processFileInstance.getJsonString());
        result.setId(processFileInstance.getId());
        return result;
    }

    public ProcessFileDto getProcessFileById(Long id) {
        return modelToDto(processFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProcessFile not found with ID: " + id)));
    }

    public ProcessDto getProcessById(Long processFileId, Long processId) {
        ProcessFileDto processFileDto = getProcessFileById(processFileId);

        Optional<ProcessDto> optionalProcess = processFileDto.getProcesses().stream()
                .filter(process -> process.getId().equals(processId))
                .findFirst();

        return optionalProcess.orElse(null);
    }

    public List<ProcessDto> getProcessesByParentProcessId(Long processFileId, Long parentProcessId) {
        ProcessFileDto processFileDto = getProcessFileById(processFileId);

        return processFileDto.getProcesses().stream()
                .filter(process -> process.getParentProcessId().equals(parentProcessId))
                .toList();
    }

    public ConnectionDto getConnectionById(Long processFileId, Long connectionId) {
        ProcessFileDto processFileDto = getProcessFileById(processFileId);

        Optional<ConnectionDto> optionalConnection = processFileDto.getConnections().stream()
                .filter(connection -> connection.getId().equals(connectionId))
                .findFirst();

        return optionalConnection.orElse(null);
    }

    private ProcessFileDto stringToDto(String processFileString) {
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
