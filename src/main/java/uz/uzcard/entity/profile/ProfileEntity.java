package uz.uzcard.entity.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.entity.base.BaseUuidEntity;
import uz.uzcard.enums.profile.ProfileRole;
import uz.uzcard.enums.profile.ProfileStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile", uniqueConstraints = @UniqueConstraint(columnNames = {"phone_number", "deleted_date"}))
@Getter
@Setter
@ToString
public class ProfileEntity extends BaseUuidEntity implements Serializable {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileStatus status;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

}
