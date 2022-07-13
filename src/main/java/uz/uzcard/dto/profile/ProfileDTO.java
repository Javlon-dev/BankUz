package uz.uzcard.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import uz.uzcard.annotation.ValidPhone;
import uz.uzcard.dto.base.BaseUuidDTO;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ProfileDTO extends BaseUuidDTO {

    @NotBlank(message = "Name required")
    @Length(min = 3, message = "Name length must be between 3 to more than")
    private String name;

    @NotBlank(message = "Surname required")
    @Length(min = 3, message = "Surname length must be between 3 to more than")
    private String surname;

    @ValidPhone(message = "PhoneNumber required")
    private String phoneNumber;

    private String token;

    public ProfileDTO(String name, String surname, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }

    public ProfileDTO(String phoneNumber, String token) {
        this.phoneNumber = phoneNumber;
        this.token = token;
    }
}
