package com.seva.seva_mobile_version.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class BrNumberValidator implements ConstraintValidator<ValidBrNumber, String> {

    private static final Pattern BR_NUMBER_PATTERN = Pattern.compile(
            "^(?:PV|PQ|GA|GS|NW|NC|SG|W|C|S|N|E|U)/?[0-9]{4,10}$",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return BR_NUMBER_PATTERN.matcher(value.trim()).matches();
    }
}
