package uz.uzcard.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.dto.BankDTO;
import uz.uzcard.dto.base.BaseUuidDTO;
import uz.uzcard.dto.profile.ProfileDTO;
import uz.uzcard.enums.card.CardStatus;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDTO extends BaseUuidDTO {

    private String pan;

    private String maskedPan;

    private String expiredDate;

    private Long balance;

    private String cvvCode;

    private String cash;

    private CardStatus status;

    private ProfileDTO profile;

    private BankDTO bank;

}
