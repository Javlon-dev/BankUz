package uz.uzcard.entity.rest.rate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.entity.base.BaseLongEntity;
import uz.uzcard.enums.card.BalanceType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "exchange_rate")
@Getter
@Setter
@ToString
public class ExchangeRateEntity extends BaseLongEntity implements Serializable {

    @Column
    @Enumerated(EnumType.STRING)
    private BalanceType type;

    @Column
    private Long rate;

}
