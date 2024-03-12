package enable.enableexecutionservice.Controller;

import enable.enableexecutionservice.Dto.GetTasksRequestBody;
import enable.enableexecutionservice.Dto.TaskDto;
import enable.enableexecutionservice.Service_Abstraction.ITaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/execution")
    public ResponseEntity<List<TaskDto>> getTasksOfExecution(@RequestParam(value = "executionId") Long executionId) {
        List<TaskDto> result = taskService.getTasksOfExecution(executionId);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(@RequestBody GetTasksRequestBody requestBody) {
        List<TaskDto> result = taskService.getTasks(requestBody);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/id")
    public ResponseEntity<TaskDto> getTask(@RequestParam(value = "id") Long id) {
        TaskDto result = taskService.getTask(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/complete")
    public ResponseEntity<TaskDto> completeTask(@RequestParam(value = "id") Long id, @RequestParam(value = "userId") Long userId) {
        TaskDto result = taskService.completeTask(id, userId);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/interrupt")
    public ResponseEntity<TaskDto> interruptTask(@RequestParam(value = "id") Long id, @RequestParam(value = "userId") Long userId) {
        TaskDto result = taskService.interruptTask(id, userId);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/claim")
    public ResponseEntity<TaskDto> claimTask(@RequestParam(value = "id") Long id, @RequestParam(value = "userId") Long userId) {
        TaskDto result = taskService.claimTask(id, userId);

        return ResponseEntity.ok(result);
    }
}
