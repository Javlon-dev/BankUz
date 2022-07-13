package uz.uzcard.repository.card;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.uzcard.entity.CardEntity;
import uz.uzcard.enums.card.CardStatus;
import uz.uzcard.enums.profile.ProfileStatus;

import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, String> {

    Optional<CardEntity> findByPanAndDeletedDateIsNull(String pan);

    Optional<CardEntity> findByPanAndStatusAndDeletedDateIsNull(String pan, CardStatus status);

    Page<CardEntity> findByStatusAndDeletedDateIsNull(CardStatus status, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update CardEntity set status = :status where pan = :pan and deletedDate is null ")
    void updateStatus(@Param("status") CardStatus status, @Param("pan") String pan);

    @Modifying
    @Transactional
    @Query(value = "update CardEntity set balance = balance + :commission where deletedDate is null and pan = :BankCardPan")
    void plusCommission(@Param("BankCardPan") String BankCardPan, @Param("commission") Long commission);

    @Modifying
    @Transactional
    @Query(value = "update card set balance = balance + :amount where deleted_date is null and pan = :pan ;" +
            "update card set balance = balance - :commission where deleted_date is null and pan = :BankCardPan ;",
            nativeQuery = true)
    void rollbackCommission(@Param("pan") String pan, @Param("amount") Long amount,
                                      @Param("BankCardPan") String BankCardPan, @Param("commission") Long commission);
}