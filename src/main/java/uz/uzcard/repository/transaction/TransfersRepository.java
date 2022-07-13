package uz.uzcard.repository.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.uzcard.entity.transaction.TransfersEntity;
import uz.uzcard.enums.transaction.TransfersStatus;

import java.util.List;


public interface TransfersRepository extends JpaRepository<TransfersEntity, String> {

    @Modifying
    @Transactional
    @Query(value = "update TransfersEntity set chanceCount = chanceCount + 1 where id = :id")
    void increaseChanceCount(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "update TransfersEntity set status = :status where id = :id")
    void updateStatus(@Param("status") TransfersStatus status, @Param("id") String id);

    Page<TransfersEntity> findAllByProfileId(String profileId, Pageable pageable);

    Page<TransfersEntity> findAllByStatus(TransfersStatus status, Pageable pageable);

    @Query(value = "select t from TransfersEntity t " +
            "inner join t.profile p " +
            "inner join t.fromCard fc " +
            "inner join t.toCard tc " +
            "where p.deletedDate is null " +
            "and fc.deletedDate is null " +
            "and tc.deletedDate is null " +
            "and p.status = 'ACTIVE' " +
            "and fc.status = 'ACTIVE' " +
            "and tc.status = 'ACTIVE' " +
            "and t.status = :status ")
    List<TransfersEntity> findAllByStatus(@Param("status") TransfersStatus status);

}