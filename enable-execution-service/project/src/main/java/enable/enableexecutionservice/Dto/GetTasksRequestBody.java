package enable.enableexecutionservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTasksRequestBody {
    private Long id;
    private Integer status; // default=all, 0=running, 1=completed, 2=interrupted, 3=skipped, 4=terminated
    private Long executionId;
    private Long parentTaskId;
    private Long claimedByUserId;
    private Integer processType; // default=all, 0=standard, 1=human, 2=code, 3=external

    private Boolean formatWithProcesses;
    private Boolean formatAsTaskTree;
    private Boolean formatWithParticipants;
}
