package uz.uzcard.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.enums.profile.ProfileStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByPhoneNumberAndDeletedDateIsNull(String phoneNumber);

    Optional<ProfileEntity> findByIdAndDeletedDateIsNull(String id);

    @Modifying
    @Transactional
    @Query(value = "update ProfileEntity set status = :status where id = :id and deletedDate is null ")
    void updateStatus(@Param("status") ProfileStatus status, @Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "update ProfileEntity set deletedDate = :now where id = :id and deletedDate is null ")
    int updateDeletedDate(@Param("now") LocalDateTime now, @Param("id") String id);

}