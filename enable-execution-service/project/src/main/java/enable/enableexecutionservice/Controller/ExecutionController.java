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

    @GetMapping("/get/userId")
    public ResponseEntity<List<ExecutionDto>> getExecutionsOfUser(@RequestParam(value = "userId") Long userId) {
        List<ExecutionDto> result = executionService.getExecutionsByUserId(userId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/id")
    public ResponseEntity<ExecutionDto> getExecution(@RequestParam(value = "id") Long id) {
        ExecutionDto result = executionService.getExecution(id);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/execute/new/processFile")
    public ResponseEntity<ExecutionDto> executeFromProcessFile(@RequestParam(value = "userId") Long userId, @RequestBody String processFileString) {
        ExecutionDto result = executionService.createExecutionFromProcessFile(userId, processFileString);

        return ResponseEntity.created(URI.create("/execution/get/id?id=" + result.getId()))
                .body(result);
    }

    @PutMapping("/terminate")
    public ResponseEntity<ExecutionDto> terminateExecution(@RequestParam(value = "id") Long id, @RequestParam(value = "userId") Long userId) {
        ExecutionDto execution = executionService.getExecution(id);

        taskService.terminateTask(execution.getMainTaskId(), userId);

        return ResponseEntity.ok(executionService.getExecution(id));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteExecution(@RequestParam(value = "id") Long id) {
        Boolean result = executionService.deleteExecution(id);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}