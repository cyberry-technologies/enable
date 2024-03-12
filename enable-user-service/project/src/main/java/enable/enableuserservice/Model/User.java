package enable.enableuserservice.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
//    @Size(max = 50)
    private String username;

    @Column(nullable = false, unique = true)
//    @Size(max = 50)
    @Email
    private String email;

    @Column(nullable = false)
//    @Size(max = 50)
    private String password;

}
