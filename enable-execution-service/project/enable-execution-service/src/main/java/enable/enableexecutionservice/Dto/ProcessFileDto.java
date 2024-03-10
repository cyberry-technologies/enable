package enable.enableexecutionservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessFileDto {
    private Long id;
    private String fileName;
    private Long mainProcessId;
    private List<ProcessDto> processes;
    private List<ConnectionDto> connections;
}
