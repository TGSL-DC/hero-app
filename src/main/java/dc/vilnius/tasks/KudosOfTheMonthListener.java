package dc.vilnius.tasks;

import com.slack.api.bolt.AppConfig;
import dc.vilnius.kudos.domain.KudosFacade;
import dc.vilnius.slack.domain.SlackMessageFacade;
import io.micronaut.context.event.ApplicationEventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KudosOfTheMonthListener implements ApplicationEventListener<HeroOfTheMonthEvent> {
  private final Logger logger = LoggerFactory.getLogger(KudosOfTheMonthListener.class);

  private final SlackMessageFacade slackMessageFacade;

  public KudosOfTheMonthListener(AppConfig appConfig, KudosFacade kudosFacade) {
    this.slackMessageFacade = new SlackMessageFacade(kudosFacade, appConfig);
  }

  @Override
  public void onApplicationEvent(HeroOfTheMonthEvent event) {
    logger.info("Consuming event: {}", event);
    slackMessageFacade.handleHeroOfTheMonth(event.channelId(), event.requestedBy(), event.date());
  }

  @Override
  public boolean supports(HeroOfTheMonthEvent event) {
    return ApplicationEventListener.super.supports(event);
  }
}
