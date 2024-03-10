package enable.enableexecutionservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_id", nullable = false)
    private Long executionId;

    @Column(name = "processfile_id", nullable = false)
    private Long processFileId;

    @Column(name = "process_id", nullable = false)
    private Long processId;

    @Column(name = "parent_task_id", nullable = true)
    private Long parentTaskId;

    @Column(name = "status", nullable = true)
    private Integer status; // 0=running, 1=completed, 2=interrupted, 3=skipped, 4=terminated

    @Column(name = "concluded_user_id", nullable = true)
    private Long concludedByUserId;

    @Column(name = "claimed_user_id", nullable = true)
    private Long claimedByUserId;

    @Column(name = "created_datetime", nullable = false)
    private Date createdDateTime;
}
