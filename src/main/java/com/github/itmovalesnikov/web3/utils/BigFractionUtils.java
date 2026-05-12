package com.github.itmovalesnikov.web3.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.math3.fraction.BigFraction;

/**
 * Utility class for converting between BigFraction and String representations.
 * Provides methods to convert fractions to decimal strings and vice versa,
 * as well as checking if a fraction has a terminating decimal representation.
 */
public class BigFractionUtils {
    
    /**
     * Creates a BigFraction from a decimal or fractional string representation.
     * Supports both decimal notation (e.g. "0.5") and fraction notation (e.g. "1/2").
     *
     * @param str String representation of the number
     * @return BigFraction object representing the input string
     * @throws IllegalArgumentException if the string format is invalid
     */
    public static BigFraction fromDecimalString(String str) {
        str = str.trim();
        if (str.contains("/")) {
            String[] parts = str.split("/");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid fraction format: " + str);
            }
            BigInteger numerator = new BigInteger(parts[0].trim());
            BigInteger denominator = new BigInteger(parts[1].trim());
            return new BigFraction(numerator, denominator);
        } else {
            BigDecimal bd = new BigDecimal(str);
            return new BigFraction(bd.unscaledValue(), BigInteger.TEN.pow(bd.scale()));
        }
    }

    /**
     * Converts a BigFraction to its decimal or fractional string representation.
     * If the fraction has a terminating decimal, returns the decimal representation.
     * Otherwise, returns the fraction in reduced form (numerator/denominator).
     *
     * @param fraction The BigFraction to convert
     * @return String representation of the fraction
     */
    public static String toDecimalString(BigFraction fraction) {
        BigInteger num = fraction.getNumerator();
        BigInteger den = fraction.getDenominator();

        BigInteger gcd = num.gcd(den);
        num = num.divide(gcd);
        den = den.divide(gcd);

        if (isTerminating(fraction)) {
            BigDecimal numerator = new BigDecimal(num);
            BigDecimal denominator = new BigDecimal(den);
            BigDecimal result = numerator.divide(denominator);
            return result.stripTrailingZeros().toPlainString();
        } else {
            return num + "/" + den;
        }
    }

    /**
     * Checks if a BigFraction has a terminating decimal representation.
     * A fraction in reduced form has a terminating decimal if and only if
     * the prime factorization of its denominator contains no primes other than 2 and 5.
     *
     * @param fraction The BigFraction to check
     * @return true if the fraction has a terminating decimal representation, false otherwise
     */
    public static boolean isTerminating(BigFraction fraction) {
        BigInteger den = fraction.getDenominator();
        BigInteger gcd = fraction.getNumerator().gcd(den);
        den = den.divide(gcd);

        while (den.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            den = den.divide(BigInteger.TWO);

        while (den.mod(BigInteger.valueOf(5)).equals(BigInteger.ZERO))
            den = den.divide(BigInteger.valueOf(5));

        return den.equals(BigInteger.ONE);
    }
}