package dc.vilnius.tasks;

import com.slack.api.bolt.AppConfig;
import dc.vilnius.kudos.domain.KudosFacade;
import dc.vilnius.slack.domain.SlackMessageFacade;
import io.micronaut.context.event.ApplicationEventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KudosOfTheYearListener implements ApplicationEventListener<HeroOfTheYearEvent> {
  private final Logger logger = LoggerFactory.getLogger(KudosOfTheYearListener.class);

  private final SlackMessageFacade slackMessageFacade;

  public KudosOfTheYearListener(AppConfig appConfig, KudosFacade kudosFacade) {
    this.slackMessageFacade = new SlackMessageFacade(kudosFacade, appConfig);
  }

  @Override
  public void onApplicationEvent(HeroOfTheYearEvent event) {
    logger.info("Consuming event: {}", event);
    slackMessageFacade.handleHeroOfTheYear(event.channelId(), event.requestedBy());
  }

  @Override
  public boolean supports(HeroOfTheYearEvent event) {
    return ApplicationEventListener.super.supports(event);
  }
}
