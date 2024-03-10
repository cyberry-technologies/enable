package enable.enableexecutionservice.Repository_Abstraction;

import enable.enableexecutionservice.Model.Execution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {
}

