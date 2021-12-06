package dc.vilnius.slack;

import static dc.vilnius.slack.domain.HeroesOfTheMonthDateUtil.heroesLeaderBoardAvailableFrom;
import static dc.vilnius.slack.domain.HeroesOfTheMonthDateUtil.isAllowedToRevealHeroesLeaderboard;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import dc.vilnius.kudos.domain.KudosFacade;
import dc.vilnius.kudos.dto.GiveKudos;
import dc.vilnius.slack.domain.MessageGenerator;
import dc.vilnius.slack.domain.SlackMessageFacade;
import io.micronaut.context.annotation.Factory;
import java.time.LocalDate;
import java.util.stream.Collectors;
import javax.inject.Singleton;

@Factory
public class SlackFactory {

  @Singleton
  public AppConfig createAppConfig() {
    return new AppConfig();
  }

  @Singleton
  public App createApp(AppConfig appConfig, KudosFacade kudosFacade) {
    App app = new App(appConfig);
    var slackMessageFacade = new SlackMessageFacade(kudosFacade, appConfig);

    app.command("/hero-ping", (req, ctx) -> {
      return ctx.ack("pong");
    });

    app.command("/hero-vote", (req, ctx) -> {
      var commandArgText = req.getPayload().getText();
      var channelId = req.getPayload().getChannelId();
      var userId = req.getPayload().getUserId();
      var parsedMessage = slackMessageFacade.parseMessage(commandArgText);
      if (parsedMessage.users().contains(userId)) {
        return ctx.ack("Really? No cheating mate!");
      }
      var kudos = new GiveKudos(channelId, parsedMessage.users(), parsedMessage.message());
      slackMessageFacade.handleHeroVote(kudos);
      var successMessages = parsedMessage.users().stream()
          .map(MessageGenerator::randomSuccessMessage).collect(
              Collectors.joining(";"));

      return ctx.ack(successMessages);
    });

    app.command("/heroes-of-the-month", (req, ctx) -> {
      var channelId = req.getPayload().getChannelId();
      var userId = req.getPayload().getUserId();

      var currentDate = LocalDate.now();
      if (isAllowedToRevealHeroesLeaderboard(currentDate)) {
        slackMessageFacade.handleHeroOfTheMonth(channelId, userId);
        return ctx.ack("Working on it!");
      } else {
        return ctx.ack("It's too soon to reveal this month heroes! Available from: "
            + heroesLeaderBoardAvailableFrom(currentDate));
      }
    });

    return app;
  }
}
