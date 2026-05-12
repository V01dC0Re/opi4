package com.github.itmovalesnikov.web3.validator;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.application.FacesMessage;

import java.math.BigDecimal;

/**
 * JSF validator for Y coordinate values.
 * Ensures that the Y coordinate value is within the range [-5, 5].
 */
@FacesValidator("yValueValidator")
public class YValueValidator implements Validator<String> {

    /**
     * Validates that the provided Y coordinate value is within the acceptable range [-5, 5].
     * The method checks if the value is not empty and within the range [-5, 5], 
     * catching any parsing errors, and wraps all errors in a ValidatorException.
     *
     * @param context The FacesContext of the current request
     * @param component The UI component being validated
     * @param value The string value to validate
     * @throws ValidatorException if the value is null, blank, outside the range [-5, 5], or not a valid number
     */
    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
        try {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("The field cannot be empty");
            }
            var val = new BigDecimal(value);
            if (val.compareTo(new BigDecimal(-5)) < 0 || val.compareTo(new BigDecimal(5)) > 0) {
                throw new IllegalArgumentException("The value must be in [-5, 5]");
            }
        } catch (Exception e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Format error", e.getMessage());
            throw new ValidatorException(msg);
        }
    }
}