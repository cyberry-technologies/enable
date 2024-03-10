package enable.enableexecutionservice.Repository_Abstraction;

import enable.enableexecutionservice.Model.Execution;
import enable.enableexecutionservice.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.executionId = :executionId")
    void deleteByExecutionId(@Param("executionId") Long executionId);

    List<Task> findByExecutionId(Long executionId);

    List<Task> findByParentTaskId(Long parentTaskId);
}


