package uz.uzcard.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.annotation.ValidPhone;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDetailDTO {

    @ValidPhone(message = "PhoneNumber required")
    private String phoneNumber;

    private String smsCode;

    private String sms;

    private String token;

}
