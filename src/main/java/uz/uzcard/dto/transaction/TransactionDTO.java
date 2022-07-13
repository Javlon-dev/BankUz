package uz.uzcard.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.dto.base.BaseUuidDTO;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.enums.transaction.TransactionStatus;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO extends BaseUuidDTO implements Serializable {

    private String cardPan;

    private CardDTO card;

    private TransactionStatus status;

    private BalanceType type;

    private Long amount;

    private String cash;

    private TransfersDTO transfers;

}
