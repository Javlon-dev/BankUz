package uz.uzcard.entity.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.entity.base.BaseLongEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "profile_detail")
@Getter
@Setter
@ToString
public class ProfileDetailEntity extends BaseLongEntity implements Serializable {

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "sms_code")
    private String smsCode;

}
