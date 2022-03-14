package gr.cognitera.util.orcid;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.junit.Assert;

public class ORCIDValidator {

    private ORCIDValidator() {}

    public static ORCIDValidationFailure validationFailure(final String orcid) {
        if (!isWellFormed(orcid))
            return ORCIDValidationFailure.MALFORMED;
        if (!isCheckdigitCorrect(orcid))
            return ORCIDValidationFailure.CHECKSUM_FAILURE;
        return null;
    }

    public static boolean isWellFormed(String orcid) {
        Assert.assertNotNull(orcid);        
        Pattern p = Pattern.compile("^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[0-9X]$");
        Matcher m = p.matcher(orcid);
        return m.matches();
    }

    public static boolean isCheckdigitCorrect(String orcid) {
        Assert.assertNotNull(orcid);
        if (!isWellFormed(orcid))
            throw new IllegalArgumentException(String.format("You are supposed to invoke this method with well-formed ORCIDs. ORCID [%s] is malformed"
                                                             , orcid));
        Assert.assertEquals(4*4+3,orcid.length());
        String checkSumS = orcid.substring(orcid.length()-1);
        return checkSumS.equals(calculateCheckDigitOnFullHypenatedORCID(orcid));
    }

    /** 
     * Calculates the check digit of a full, hypenated ORCID in the
     * ####-####-####-#### format ignoring the ORCID-supplied 
     * check digit.
     *
     * The implementation piggybacks on top of the
     * {@link #generateCheckDigit(String) generateCheckDigit} method
     *
     * @param orcid the ORCID to check for well-formedness
     * @return the check digit for the given ORCID
     * 
     */
    public static String calculateCheckDigitOnFullHypenatedORCID(String orcid) {
        Assert.assertNotNull(orcid);
        if (!isWellFormed(orcid))
            throw new IllegalArgumentException(String.format("You are supposed to invoke this method with well-formed ORCIDs. ORCID [%s] is malformed"
                                                             , orcid));
        Assert.assertEquals(4*4+3,orcid.length());
        return generateCheckDigit(orcid.substring(0, orcid.length()-1).replace("-", ""));
    }

    /** 
     * Generates check digit as per ISO/IEC 7064:2003, MOD 11-2
     *
     * @param baseDigits the base digits for the check digit calculation
     * @return the check digit
     * 
     */
    public static String generateCheckDigit(String baseDigits) {
        int total = 0;
        for (int i = 0; i < baseDigits.length(); i++) {
            int digit = Character.getNumericValue(baseDigits.charAt(i));
            total = (total + digit) * 2;
        }
        int remainder = total % 11;
        int result = (12 - remainder) % 11;
        return result == 10 ? "X" : String.valueOf(result);
    }

}
