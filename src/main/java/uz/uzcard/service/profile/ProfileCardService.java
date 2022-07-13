package uz.uzcard.service.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzcard.config.details.EntityDetails;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.dto.card.PanDTO;
import uz.uzcard.dto.card.VisaPanDTO;
import uz.uzcard.entity.CardEntity;
import uz.uzcard.entity.profile.ProfileCardEntity;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.enums.card.CardStatus;
import uz.uzcard.enums.profile.ProfileStatus;
import uz.uzcard.exception.AppBadRequestException;
import uz.uzcard.exception.ItemAlreadyExistsException;
import uz.uzcard.repository.mapper.ProfileCardInfoMapper;
import uz.uzcard.repository.profile.ProfileCardRepository;
import uz.uzcard.service.card.HumoCardService;
import uz.uzcard.service.card.UzcardService;
import uz.uzcard.service.card.VisaCardService;
import uz.uzcard.util.DateUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileCardService {

    private final ProfileCardRepository profileCardRepository;

    private final ProfileService profileService;

    private final UzcardService uzcardService;

    private final HumoCardService humoCardService;

    private final VisaCardService visaCardService;


    @Transactional
    public CardDTO assignPhoneToUzcard(PanDTO dto, String profileId) {

        ProfileEntity profileEntity = checkProfileStatus(profileId);

        CardEntity cardEntity = uzcardService.getByInactivePanOrThrow(dto.getPan());

        ProfileCardEntity entity = new ProfileCardEntity();
        entity.setProfileId(profileEntity.getId());
        entity.setCardId(cardEntity.getId());

        uzcardService.assignPhoneSave(cardEntity);

        profileCardRepository.save(entity);
        return toDTO(getByIdOrThrow(entity.getId()));
    }

    @Transactional
    public CardDTO assignPhoneToHumoCard(PanDTO dto, String profileId) {

        ProfileEntity profileEntity = checkProfileStatus(profileId);

        CardEntity cardEntity = humoCardService.getByInactivePanOrThrow(dto.getPan());

        ProfileCardEntity entity = new ProfileCardEntity();
        entity.setProfileId(profileEntity.getId());
        entity.setCardId(cardEntity.getId());

        humoCardService.assignPhoneSave(cardEntity);

        profileCardRepository.save(entity);
        return toDTO(getByIdOrThrow(entity.getId()));
    }

    @Transactional
    public CardDTO assignPhoneToVisaCard(VisaPanDTO dto, String profileId) {

        ProfileEntity profileEntity = checkProfileStatus(profileId);

        CardEntity cardEntity = visaCardService.getByInactivePanOrThrow(dto.getPan());

        if (!cardEntity.getCvvCode().equals(dto.getCvvCode())) {
            log.warn("Incorrect CVV {}", profileId);
            throw new BadCredentialsException("Incorrect CVV!");
        }

        ProfileCardEntity entity = new ProfileCardEntity();
        entity.setProfileId(profileEntity.getId());
        entity.setCardId(cardEntity.getId());

        visaCardService.assignPhoneSave(cardEntity);

        profileCardRepository.save(entity);
        return toDTO(getByIdOrThrow(entity.getId()));
    }

    public CardDTO changeStatusToUzcard(PanDTO dto) {

        ProfileEntity profile = EntityDetails.getProfile();

        CardEntity cardEntity = uzcardService.getByPanOrThrow(dto.getPan());

        ProfileCardEntity profileCardEntity = getProfilePan(profile.getId(), dto.getPan());

        CardEntity entity = profileCardEntity.getCard();

        switch (entity.getStatus()) {
            case ACTIVE: {
                entity.setStatus(CardStatus.BLOCK);
                uzcardService.updateStatus(CardStatus.BLOCK, dto.getPan());
                break;
            }
            case BLOCK: {
                entity.setStatus(CardStatus.ACTIVE);
                uzcardService.updateStatus(CardStatus.ACTIVE, dto.getPan());
                break;
            }
            case EXPIRED: {
                log.warn("Expired Card Status Not Changed {}", dto);
                throw new AppBadRequestException("Expired Card!");
            }
        }

        return toDTO(getByIdOrThrow(profileCardEntity.getId()));
    }

    public CardDTO changeStatusToHumoCard(PanDTO dto) {

        ProfileEntity profile = EntityDetails.getProfile();

        CardEntity cardEntity = humoCardService.getByPanOrThrow(dto.getPan());

        ProfileCardEntity profileCardEntity = getProfilePan(profile.getId(), dto.getPan());

        CardEntity entity = profileCardEntity.getCard();

        switch (entity.getStatus()) {
            case ACTIVE: {
                entity.setStatus(CardStatus.BLOCK);
                humoCardService.updateStatus(CardStatus.BLOCK, dto.getPan());
                break;
            }
            case BLOCK: {
                entity.setStatus(CardStatus.ACTIVE);
                humoCardService.updateStatus(CardStatus.ACTIVE, dto.getPan());
                break;
            }
            case EXPIRED: {
                log.warn("Expired Card Status Not Changed {}", dto);
                throw new AppBadRequestException("Expired Card!");
            }
        }

        return toDTO(getByIdOrThrow(profileCardEntity.getId()));
    }

    public CardDTO changeStatusToVisaCard(VisaPanDTO dto) {

        ProfileEntity profile = EntityDetails.getProfile();

        CardEntity cardEntity = visaCardService.getByPanOrThrow(dto.getPan());

        ProfileCardEntity profileCardEntity = getProfilePan(profile.getId(), dto.getPan());

        CardEntity entity = profileCardEntity.getCard();

        if (!entity.getCvvCode().equals(dto.getCvvCode())) {
            log.warn("Incorrect CVV {}", dto);
            throw new BadCredentialsException("Incorrect CVV!");
        }

        switch (entity.getStatus()) {
            case ACTIVE: {
                entity.setStatus(CardStatus.BLOCK);
                visaCardService.updateStatus(CardStatus.BLOCK, dto.getPan());
                break;
            }
            case BLOCK: {
                entity.setStatus(CardStatus.ACTIVE);
                visaCardService.updateStatus(CardStatus.ACTIVE, dto.getPan());
                break;
            }
            case EXPIRED: {
                log.warn("Expired Card Status Not Changed {}", dto);
                throw new AppBadRequestException("Expired Card!");
            }
        }

        return toDTO(getByIdOrThrow(profileCardEntity.getId()));
    }

    public ProfileEntity checkProfileStatus(String profileId) {
        ProfileEntity entity = profileService.getByIdOrThrow(profileId);

        if (entity.getStatus().equals(ProfileStatus.BLOCK)) {
            log.warn("Profile Blocked {}", profileId);
            throw new BadCredentialsException("Profile Blocked!");
        }

        return entity;
    }

    public List<CardDTO> getAllByPhoneNumber() {
        ProfileEntity entity = EntityDetails.getProfile();

        return profileCardRepository
                .findByProfilePhoneNumber(entity.getPhoneNumber())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CardDTO> getAllByProfileId(String profileId) {
        ProfileEntity entity = profileService.getByIdOrThrow(profileId);

        return profileCardRepository
                .findByProfilePhoneNumber(entity.getPhoneNumber())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProfileCardEntity getByProfileIdAndPanAndCheck(String profileId, String pan) {
        ProfileCardEntity profileCardEntity = profileCardRepository
                .findByProfileIdAndPan(profileId, pan)
                .orElseThrow(() -> {
                    log.warn("Not Your Card {} {}", profileId, pan);
                    return new AppBadRequestException("Not Your Card!");
                });

        return check(profileCardEntity);
    }

    public ProfileCardEntity getByPanAndCheck(String pan) {
        ProfileCardEntity profileCardEntity = profileCardRepository
                .findByPan(pan)
                .orElseThrow(() -> {
                    log.warn("Wrong Card {}", pan);
                    return new AppBadRequestException("Wrong Card!");
                });

        return check(profileCardEntity);
    }

    private ProfileCardEntity check(ProfileCardEntity profileCardEntity) {
        ProfileEntity profile = profileCardEntity.getProfile();
        if (profile.getStatus().equals(ProfileStatus.BLOCK)) {
            log.warn("Not Active Profile {}", profile);
            throw new AppBadRequestException("Not Active Profile!");
        }

        CardEntity card = profileCardEntity.getCard();
        if (!card.getStatus().equals(CardStatus.ACTIVE)) {
            log.warn("Not Active Card {}", card);
            throw new AppBadRequestException("Not Active Card!");
        }

        return profileCardEntity;
    }

    public ProfileCardEntity getByPan(String pan) {
        return profileCardRepository
                .findByPan(pan)
                .orElseThrow(() -> {
                    log.warn("Wrong Card {}", pan);
                    return new AppBadRequestException("Wrong Card!");
                });
    }

    public ProfileCardEntity getProfilePan(String profileId, String pan) {
        return profileCardRepository
                .findByProfileIdAndPan(profileId, pan)
                .orElseThrow(() -> {
                    log.warn("Not Your Card {} {}", profileId, pan);
                    return new AppBadRequestException("Not Your Card!");
                });

    }

    public void plusAmount(String pan, Long amount) {
        profileCardRepository.plusAmount(pan, amount);
    }

    public void minusAmount(String pan, Long amount) {
        profileCardRepository.minusAmount(pan, amount);
    }

    public void plusCommissionToUzcard(Long commission) {
        uzcardService.plusCommission(commission);
    }

    public void plusCommissionToHumoCard(Long commission) {
        humoCardService.plusCommission(commission);
    }

    public void plusCommissionToVisaCard(Long commission) {
        visaCardService.plusCommission(commission);
    }

    public void rollbackCommissionFromUzcard(String pan, Long amount, Long commission) {
        uzcardService.rollbackCommission(pan, amount, commission);
    }

    public void rollbackCommissionFromHumoCard(String pan, Long amount, Long commission) {
        humoCardService.rollbackCommission(pan, amount, commission);
    }

    public void rollbackCommissionFromVisaCard(String pan, Long amount, Long commission) {
        visaCardService.rollbackCommission(pan, amount, commission);
    }

    public ProfileCardInfoMapper getByIdOrThrow(Long id) {
        return profileCardRepository
                .findByIdMapper(id)
                .orElseThrow(() -> {
                    log.warn("Profile Card Not Found {}", id);
                    return new ItemAlreadyExistsException("Profile Card Not Found!");
                });
    }

    public CardDTO toDTO(ProfileCardInfoMapper mapper) {
        return mapper.toDTO();
    }

    //    @Scheduled(cron = "${cron.expression.monthly}")
    @Scheduled(cron = "${cron.expression.1minutes}")
    public void checkExpiredDate() {
        log.info("Check Expired Date");

        String toFormatMMyy = DateUtil.toFormatMMyy(LocalDate.now());
        profileCardRepository.checkExpiredDate(CardStatus.EXPIRED.name(), toFormatMMyy, CardStatus.ACTIVE.toString());
    }

    @Scheduled(cron = "${cron.expression.1minutes}")
    public void checkBalance() {
        log.info("Check Balance");

        profileCardRepository.checkBalanceForMinus(CardStatus.NOT_ALLOWED);

        profileCardRepository.checkBalanceForPlus(CardStatus.ACTIVE);

    }


}
