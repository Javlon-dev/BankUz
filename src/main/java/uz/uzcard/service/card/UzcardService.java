package uz.uzcard.service.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import uz.uzcard.config.details.EntityDetails;
import uz.uzcard.dto.BankDTO;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.dto.card.PanDTO;
import uz.uzcard.dto.profile.ProfileDTO;
import uz.uzcard.entity.BankEntity;
import uz.uzcard.entity.CardEntity;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.enums.card.CardStatus;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.exception.AppBadRequestException;
import uz.uzcard.exception.ItemAlreadyExistsException;
import uz.uzcard.repository.card.CardRepository;
import uz.uzcard.service.BankService;
import uz.uzcard.util.DateUtil;
import uz.uzcard.util.NumberUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UzcardService {

    private final CardRepository cardRepository;

    private final BankService bankService;

    @Value("${card.type.uzcard}")
    private String issuerIdNumber;

    @Value("${bank.card.uzcard}")
    private String uzcardPan;

    @Value("${card.expiration.year}")
    private String expirationYear;


    public CardDTO create() {

        ProfileEntity profile = EntityDetails.getProfile();

        String pan = NumberUtil.generatePan(issuerIdNumber);

        if (getByPanOptional(pan).isPresent()) {
            log.warn("Pan Already Exists {}", pan);
            throw new ItemAlreadyExistsException("This Pan Already Exists!");
        }

        CardEntity entity = new CardEntity();
        entity.setPan(pan);
        entity.setMaskedPan(NumberUtil.doMaskedPan(pan));
        entity.setStatus(CardStatus.INACTIVE);
        entity.setType(BalanceType.SUM);
        entity.setProfileId(profile.getPhoneNumber());
        entity.setBankId(issuerIdNumber);

        cardRepository.save(entity);
        entity.setProfile(profile);
        entity.setBank(bankService.getByMfoCodeOrThrow(issuerIdNumber));
        return toDTO(entity);
    }

    public CardDTO changeStatus(PanDTO dto) {

        CardEntity entity = getByPanOrThrow(dto.getPan());

        switch (entity.getStatus()) {
            case ACTIVE: {
                entity.setStatus(CardStatus.BLOCK);
                cardRepository.updateStatus(CardStatus.BLOCK, dto.getPan());
                break;
            }
            case BLOCK: {
                entity.setStatus(CardStatus.ACTIVE);
                cardRepository.updateStatus(CardStatus.ACTIVE, dto.getPan());
                break;
            }
            case INACTIVE: {
                log.warn("Inactive Card Status Not Changed {}", dto);
                throw new AppBadRequestException("Inactive Card!");
            }
            case EXPIRED: {
                log.warn("Expired Card Status Not Changed {}", dto);
                throw new AppBadRequestException("Expired Card!");
            }
            case NATIVE:{
                log.warn("Native Card Status Not Changed {}", dto);
                throw new AppBadRequestException("Native Card!");
            }
        }

        return toDTO(entity);
    }

    public void updateStatus(CardStatus status, String pan) {
        cardRepository.updateStatus(status, pan);
    }

    public void assignPhoneSave(CardEntity entity) {
        entity.setStatus(CardStatus.ACTIVE);
        entity.setExpiredDate(DateUtil.toFormatMMyy(LocalDate.now().plusYears(Long.parseLong(expirationYear))));
        cardRepository.save(entity);
    }

    public PageImpl<CardDTO> getInactiveCardByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdDate"));

        Page<CardEntity> entityPage = cardRepository.findByStatusAndDeletedDateIsNull(CardStatus.INACTIVE, pageable);

        List<CardDTO> dtoList = new ArrayList<>();

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public void plusCommission(Long commission) {
        cardRepository.plusCommission(uzcardPan, commission);
    }

    public void rollbackCommission(String pan, Long amount, Long commission) {
        cardRepository.rollbackCommission(pan, amount, uzcardPan, commission);
    }

    public CardEntity getByPanOrThrow(String pan) {
        if (!pan.startsWith(issuerIdNumber)){
            log.warn("Not Uzcard! {}", pan);
            throw new AppBadRequestException("Not Uzcard!");
        }
        return cardRepository
                .findByPanAndDeletedDateIsNull(pan)
                .orElseThrow(() -> {
                    log.warn("Card Not Found {}", pan);
                    return new ItemAlreadyExistsException("Card Not Found!");
                });
    }

    public CardEntity getByInactivePanOrThrow(String pan) {
        CardEntity entity = getByPanOrThrow(pan);

        if (!entity.getStatus().equals(CardStatus.INACTIVE)) {
            log.warn("Active Card {}", pan);
            throw new AppBadRequestException("Active Card!");
        }

        return entity;
    }

    public Optional<CardEntity> getByPanOptional(String pan) {
        return cardRepository
                .findByPanAndDeletedDateIsNull(pan);
    }

    public CardDTO toDTO(CardEntity entity) {
        CardDTO dto = new CardDTO();
        dto.setId(entity.getId());
        dto.setPan(entity.getPan());
        dto.setMaskedPan(entity.getMaskedPan());
        dto.setExpiredDate(entity.getExpiredDate());
        dto.setStatus(entity.getStatus());
        dto.setCash(NumberUtil.balanceToType(entity.getBalance(), entity.getType()));
        dto.setCreatedDate(entity.getCreatedDate());

        ProfileEntity profileEntity = entity.getProfile();
        ProfileDTO profile = new ProfileDTO();
        profile.setId(profileEntity.getId());
        profile.setName(profileEntity.getName());
        profile.setSurname(profileEntity.getSurname());
        profile.setPhoneNumber(profileEntity.getPhoneNumber());

        dto.setProfile(profile);

        BankEntity bankEntity = entity.getBank();
        BankDTO bank = new BankDTO();
        bank.setMfoCode(bankEntity.getMfoCode());

        dto.setBank(bank);

        return dto;
    }
}
