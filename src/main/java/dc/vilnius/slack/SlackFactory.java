package dc.vilnius.slack;

import static dc.vilnius.slack.domain.HeroesOfTheMonthDateUtil.heroesLeaderBoardAvailableFrom;
import static dc.vilnius.slack.domain.HeroesOfTheMonthDateUtil.isAllowedToRevealHeroesLeaderboard;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import dc.vilnius.slack.domain.CommandParser;
import dc.vilnius.slack.domain.MessageGenerator;
import dc.vilnius.tasks.GetKudosOfTheMonthEmitter;
import dc.vilnius.tasks.GetKudosOfTheYearEmitter;
import dc.vilnius.tasks.SubmitKudosEmitter;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

@Factory
public class SlackFactory {

  @Singleton
  public AppConfig createAppConfig() {
    return new AppConfig();
  }

  @Singleton
  public App createApp(AppConfig appConfig, GetKudosOfTheMonthEmitter getKudosOfTheMonthEmitter,
      GetKudosOfTheYearEmitter getKudosOfTheYearEmitter, SubmitKudosEmitter submitKudosEmitter) {
    App app = new App(appConfig);

    app.command("/hero-ping", (req, ctx) -> ctx.ack("pong"));

    app.command("/hero-vote", (req, ctx) -> {
      var commandArgText = req.getPayload().getText();
      var channelId = req.getPayload().getChannelId();
      var userId = req.getPayload().getUserId();
      var parsedMessage = CommandParser.parse(commandArgText);
      if (parsedMessage.users().contains(userId)) {
        return ctx.ack("Really? No cheating mate!");
      }

      submitKudosEmitter.publish(channelId, parsedMessage.users(), parsedMessage.message());
      var successMessages = parsedMessage.users().stream()
          .map(MessageGenerator::randomSuccessMessage).collect(
              Collectors.joining(";"));

      return ctx.ack(successMessages);
    });

    app.command("/heroes-of-the-month", (req, ctx) -> {
      var channelId = req.getPayload().getChannelId();
      var userId = req.getPayload().getUserId();
      var commandArgText = req.getPayload().getText();
      var date = Optional.ofNullable(commandArgText)
          .map(LocalDate::parse).orElse(LocalDate.now());
      if (isAllowedToRevealHeroesLeaderboard(date)) {
        getKudosOfTheMonthEmitter.publish(channelId, userId, date);
        return ctx.ack("Working on it! Loading heroes of the month...");
      } else {
        return ctx.ack("It's too early to reveal heroes! Available from: "
            + heroesLeaderBoardAvailableFrom(date));
      }
    });

    app.command("/heroes-of-the-year", (req, ctx) -> {
      var channelId = req.getPayload().getChannelId();
      var userId = req.getPayload().getUserId();
      getKudosOfTheYearEmitter.publish(channelId, userId);
      return ctx.ack("Working on it! Loading heroes of the year...");
    });

    return app;
  }
}
