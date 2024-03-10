package enable.enableexecutionservice.Controller;

import enable.enableexecutionservice.Dto.ExecutionDto;
import enable.enableexecutionservice.Service_Abstraction.IExecutionService;
import enable.enableexecutionservice.Service_Abstraction.ITaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/execution")
public class ExecutionController {

    private final IExecutionService executionService;
    private final ITaskService taskService;

    public ExecutionController(IExecutionService executionService, ITaskService taskService) {
        this.executionService = executionService;
        this.taskService = taskService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<ExecutionDto>> getExecutionsOfUser(@RequestParam(value = "userId") Long userId) {
        List<ExecutionDto> result = executionService.getExecutionsByUserId(userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<ExecutionDto> getExecution(@RequestParam(value = "id") Long id) {
        ExecutionDto result = executionService.getExecution(id);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/execute/new/processfile")
    public ResponseEntity<ExecutionDto> executeFromProcessFile(@RequestParam(value = "userId") Long userId, @RequestBody String processFileString) {
        ExecutionDto result = executionService.createExecutionFromProcessFile(userId, processFileString);

        taskService.extractNextTasks(result.getId(), null);

        return ResponseEntity.created(URI.create("/execution/" + result.getId()))
                .body(result);
    }

    @PutMapping("/terminate")
    public ResponseEntity<ExecutionDto> terminateExecution(@RequestParam(value = "id") Long id) {
        ExecutionDto result = executionService.terminateExecution(id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteExecution(@RequestParam(value = "id") Long id) {
        Boolean result = executionService.deleteExecution(id);
        return ResponseEntity.ok(result);
    }
}
