package dc.vilnius.tasks;

import java.time.LocalDate;

public record HeroOfTheMonthEvent(String channelId, String requestedBy, LocalDate date) {}
