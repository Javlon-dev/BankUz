package uz.uzcard.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisaPanDTO {

    @NotBlank(message = "Pan required")
    private String pan;

    @NotBlank(message = "CvvCode required")
    private String cvvCode;

}
