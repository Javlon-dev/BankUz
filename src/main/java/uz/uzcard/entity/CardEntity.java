package uz.uzcard.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.entity.base.BaseUuidEntity;
import uz.uzcard.entity.profile.ProfileEntity;
import uz.uzcard.enums.card.BalanceType;
import uz.uzcard.enums.card.CardStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "card", uniqueConstraints = @UniqueConstraint(columnNames = {"pan", "deleted_date"}))
@Getter
@Setter
@ToString
public class CardEntity extends BaseUuidEntity implements Serializable {

    @Column(name = "pan", nullable = false)
    private String pan;

    @Column(name = "masked_pan", nullable = false)
    private String maskedPan;

    @Column(name = "expired_date")
    private String expiredDate;

    @Column
    private Long balance = 0L;

    @Column(name = "cvv_code")
    private String cvvCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BalanceType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "deleted_date", insertable = false, updatable = false)
    private LocalDateTime deletedDate;

    @Column(name = "profile_id", nullable = false)
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "phone_number", updatable = false, insertable = false)
    private ProfileEntity profile;

    @Column(name = "bank_id", nullable = false)
    private String bankId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", referencedColumnName = "mfo_code", updatable = false, insertable = false)
    private BankEntity bank;

}
