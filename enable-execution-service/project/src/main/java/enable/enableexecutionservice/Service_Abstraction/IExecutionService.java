package enable.enableexecutionservice.Service_Abstraction;

import enable.enableexecutionservice.Dto.ExecutionDto;

import java.util.List;

public interface IExecutionService {
    List<ExecutionDto> getExecutionsByUserId(Long userId);

    ExecutionDto getExecution(Long id);

    ExecutionDto createExecutionFromProcessFile(Long userId, String processFileString);

    Boolean deleteExecution(Long id);
}
