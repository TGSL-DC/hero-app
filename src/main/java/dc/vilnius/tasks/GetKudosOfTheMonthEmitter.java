package dc.vilnius.tasks;

import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GetKudosOfTheMonthEmitter {

  private final Logger logger = LoggerFactory.getLogger(GetKudosOfTheMonthEmitter.class);

  @Inject
  ApplicationEventPublisher<HeroOfTheMonthEvent> eventPublisher;

  public void publish(String channelId, String requestBy, LocalDate date) {
    logger.info("Publishing message to {}, by {} at {}", channelId, requestBy, date);
    eventPublisher.publishEventAsync(new HeroOfTheMonthEvent(channelId, requestBy, date));
  }
}
