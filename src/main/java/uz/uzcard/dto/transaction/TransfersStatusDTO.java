package uz.uzcard.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzcard.enums.transaction.TransfersStatus;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransfersStatusDTO {

    @NotNull(message = "Status required [SUCCESS, PROCESSING, FAILED]")
    private TransfersStatus status;

}
