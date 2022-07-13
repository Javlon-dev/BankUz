package uz.uzcard.validator;

import uz.uzcard.annotation.ValidMfo;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MfoValidator implements ConstraintValidator<ValidMfo, String> {

    private static final
    String MFO_PATTERN = "([0-9]{6})";


    @Override
    public void initialize(ValidMfo constraintAnnotation) {
    }

    @Override
    public boolean isValid(String mfoCode, ConstraintValidatorContext constraintValidatorContext) {
        return (validateMfo(mfoCode));
    }

    private boolean validateMfo(String mfoCode) {
        Pattern pattern = Pattern.compile(MFO_PATTERN);
        if (Optional.ofNullable(mfoCode).isPresent()) {
            Matcher matcher = pattern.matcher(mfoCode);
            return matcher.matches();
        }
        return false;
    }

}
