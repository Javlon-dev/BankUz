package uz.uzcard.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.annotation.ValidPhone;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfilePhoneNumberDTO {

    @ValidPhone(message = "PhoneNumber required")
    private String phoneNumber;

}

