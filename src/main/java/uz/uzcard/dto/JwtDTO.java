package uz.uzcard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtDTO {

    private String subject;

    public JwtDTO(String subject) {
        this.subject = subject;
    }

}
