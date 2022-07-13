package uz.uzcard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.annotation.ValidMfo;
import uz.uzcard.dto.base.BaseUuidDTO;
import uz.uzcard.dto.profile.ProfileDTO;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankDTO extends BaseUuidDTO {

    @ValidMfo(message = "MfoCode required")
    private String mfoCode;

    private ProfileDTO profile;

}
