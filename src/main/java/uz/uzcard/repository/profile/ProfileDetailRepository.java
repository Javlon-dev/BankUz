package uz.uzcard.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzcard.entity.profile.ProfileDetailEntity;

import java.util.Optional;

public interface ProfileDetailRepository extends JpaRepository<ProfileDetailEntity, Long> {

    Optional<ProfileDetailEntity> findByPhoneNumber(String phoneNumber);

}