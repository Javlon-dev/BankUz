package uz.uzcard.repository.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.uzcard.entity.transaction.TransactionEntity;
import uz.uzcard.enums.transaction.TransactionStatus;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    Page<TransactionEntity> findAllByStatus(TransactionStatus status, Pageable pageable);

}