package uz.uzcard.service.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzcard.dto.transaction.TransactionDTO;
import uz.uzcard.dto.transaction.TransactionStatusDTO;
import uz.uzcard.dto.transaction.TransfersDTO;
import uz.uzcard.entity.profile.ProfileCardEntity;
import uz.uzcard.entity.transaction.TransactionEntity;
import uz.uzcard.entity.transaction.TransfersEntity;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.enums.transaction.TransactionStatus;
import uz.uzcard.repository.transaction.TransactionRepository;
import uz.uzcard.service.profile.ProfileCardService;
import uz.uzcard.service.rest.rate.ExchangeRateService;
import uz.uzcard.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final ProfileCardService profileCardService;

    @Autowired
    private TransfersService transfersService;

    @Value("${bank.card.uzcard}")
    private String uzcardPan;

    @Value("${bank.card.humo}")
    private String humoCardPan;

    @Value("${bank.card.visa}")
    private String visaCardPan;

    @Value("${card.type.uzcard}")
    private String uzcardPanType;

    @Value("${card.type.humo}")
    private String humoCardType;

    @Value("${card.type.visa}")
    private String visaCardType;

    @Transactional
    public void createForTransfers(TransfersDTO dto) {
        createCredit(dto);
        createDebit(dto);
        createCompanyDebit(dto);
    }

    @Transactional
    public void createForConversion(TransfersDTO dto) {
        createCredit(dto);
        createCompanyDebit(dto);

        if (dto.getFromCardPan().startsWith(uzcardPanType) ||
                dto.getFromCardPan().startsWith(humoCardType)) {

            dto.setType(BalanceType.USD);

            createDebit(dto);

        } else if (dto.getFromCardPan().startsWith(visaCardType)) {

            dto.setType(BalanceType.SUM);

            createDebit(dto);
        }

    }

    private void createCredit(TransfersDTO dto) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransfersId(dto.getId());
        entity.setCardPan(dto.getFromCardPan());
        entity.setAmount(dto.getFullAmount());
        entity.setType(dto.getType());
        entity.setStatus(TransactionStatus.CREDIT);

        transactionRepository.save(entity);
    }

    private void createDebit(TransfersDTO dto) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransfersId(dto.getId());
        entity.setCardPan(dto.getToCardPan());
        entity.setAmount(dto.getAmount());
        entity.setType(dto.getType());
        entity.setStatus(TransactionStatus.DEBIT);

        transactionRepository.save(entity);
    }

    private void createCompanyDebit(TransfersDTO dto) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransfersId(dto.getId());

        if (dto.getFromCardPan().startsWith(uzcardPanType)) {
            entity.setCardPan(uzcardPan);

        } else if (dto.getFromCardPan().startsWith(humoCardType)) {
            entity.setCardPan(humoCardPan);

        } else if (dto.getFromCardPan().startsWith(visaCardType)) {
            entity.setCardPan(visaCardPan);

        }

        entity.setType(dto.getType());
        entity.setAmount(dto.getCommissionAmount());
        entity.setStatus(TransactionStatus.DEBIT);

        transactionRepository.save(entity);
    }

    public PageImpl<TransactionDTO> transactionPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<TransactionEntity> entityPage = transactionRepository.findAll(pageable);

        List<TransactionDTO> dtoList = new ArrayList<>();

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public PageImpl<TransactionDTO> transactionPaginationByStatus(int page, int size, TransactionStatusDTO dto) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<TransactionEntity> entityPage = transactionRepository.findAllByStatus(dto.getStatus(), pageable);

        List<TransactionDTO> dtoList = new ArrayList<>();

        entityPage.forEach(entity -> {
            dtoList.add(toDTO(entity));
        });

        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public TransactionDTO toDTO(TransactionEntity entity) {
        if (entity.getCardPan().equals(uzcardPan) ||
                entity.getCardPan().equals(humoCardPan) ||
                entity.getCardPan().equals(visaCardPan)) {
            return null;
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setAmount(entity.getAmount());
        dto.setCash(NumberUtil.balanceToType(entity.getAmount(), entity.getType()));
        dto.setType(entity.getType());
        dto.setCardPan(entity.getCardPan());
        dto.setCreatedDate(entity.getCreatedDate());


        ProfileCardEntity cardProfile = profileCardService.getByPan(entity.getCardPan());

        dto.setCard(profileCardService.toDTO(profileCardService.getByIdOrThrow(cardProfile.getId())));

        dto.setTransfers(transfersService.toDTO(entity.getTransfers()));

        return dto;
    }

}
