package enable.enableexecutionservice.Dto;

import enable.enableexecutionservice.Model.Execution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExecutionDto {
    private Long id;
    private Long processFileId;
    private Long createdByUserId;
    private Date createdDateTime;
    private Long mainTaskId;

    private TaskDto mainTask;

    public ExecutionDto(Execution execution) {
        id = execution.getId();
        processFileId = execution.getProcessFileId();
        createdByUserId = execution.getCreatedByUserId();
        createdDateTime = execution.getCreatedDateTime();
        mainTaskId = execution.getMainTaskId();
        mainTask = null;
    }

    public Execution toExecution() {
        return new Execution(id,
                processFileId,
                createdByUserId,
                createdDateTime,
                mainTaskId);
    }
}
