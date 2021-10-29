package dc.vilnius.slack.domain;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class HeroesOfTheMonthDateUtil {

  private HeroesOfTheMonthDateUtil() {}

  public static boolean isAllowedToRevealHeroesLeaderboard(LocalDate date) {
    var lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);
    var allowedDate = heroesLeaderBoardAvailableFrom(date);
    var isAllowed = date.isAfter(allowedDate) && date.isBefore(lastDayOfMonth);
    return isAllowed;
  }

  public static LocalDate heroesLeaderBoardAvailableFrom(LocalDate date) {
    return date.with(TemporalAdjusters.lastDayOfMonth()).minusDays(5);
  }
}
