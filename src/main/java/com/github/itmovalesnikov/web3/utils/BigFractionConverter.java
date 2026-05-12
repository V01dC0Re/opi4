package com.github.itmovalesnikov.web3.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.math3.fraction.BigFraction;

/**
 * Converts BigFraction objects to and from String representations for database storage.
 * This converter is used by JPA to persist BigFraction objects in the database.
 */
@Converter
public class BigFractionConverter implements AttributeConverter<BigFraction, String> {

    /**
     * Converts a BigFraction object to its String representation for database storage.
     *
     * @param fraction The BigFraction object to convert
     * @return String representation of the fraction
     */
    @Override
    public String convertToDatabaseColumn(BigFraction fraction) {
        return BigFractionUtils.toDecimalString(fraction);
    }

    /**
     * Converts a String representation from the database back to a BigFraction object.
     *
     * @param dbData The String representation from the database
     * @return BigFraction object reconstructed from the string
     */
    @Override
    public BigFraction convertToEntityAttribute(String dbData) {
        return BigFractionUtils.fromDecimalString(dbData);
    }
}