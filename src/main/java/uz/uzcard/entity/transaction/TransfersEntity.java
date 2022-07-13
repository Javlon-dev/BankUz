package uz.uzcard.entity.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.entity.CardEntity;
import uz.uzcard.entity.base.BaseUuidEntity;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.enums.transaction.TransfersStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@ToString
public class TransfersEntity extends BaseUuidEntity implements Serializable {

    @Column(name = "from_card_pan", nullable = false)
    private String fromCardPan;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_pan", referencedColumnName = "pan", updatable = false, insertable = false)
    private CardEntity fromCard;

    @Column(name = "to_card_pan", nullable = false)
    private String toCardPan;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_pan", referencedColumnName = "pan", updatable = false, insertable = false)
    private CardEntity toCard;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "commission_amount", nullable = false)
    private Long commissionAmount;

    @Column(name = "full_amount", nullable = false)
    private Long fullAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BalanceType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransfersStatus status;

    @Column(name = "profile_id", nullable = false)
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private ProfileEntity profile;

    @Column(name = "chance_count", nullable = false)
    private Integer chanceCount = 0;

}
