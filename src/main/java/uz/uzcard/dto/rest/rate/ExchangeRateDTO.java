package uz.uzcard.dto.rest.rate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.enums.card.BalanceType;

@Getter
@Setter
@ToString
public class ExchangeRateDTO {

    private BalanceType type;

    private Long rate;

}
