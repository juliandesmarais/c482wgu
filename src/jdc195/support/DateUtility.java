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

  public static LocalDateTime getCurrentLocalDateTime() {
    return LocalDateTime.now();
    //    return convertZonedDateTimeToSystemDefaultLDT(getCurrentZonedDateTimeInUTC());
  }

  /**
   * Converts the given LocalDateTime object (assuming it is in UTC) to a ZonedDateTime object in the System Default time zone.
   * Use when reading date fields retrieved from the database and populating data models (which use ZonedDateTime).
   *
   * @param localDateTime The LocalDateTime object to convert, in UTC.
   * @return The ZonedDateTime object that was converted to the system default time zone.
   */
  public static ZonedDateTime convertLocalDateTimeToSystemDefaultZDT(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).atZone(ZoneId.systemDefault());
  }

  public static LocalDateTime convertLocalDateTimeToSystemDefaultLDT(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static int getWeekOfYearFromLocalDateTime(LocalDateTime localDateTime) {
    TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    return localDateTime.get(weekOfYear);
  }

}
