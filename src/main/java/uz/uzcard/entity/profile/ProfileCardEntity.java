package uz.uzcard.entity.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.entity.base.BaseLongEntity;
import uz.uzcard.entity.CardEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "profile_card")
@Getter
@Setter
@ToString
public class ProfileCardEntity extends BaseLongEntity implements Serializable {

    @Column(name = "profile_id", nullable = false)
    private String profileId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", updatable = false, insertable = false)
    private ProfileEntity profile;

    @Column(name = "card_id", nullable = false)
    private String cardId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", updatable = false, insertable = false)
    private CardEntity card;

}
