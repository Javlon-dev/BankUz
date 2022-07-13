package uz.uzcard.entity.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.entity.CardEntity;
import uz.uzcard.entity.base.BaseUuidEntity;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.enums.transaction.TransactionStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transaction_history")
@Getter
@Setter
@ToString
public class TransactionEntity extends BaseUuidEntity implements Serializable {

    @Column(name = "card_pan", nullable = false)
    private String cardPan;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_pan", referencedColumnName = "pan", updatable = false, insertable = false)
    private CardEntity card;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BalanceType type;

    @Column(name = "transfers_id", nullable = false)
    private String transfersId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfers_id", updatable = false, insertable = false)
    private TransfersEntity transfers;

}
