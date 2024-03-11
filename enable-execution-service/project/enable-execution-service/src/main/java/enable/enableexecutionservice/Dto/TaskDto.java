package enable.enableexecutionservice.Dto;

import enable.enableexecutionservice.Model.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskDto {
    private Long id;
    private Long executionId;
    private Long processFileId;
    private Long processId;
    private Long parentTaskId;
    private Integer status; // 0=running, 1=completed, 2=interrupted, 3=skipped, 4=terminated
    private Long concludedByUserId;
    private Long claimedByUserId;
    private Date createdDateTime;

    private ProcessDto process;

    public TaskDto(Task task) {
        id = task.getId();
        executionId = task.getExecutionId();
        processFileId = task.getProcessFileId();
        processId = task.getProcessId();
        parentTaskId = task.getParentTaskId();
        status = task.getStatus();
        concludedByUserId = task.getConcludedByUserId();
        claimedByUserId = task.getClaimedByUserId();
        createdDateTime = task.getCreatedDateTime();
        process = null;
    }

    public Task toTask() {
        return new Task(id,
                executionId,
                processFileId,
                processId,
                parentTaskId,
                status,
                concludedByUserId,
                claimedByUserId,
                createdDateTime);
    }
}
