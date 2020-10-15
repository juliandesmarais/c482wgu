package jdc195.support;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateUtility {

  public static ZonedDateTime getCurrentZonedDateTimeInUTC() {
    return ZonedDateTime.ofInstant(Instant.now().truncatedTo(ChronoUnit.MILLIS), ZoneId.of("UTC"));
  }

  public static ZonedDateTime getConvertedZonedDateTime(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).atZone(ZoneId.systemDefault());
  }

  public static LocalDateTime getConvertedLocalDateTime(ZonedDateTime zonedDateTime) {
    return getConvertedZonedDateTime(zonedDateTime.toLocalDateTime()).toLocalDateTime();
  }

  public static LocalDateTime getCurrentLocalDateTime() {
    return getConvertedLocalDateTime(getCurrentZonedDateTimeInUTC());
  }

  public static int getWeekOfYearFromLocalDateTime(LocalDateTime localDateTime) {
    TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    return localDateTime.get(weekOfYear);
  }

}
