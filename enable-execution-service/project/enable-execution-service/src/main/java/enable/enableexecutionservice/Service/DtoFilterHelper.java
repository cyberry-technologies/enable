package enable.enableexecutionservice.Service;

import enable.enableexecutionservice.Dto.ConnectionDto;
import enable.enableexecutionservice.Dto.ProcessDto;
import enable.enableexecutionservice.Dto.TaskDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class DtoFilterHelper {
    public List<TaskDto> filterTasks(List<TaskDto> list, TaskDto filter) {
        Stream<TaskDto> stream = list.stream();

        if (filter != null) {
            if (filter.getId() != null) {
                stream = stream.filter(task -> (Objects.equals(task.getId(), filter.getId())));
            }
            if (filter.getExecutionId() != null) {
                stream = stream.filter(task -> (Objects.equals(task.getExecutionId(), filter.getExecutionId())));
            }
            if (filter.getProcessFileId() != null) {
                stream = stream.filter(task -> (Objects.equals(task.getProcessFileId(), filter.getProcessFileId())));
            }
            if (filter.getProcessId() != null) {
                stream = stream.filter(task -> (Objects.equals(task.getProcessId(), filter.getProcessId())));
            }
            if (filter.getParentTaskId() != null) {
                stream = stream.filter(task -> (Objects.equals(task.getParentTaskId(), filter.getParentTaskId())));
            }
            if (filter.getStatus() != null) {
                stream = stream.filter(task -> (Objects.equals(task.getStatus(), filter.getStatus())));
            }
        }

        return stream.toList();
    }

    public List<ProcessDto> filterProcesses(List<ProcessDto> list, ProcessDto filter) {
        Stream<ProcessDto> stream = list.stream();

        if (filter != null) {
            if (filter.getId() != null) {
                stream = stream.filter(process -> (Objects.equals(process.getId(), filter.getId())));
            }
            if (filter.getParentProcessId() != null) {
                stream = stream.filter(process -> (Objects.equals(process.getParentProcessId(), filter.getParentProcessId())));
            }
            if (filter.getType() != null) {
                stream = stream.filter(process -> (Objects.equals(process.getType(), filter.getType())));
            }
        }

        return stream.toList();
    }

    public List<ConnectionDto> filterConnections(List<ConnectionDto> list, ConnectionDto filter) {
        Stream<ConnectionDto> stream = list.stream();

        if (filter != null) {
            if (filter.getId() != null) {
                stream = stream.filter(connection -> (Objects.equals(connection.getId(), filter.getId())));
            }
            if (filter.getParentProcessId() != null) {
                stream = stream.filter(connection -> (Objects.equals(connection.getParentProcessId(), filter.getParentProcessId())));
            }
            if (filter.getOriginProcessId() != null) {
                stream = stream.filter(connection -> (Objects.equals(connection.getOriginProcessId(), filter.getOriginProcessId())));
            }
            if (filter.getDestinationProcessId() != null) {
                stream = stream.filter(connection -> (Objects.equals(connection.getDestinationProcessId(), filter.getDestinationProcessId())));
            }
            if (filter.getIsParentConnection() != null) {
                stream = stream.filter(connection -> (Objects.equals(connection.getIsParentConnection(), filter.getIsParentConnection())));
            }
        }

        return stream.toList();
    }
}
