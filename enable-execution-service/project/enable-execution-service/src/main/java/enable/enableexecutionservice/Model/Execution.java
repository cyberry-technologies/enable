package enable.enableexecutionservice.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "execution")
public class Execution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "processfile_id", nullable = false)
    private Long processFileId;

    @Column(name = "create_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "created_datetime", nullable = false)
    private Date createdDateTime;

    @Column(name = "main_task_id", nullable = true)
    private Long mainTaskId;
}
