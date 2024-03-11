package enable.enableexecutionservice.Model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "processfile")
public class ProcessFileInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "json_string", nullable = false, columnDefinition = "TEXT")
    private String jsonString;
}
