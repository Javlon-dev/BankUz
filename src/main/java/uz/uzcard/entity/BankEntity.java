package uz.uzcard.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.entity.base.BaseUuidEntity;
import uz.uzcard.entity.profile.ProfileEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bank")
@Getter
@Setter
@ToString
public class BankEntity extends BaseUuidEntity implements Serializable {

    @Column(name = "mfo_code", nullable = false)
    private String mfoCode;

    @Column(name = "profile_id", nullable = false)
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "phone_number", updatable = false, insertable = false)
    private ProfileEntity profile;

}
