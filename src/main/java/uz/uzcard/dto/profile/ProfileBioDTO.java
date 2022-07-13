package uz.uzcard.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileBioDTO {

    @NotBlank(message = "Name required")
    @Length(min = 3, message = "Name length must be between 3 to more than")
    private String name;

    @NotBlank(message = "Surname required")
    @Length(min = 3, message = "Surname length must be between 3 to more than")
    private String surname;

}
