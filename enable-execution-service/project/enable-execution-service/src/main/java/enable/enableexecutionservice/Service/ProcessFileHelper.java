package enable.enableexecutionservice.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import enable.enableexecutionservice.Dto.ProcessFileDto;
import enable.enableexecutionservice.Model.ProcessFileInstance;
import enable.enableexecutionservice.Repository_Abstraction.ProcessFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    private ProcessFileDto modelToDto(ProcessFileInstance processFileInstance) {
        ProcessFileDto result = stringToDto(processFileInstance.getJsonString());
        result.setId(processFileInstance.getId());
        return result;
    }

    public ProcessFileDto getProcessFileById(Long id) {
        return modelToDto(processFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ProcessFile not found with ID: " + id)));
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
