package gr.cognitera.util.time;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;


public class ISO8601Util {

    private ISO8601Util() {}


    public static String localTimeNoOffsetAtZone(final ZoneOffset zone, final Instant instant) {
        // https://stackoverflow.com/a/27483371/274677
        final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zone);
        return formatter.format(instant);
    }


    public static String localTimeNoOffsetAtUTC(final Instant instant) {
        return localTimeNoOffsetAtZone(ZoneOffset.UTC, instant);
    }
    

    public static Instant parseISO8601NoTZ(final String value, final ZoneOffset zone) throws DateTimeParseException {
        final DateTimeFormatter TS_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
        return LocalDateTime
            .parse(value, TS_FORMATTER)
            .atOffset(zone)
            .toInstant();
    }

    public static Instant parseISO8601NoTZImplicitUTC(final String value) throws DateTimeParseException {
        return parseISO8601NoTZ(value, ZoneOffset.UTC);
    }    

    public static LocalDate parseYYYYMMDD(final String value) throws DateTimeParseException {
        final DateTimeFormatter TS_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        return LocalDate
            .parse(value, TS_FORMATTER);

    }
    

}

