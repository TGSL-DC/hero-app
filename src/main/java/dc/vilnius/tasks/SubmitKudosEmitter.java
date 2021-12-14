package dc.vilnius.tasks;

import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SubmitKudosEmitter {

  private final Logger logger = LoggerFactory.getLogger(SubmitKudosEmitter.class);

  @Inject
  ApplicationEventPublisher<SubmitKudosEvent> eventPublisher;

  public void publish(String channelId, List<String> usernames, String message) {
    logger.info("Publishing message to {} channel", channelId);
    eventPublisher.publishEventAsync(new SubmitKudosEvent(channelId, usernames, message));
  }
}
