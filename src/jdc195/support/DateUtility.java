package jdc195.support;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class DateUtility {

  public static ZonedDateTime getCurrentZonedDateTime() {
    return ZonedDateTime.ofInstant(Instant.now().truncatedTo(ChronoUnit.MILLIS), ZoneId.of("UTC"));
  }

  public static ZonedDateTime convertToLocal(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).atZone(ZoneId.systemDefault());
  }

}
