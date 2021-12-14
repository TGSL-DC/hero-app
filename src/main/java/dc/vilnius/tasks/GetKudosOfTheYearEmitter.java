package dc.vilnius.tasks;

import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GetKudosOfTheYearEmitter {

  private final Logger logger = LoggerFactory.getLogger(GetKudosOfTheYearEmitter.class);

  @Inject
  ApplicationEventPublisher<HeroOfTheYearEvent> eventPublisher;

  public void publish(String channelId, String requestBy) {
    logger.info("Publishing message to {}, by {}", channelId, requestBy);
    eventPublisher.publishEventAsync(new HeroOfTheYearEvent(channelId, requestBy));
  }
}
