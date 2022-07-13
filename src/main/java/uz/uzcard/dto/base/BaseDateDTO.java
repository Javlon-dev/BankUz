package uz.uzcard.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDateDTO {

    protected LocalDateTime createdDate;

    protected LocalDateTime updatedDate;

}
