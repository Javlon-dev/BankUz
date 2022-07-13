package uz.uzcard.service.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzcard.dto.auth.ProfileDetailDTO;
import uz.uzcard.entity.profile.ProfileDetailEntity;
import uz.uzcard.repository.profile.ProfileDetailRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileDetailService {

    private final ProfileDetailRepository profileDetailRepository;

    public void create(ProfileDetailDTO dto) {
        ProfileDetailEntity entity = getByPhoneNumberOrNull(dto.getPhoneNumber());

        if (!Optional.ofNullable(entity).isPresent()) {
            entity = new ProfileDetailEntity();
        }

        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setSmsCode(dto.getSmsCode());
        entity.setUpdatedDate(LocalDateTime.now());

        profileDetailRepository.save(entity);
    }

    public ProfileDetailEntity getByPhoneNumberOrNull(String phoneNumber) {
        return profileDetailRepository
                .findByPhoneNumber(phoneNumber)
                .orElse(null);
    }

    public Optional<ProfileDetailEntity> findById(Long id) {
        return profileDetailRepository
                .findById(id);
    }

}
