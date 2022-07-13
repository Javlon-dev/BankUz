package uz.uzcard.service.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzcard.config.details.EntityDetails;
import uz.uzcard.dto.rest.rate.ExchangeRateDTO;
import uz.uzcard.dto.transaction.TransfersDTO;
import uz.uzcard.dto.transaction.TransfersStatusDTO;
import uz.uzcard.entity.CardEntity;
import uz.uzcard.entity.profile.ProfileCardEntity;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.entity.transaction.TransfersEntity;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.enums.transaction.TransfersStatus;
import uz.uzcard.exception.AppBadRequestException;
import uz.uzcard.exception.ItemNotFoundException;
import uz.uzcard.repository.transaction.TransfersRepository;
import uz.uzcard.service.profile.ProfileCardService;
import uz.uzcard.service.rest.rate.ExchangeRateService;
import uz.uzcard.util.CommissionUtil;
import uz.uzcard.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransfersService {

    private final TransfersRepository transfersRepository;

    private final TransactionService transactionService;

    private final ProfileCardService profileCardService;

    private final ExchangeRateService exchangeRateService;

    @Value("${card.type.uzcard}")
    private String uzcardPanType;

    @Value("${card.type.humo}")
    private String humoCardType;

    @Value("${card.type.visa}")
    private String visaCardType;

    @Value("${commission.percent.sum}")
    private String commissionPercentSum;

    @Value("${commission.percent.usd}")
    private String commissionPercentUsd;

    @Value("${amount.border.sum}")
    private String amountBorderSum;

    @Value("${amount.border.usd}")
    private String amountBorderUsd;

    @Value("${after.amount.sum}")
    private String afterAmountSum;

    @Value("${after.amount.usd}")
    private String afterAmountUsd;

    @Value("${after.percentage.sum}")
    private String afterPercentageSum;

    @Value("${after.percentage.usd}")
    private String afterPercentageUsd;


    public TransfersDTO makeTransfer(TransfersDTO dto) {
        ProfileEntity profile = EntityDetails.getProfile();

        dto.setAmount(dto.getAmount() * 100); // sum or usd
        Long amount = dto.getAmount();

        String fromCardPan = dto.getFromCardPan();
        String toCardPan = dto.getToCardPan();

        ProfileCardEntity fromProfileCard = profileCardService.getByProfileIdAndPanAndCheck(profile.getId(), fromCardPan);

        CardEntity fromCard = fromProfileCard.getCard();

        ProfileCardEntity toProfileCard = profileCardService.getByPanAndCheck(toCardPan);

        CardEntity toCard = toProfileCard.getCard();

        if (fromCardPan.equals(toCardPan)) {
            log.warn("Incorrect Transfer {}", dto);
            throw new AppBadRequestException("Incorrect Transfer!");
        }

        if (!fromCard.getType().equals(toCard.getType())) {
            log.warn("Incorrect Currency {}", dto);
            throw new AppBadRequestException("Incorrect Currency!");
        }

        Long commission;
        if (fromCardPan.startsWith(uzcardPanType) ||
                fromCardPan.startsWith(humoCardType)) {

            if (amount < Long.parseLong(amountBorderSum)) {
                log.warn("Small Amount {}", dto);
                throw new AppBadRequestException("Small Amount!");
            }

            commission = CommissionUtil.commission(amount, Long.valueOf(afterAmountSum),
                    Double.valueOf(commissionPercentSum), Double.valueOf(afterPercentageSum));

        } else if (fromCardPan.startsWith(visaCardType)) {

            if (amount < Long.parseLong(amountBorderUsd)) {
                log.warn("Small Amount {}", dto);
                throw new AppBadRequestException("Small Amount!");
            }

            commission = CommissionUtil.commission(amount, Long.valueOf(afterAmountUsd),
                    Double.valueOf(commissionPercentUsd), Double.valueOf(afterPercentageUsd));

        } else {
            log.warn("Unknown Pan Type {}", dto);
            throw new AppBadRequestException("Unknown Pan Type!");
        }

        if (amount + commission > fromCard.getBalance()) {
            log.warn("Not Enough Money {}", dto);
            throw new AppBadRequestException("Not Enough Money!");
        }

        TransfersDTO transfersDTO = create(fromCardPan, toCardPan, amount, commission, profile, fromCard.getType());

        return transfer(transfersDTO);
    }

    public TransfersDTO transfer(TransfersDTO dto) {

        profileCardService.minusAmount(dto.getFromCardPan(), dto.getFullAmount());

        if (dto.getFromCardPan().startsWith(uzcardPanType)) {
            profileCardService.plusCommissionToUzcard(dto.getCommissionAmount());

        } else if (dto.getFromCardPan().startsWith(humoCardType)) {
            profileCardService.plusCommissionToHumoCard(dto.getCommissionAmount());

        } else if (dto.getFromCardPan().startsWith(visaCardType)) {
            profileCardService.plusCommissionToVisaCard(dto.getCommissionAmount());
        }

        try {
            transactionService.createForTransfers(dto);

            profileCardService.plusAmount(dto.getToCardPan(), dto.getAmount());

            transfersRepository.updateStatus(TransfersStatus.SUCCESS, dto.getId());

        } catch (Throwable e) {
            log.warn("Cannot Transfer {}", dto);
            transfersRepository.increaseChanceCount(dto.getId());
        }

        return toDTO(getByIdOrThrow(dto.getId()));
    }

    @Transactional
    public TransfersDTO create(String fromCardPan, String toCardPan, Long amount,
                               Long commissionAmount, ProfileEntity profile, BalanceType type) {
        TransfersEntity entity = new TransfersEntity();
        entity.setFromCardPan(fromCardPan);
        entity.setToCardPan(toCardPan);
        entity.setAmount(amount);
        entity.setCommissionAmount(commissionAmount);
        entity.setFullAmount(amount + commissionAmount);
        entity.setProfileId(profile.getId());
        entity.setType(type);
        entity.setStatus(TransfersStatus.PROCESSING);

        transfersRepository.save(entity);
        return toDTO(getByIdOrThrow(entity.getId()));
    }

    public TransfersDTO makeConversion(TransfersDTO dto) {
        ProfileEntity profile = EntityDetails.getProfile();

        dto.setAmount(dto.getAmount() * 100); // sum or usd
        Long amount = dto.getAmount();

        String fromCardPan = dto.getFromCardPan();
        String toCardPan = dto.getToCardPan();

        ProfileCardEntity fromProfileCard = profileCardService.getByProfileIdAndPanAndCheck(profile.getId(), fromCardPan);

        CardEntity fromCard = fromProfileCard.getCard();

        ProfileCardEntity toProfileCard = profileCardService.getByProfileIdAndPanAndCheck(profile.getId(), toCardPan);

        CardEntity toCard = toProfileCard.getCard();

        if (fromCardPan.equals(toCardPan) ||
                fromCard.getType().equals(toCard.getType())) {
            log.warn("Incorrect Conversion {}", dto);
            throw new AppBadRequestException("Incorrect Conversion!");
        }

        ExchangeRateDTO todayRate = exchangeRateService.getTodayRate(BalanceType.USD);

        Long commission;
        if (fromCardPan.startsWith(uzcardPanType) ||
                fromCardPan.startsWith(humoCardType)) {

            if (amount < Long.parseLong(amountBorderUsd)) {
                log.warn("Small Amount {}", dto);
                throw new AppBadRequestException("Small Amount!");
            }

            amount = amount * todayRate.getRate() / 100;

        } else if (fromCardPan.startsWith(visaCardType)) {

            if (amount < Long.parseLong(amountBorderUsd)) {
                log.warn("Small Amount {}", dto);
                throw new AppBadRequestException("Small Amount!");
            }


        } else {
            log.warn("Unknown Pan Type {}", dto);
            throw new AppBadRequestException("Unknown Pan Type!");
        }

        commission = 0L;

        if (amount + commission > fromCard.getBalance()) {
            log.warn("Not Enough Money {}", dto);
            throw new AppBadRequestException("Not Enough Money!");
        }

        TransfersDTO transfersDTO = create(fromCardPan, toCardPan, amount, commission, profile, fromCard.getType());

        return conversion(transfersDTO);
    }

    @Transactional
    public TransfersDTO conversion(TransfersDTO dto) {

        profileCardService.minusAmount(dto.getFromCardPan(), dto.getFullAmount());

        ExchangeRateDTO todayRate = exchangeRateService.getTodayRate(BalanceType.USD);

        if (dto.getFromCardPan().startsWith(uzcardPanType)) {
            profileCardService.plusCommissionToUzcard(dto.getCommissionAmount());

            dto.setAmount(dto.getAmount() / todayRate.getRate() * 100);

        } else if (dto.getFromCardPan().startsWith(humoCardType)) {
            profileCardService.plusCommissionToHumoCard(dto.getCommissionAmount());

            dto.setAmount(dto.getAmount() / todayRate.getRate() * 100);

        } else if (dto.getFromCardPan().startsWith(visaCardType)) {
            profileCardService.plusCommissionToVisaCard(dto.getCommissionAmount());

            dto.setAmount(dto.getAmount() * todayRate.getRate() / 100);
        }

        transactionService.createForConversion(dto);

        profileCardService.plusAmount(dto.getToCardPan(), dto.getAmount());

        transfersRepository.updateStatus(TransfersStatus.SUCCESS, dto.getId());

        return toDTO(getByIdOrThrow(dto.getId()));
    }

    public String getCommission(Long amount, String type) {

        amount *= 100; // sum or usd

        Long commission;
        BalanceType balanceType;

        if (type.equals("sum")) {

            commission = CommissionUtil.commission(amount, Long.valueOf(afterAmountSum),
                    Double.valueOf(commissionPercentSum), Double.valueOf(afterPercentageSum));

            balanceType = BalanceType.SUM;

        } else if (type.equals("usd")) {

            commission = CommissionUtil.commission(amount, Long.valueOf(afterAmountUsd),
                    Double.valueOf(commissionPercentUsd), Double.valueOf(afterPercentageUsd));

            balanceType = BalanceType.USD;

        } else {
            log.warn("Incorrect Type {}", type);
            throw new AppBadRequestException("Incorrect Type ex:[sum,usd]");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", NumberUtil.balanceToType(commission, balanceType));
        jsonObject.put("type", type);
        return jsonObject.toString();
    }

    public PageImpl<TransfersDTO> transfersPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<TransfersEntity> entityPage = transfersRepository.findAll(pageable);

        List<TransfersDTO> dtoList = new ArrayList<>();

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public PageImpl<TransfersDTO> transfersPaginationByStatus(int page, int size, TransfersStatusDTO dto) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<TransfersEntity> entityPage = transfersRepository.findAllByStatus(dto.getStatus(), pageable);

        List<TransfersDTO> dtoList = new ArrayList<>();

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public PageImpl<TransfersDTO> transfersPaginationByProfileId(int page, int size) {
        ProfileEntity profile = EntityDetails.getProfile();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<TransfersEntity> entityPage = transfersRepository.findAllByProfileId(profile.getId(), pageable);

        List<TransfersDTO> dtoList = new ArrayList<>();

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public TransfersEntity getByIdOrThrow(String id) {
        return transfersRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.info("Transfer Not Found {}", id);
                    return new ItemNotFoundException("Transfer Not Found!");
                });
    }

    public TransfersDTO toDTO(TransfersEntity entity) {
        TransfersDTO dto = new TransfersDTO();

        dto.setId(entity.getId());

        ProfileCardEntity fromCardProfile = profileCardService.getByPan(entity.getFromCardPan());
        ProfileCardEntity toCardProfile = profileCardService.getByPan(entity.getToCardPan());

        dto.setFromCard(profileCardService.toDTO(profileCardService.getByIdOrThrow(fromCardProfile.getId())));
        dto.setToCard(profileCardService.toDTO(profileCardService.getByIdOrThrow(toCardProfile.getId())));

        dto.setFromCardPan(entity.getFromCardPan());
        dto.setToCardPan(entity.getToCardPan());
        dto.setAmount(entity.getAmount());
        dto.setType(entity.getType());
        dto.setFullAmount(entity.getFullAmount());
        dto.setCommissionAmount(entity.getCommissionAmount());
        dto.setCommissionCash(NumberUtil.balanceToType(entity.getCommissionAmount(), entity.getType()));
        dto.setCash(NumberUtil.balanceToType(entity.getAmount(), entity.getType()));
        dto.setCreatedDate(entity.getCreatedDate());

        return dto;
    }

    @Scheduled(cron = "${cron.expression.2minutes}")
    public void checkTransfers() {

        log.info("Check Transfers");

        List<TransfersEntity> entityList = transfersRepository.findAllByStatus(TransfersStatus.PROCESSING);

        entityList.forEach(entity -> {

            try {

                transactionService.createForTransfers(toDTO(entity));

                profileCardService.plusAmount(entity.getToCardPan(), entity.getAmount());

                transfersRepository.updateStatus(TransfersStatus.SUCCESS, entity.getId());

            } catch (Throwable e) {
                log.warn("Cannot Transfer {}", entity);
                e.printStackTrace();


                if (entity.getChanceCount() == 3) {

                    transfersRepository.updateStatus(TransfersStatus.FAILED, entity.getId());

                    if (entity.getFromCardPan().startsWith(uzcardPanType)) {
                        profileCardService.rollbackCommissionFromUzcard(entity.getFromCardPan(),
                                entity.getFullAmount(), entity.getCommissionAmount());

                    } else if (entity.getFromCardPan().startsWith(humoCardType)) {
                        profileCardService.rollbackCommissionFromHumoCard(entity.getFromCardPan(),
                                entity.getFullAmount(), entity.getCommissionAmount());

                    } else if (entity.getFromCardPan().startsWith(visaCardType)) {
                        profileCardService.rollbackCommissionFromVisaCard(entity.getFromCardPan(),
                                entity.getFullAmount(), entity.getCommissionAmount());

                    }
                } else {
                    transfersRepository.increaseChanceCount(entity.getId());
                }
            }
        });
    }


}
