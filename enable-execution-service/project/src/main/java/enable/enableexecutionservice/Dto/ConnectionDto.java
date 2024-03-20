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
public class ConnectionDto {
    private Long id;
    private Long parentProcessId;
    private Long originProcessId;
    private Long destinationProcessId;
    private Boolean isParentConnection;
}
