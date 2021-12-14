package dc.vilnius.tasks;

import com.slack.api.bolt.AppConfig;
import dc.vilnius.kudos.domain.KudosFacade;
import dc.vilnius.kudos.dto.GiveKudos;
import dc.vilnius.slack.domain.SlackMessageFacade;
import io.micronaut.context.event.ApplicationEventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GivenKudosListener implements ApplicationEventListener<SubmitKudosEvent> {
  private final Logger logger = LoggerFactory.getLogger(GivenKudosListener.class);

  private final SlackMessageFacade slackMessageFacade;

  public GivenKudosListener(AppConfig appConfig, KudosFacade kudosFacade) {
    this.slackMessageFacade = new SlackMessageFacade(kudosFacade, appConfig);
  }

  @Override
  public void onApplicationEvent(SubmitKudosEvent event) {
    logger.info("Consuming event: {}", event);
    var kudos = new GiveKudos(event.channelId(), event.usernames(), event.message());
    slackMessageFacade.handleHeroVote(kudos);
  }

  @Override
  public boolean supports(SubmitKudosEvent event) {
    return ApplicationEventListener.super.supports(event);
  }
}
