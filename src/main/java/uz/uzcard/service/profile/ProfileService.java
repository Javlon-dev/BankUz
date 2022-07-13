package uz.uzcard.service.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzcard.config.details.EntityDetails;
import uz.uzcard.dto.profile.ProfileBioDTO;
import uz.uzcard.dto.profile.ProfileDTO;
import uz.uzcard.dto.profile.ProfilePhoneNumberDTO;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.enums.profile.ProfileRole;
import uz.uzcard.enums.profile.ProfileStatus;
import uz.uzcard.exception.ItemNotFoundException;
import uz.uzcard.repository.profile.ProfileRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileDTO registration(ProfileDTO dto) {

        checkPhone(dto.getPhoneNumber());

        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName().trim());
        entity.setSurname(dto.getSurname().trim());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setStatus(ProfileStatus.ACTIVE);
        entity.setRole(ProfileRole.ROLE_PROFILE);

        profileRepository.save(entity);
        return toDTO(entity);
    }


    public ProfileDTO updateBio(ProfileBioDTO dto) {
        ProfileEntity entity = EntityDetails.getProfile();

        entity.setName(dto.getName().trim());
        entity.setSurname(dto.getSurname().trim());
        entity.setUpdatedDate(LocalDateTime.now());

        profileRepository.save(entity);
        return toDTO(entity);
    }

    public ProfileDTO updatePhone(ProfilePhoneNumberDTO dto) {
        ProfileEntity entity = EntityDetails.getProfile();

        checkPhone(dto.getPhoneNumber());

        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setUpdatedDate(LocalDateTime.now());

        profileRepository.save(entity);
        return toDTO(entity);
    }

    public ProfileDTO changeStatus(String profileId) {
        ProfileEntity entity = getByIdOrThrow(profileId);

        switch (entity.getStatus()) {
            case ACTIVE: {
                entity.setStatus(ProfileStatus.BLOCK);
                profileRepository.updateStatus(ProfileStatus.BLOCK, profileId);
                break;
            }
            case BLOCK: {
                entity.setStatus(ProfileStatus.ACTIVE);
                profileRepository.updateStatus(ProfileStatus.ACTIVE, profileId);
                break;
            }
        }

        return toDTO(entity);
    }

    public Boolean delete() {
        return profileRepository.updateDeletedDate(LocalDateTime.now(), EntityDetails.getProfile().getId()) > 0;
    }

    public ProfileDTO getProfile() {
        return toDTO(EntityDetails.getProfile());
    }

    public ProfileEntity getByIdOrThrow(String profileId) {
        return profileRepository
                .findByIdAndDeletedDateIsNull(profileId)
                .orElseThrow(() -> {
                    log.warn("Profile Not Found {}", profileId);
                    return new ItemNotFoundException("Profile Not Found!");
                });
    }

    public Optional<ProfileEntity> getByPhoneNumber(String phoneNumber) {
        return profileRepository
                .findByPhoneNumberAndDeletedDateIsNull(phoneNumber);
    }

    public void checkPhone(String phone) {
        if (profileRepository.findByPhoneNumberAndDeletedDateIsNull(phone).isPresent()) {
            log.warn("Phone Number Unique {}", phone);
            throw new ItemNotFoundException("This Phone Number Exists!");
        }
    }

    public ProfileDTO toDTO(ProfileEntity entity) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        return dto;
    }
}
