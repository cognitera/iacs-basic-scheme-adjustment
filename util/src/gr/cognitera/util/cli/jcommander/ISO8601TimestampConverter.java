package gr.cognitera.util.cli.jcommander;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.IStringConverter;

import gr.cognitera.util.time.ISO8601Util;


public class ISO8601TimestampConverter implements IStringConverter<Instant> {


    @Override
    public Instant convert(String value) {
        try {
            return ISO8601Util.parseISO8601NoTZImplicitUTC(value);
        } catch (DateTimeParseException e) {
            throw new ParameterException("Invalid timestamp");
        }
    }
}
