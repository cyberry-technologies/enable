package enable.enableexecutionservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessDto {
    private Long id;
    private Long parentProcessId;
    private Integer type;
    private String name;
    private String description;
    private String inputDescription;
    private String outputDescription;
}
