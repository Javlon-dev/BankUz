package uz.uzcard.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.dto.base.BaseUuidDTO;
import uz.uzcard.dto.card.CardDTO;
import uz.uzcard.enums.card.BalanceType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransfersDTO extends BaseUuidDTO {

    @NotBlank(message = "FromCardPan required")
    private String fromCardPan;
    private CardDTO fromCard;

    @NotBlank(message = "ToCardPan required")
    private String toCardPan;
    private CardDTO toCard;

    @Positive(message = "Amount must be positive number")
    @NotNull(message = "Amount cannot be null")
    private Long amount;

    private BalanceType type;

    private Long fullAmount;

    private Long commissionAmount;

    private String cash;

    private String commissionCash;

}
