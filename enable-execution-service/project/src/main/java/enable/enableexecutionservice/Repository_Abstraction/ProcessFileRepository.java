package enable.enableexecutionservice.Repository_Abstraction;

import enable.enableexecutionservice.Model.ProcessFileInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessFileRepository extends JpaRepository<ProcessFileInstance, Long> {

}

