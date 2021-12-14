package dc.vilnius.slack.domain;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.chat.ChatScheduleMessageRequest;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.composition.TextObject;
import dc.vilnius.kudos.domain.KudosFacade;
import dc.vilnius.kudos.dto.GiveKudos;
import dc.vilnius.kudos.dto.KudosDto;
import dc.vilnius.slack.dto.SlackMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class SlackMessageFacade {

  private static final int MAX_FIELDS_COUNT = 10;
  private final Logger logger = LoggerFactory.getLogger(SlackMessageFacade.class);

  private final KudosFacade kudosFacade;
  private final AppConfig appConfig;
  private final App slackApp;

  public SlackMessageFacade(KudosFacade kudosFacade, AppConfig appConfig) {
    this.kudosFacade = kudosFacade;
    this.appConfig = appConfig;
    this.slackApp = new App(appConfig);
  }

  private void scheduleMessageAtTheEndOfTheMonth(String user, String message) {
    var currentDate = LocalDate.now();
    var lastFriday = currentDate.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY));
    var messageDeliveryDay =
        currentDate.isEqual(lastFriday) || currentDate.isAfter(lastFriday) ? currentDate.plusDays(1)
            : lastFriday;
    var deliveryDayAtTen = messageDeliveryDay.atTime(10, 0);

    int postAt = (int) deliveryDayAtTen.toEpochSecond(ZoneOffset.UTC);
    try {
      var scheduledMessage = ChatScheduleMessageRequest.builder()
          .channel(user)
          .text(message)
          .postAt(postAt)
          .token(appConfig.getSingleTeamBotToken())
          .build();
      var response = slackApp.client().chatScheduleMessage(scheduledMessage);
      if (response.isOk()) {
        logger.info("Scheduled a private message {} for user {}", response.getScheduledMessageId(),
            user);
      } else {
        logger.error("Failed to schedule a message for user {}, reason: {}", user,
            response.getError());
      }
    } catch (IOException | SlackApiException e) {
      logger.error("Failed to schedule message for user: {}", user, e);
    }
  }

  private void buildAndPostHeroesLeaderboard(String channelId, String requestedBy, List<LayoutBlock> blocks, Map<String, List<KudosDto>> sortedHeroesByMessageSize) {
    blocks.addAll(buildBlocks(sortedHeroesByMessageSize));
    blocks.addAll(requestedByMessage(requestedBy));
    ChatPostMessageRequest message = ChatPostMessageRequest.builder()
        .channel(channelId)
        .token(appConfig.getSingleTeamBotToken())
        .blocks(blocks)
        .build();
    try {
      var response = slackApp.client().chatPostMessage(message);
      if (response.isOk()) {
        logger.info("Posted successfully heroes of the month in the channel {}", channelId);
      } else {
        logger.error("Failed to post a message in the channel {}, reason: {}", channelId,
            response.getError());
      }
    } catch (IOException | SlackApiException e) {
      logger.error("Failed to post hero of the month", e);
    }
  }

  public void handleHeroVote(GiveKudos giveKudos) {
    kudosFacade.submit(giveKudos);

    giveKudos.usernames()
        .forEach(user -> scheduleMessageAtTheEndOfTheMonth(user, giveKudos.message()));
  }

  public void handleHeroOfTheMonth(String channelId, String requestedBy, LocalDate date) {
    var heroes = kudosFacade.findAllGivenMonthKudosBy(channelId, date);
    var sortedHeroesByMessageSize = sortByHeroes(heroes);
    var blocks = new ArrayList<LayoutBlock>();
    blocks.add(HeaderBlock.builder().text(givenMonthHeroHeader(date)).build());
    buildAndPostHeroesLeaderboard(channelId, requestedBy, blocks, sortedHeroesByMessageSize);
  }

  public void handleHeroOfTheYear(String channelId, String requestedBy) {
    var date = LocalDate.now();
    var heroes = kudosFacade.findAllGivenYearKudosBy(channelId, date);
    var sortedHeroesByMessageSize = sortByHeroes(heroes);
    var blocks = new ArrayList<LayoutBlock>();
    var currentYearHeroHeader = PlainTextObject.builder()
        .text(date.getYear() + " heroes of the year \uD83C\uDFC6 \uD83C\uDFC6 \uD83C\uDFC6")
        .emoji(true)
        .build();
    blocks.add(HeaderBlock.builder().text(currentYearHeroHeader).build());
    buildAndPostHeroesLeaderboard(channelId, requestedBy, blocks, sortedHeroesByMessageSize);
  }

  private Map<String, List<KudosDto>> sortByHeroes(List<KudosDto> kudos) {
    var mapByHeroes = kudos.stream()
        .collect(groupingBy(KudosDto::username, toList()));

    return mapByHeroes.entrySet()
        .stream()
        .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
        .collect(toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
  }

  private String getUserTag(String userId) {
    return "*<@" + userId + ">*";
  }

  private List<LayoutBlock> requestedByMessage(String userId) {
    var blocks = new ArrayList<LayoutBlock>();
    var message = "Heroes of the month leaderboard requested by " + getUserTag(userId);
    var text = MarkdownTextObject.builder().text(message).build();
    var layout = SectionBlock.builder().text(text).build();
    blocks.add(layout);
    return blocks;
  }

  private List<LayoutBlock> buildBlocks(Map<String, List<KudosDto>> groupedHeroes) {
    var blocks = new ArrayList<LayoutBlock>();
    blocks.add(DividerBlock.builder().build());
    blocks.addAll(addCurrentMonthLeaderboard(groupedHeroes));
    blocks.add(DividerBlock.builder().build());
    blocks.addAll(addCurrentMonthMessages(groupedHeroes));
    return blocks;
  }

  private List<LayoutBlock> addCurrentMonthMessages(Map<String, List<KudosDto>> groupedHeroes) {
    var blocks = new ArrayList<LayoutBlock>();
    blocks.add(
        SectionBlock.builder().text(PlainTextObject.builder().text("Votes:").build()).build());
    for (String hero : groupedHeroes.keySet()) {
      var sectionValues = new ArrayList<TextObject>();
      var messages = groupedHeroes.get(hero).stream().map(KudosDto::message).collect(joining("\n"));
      var text = getUserTag(hero) + "\n " + messages;
      sectionValues.add(MarkdownTextObject.builder().text(text).build());
      blocks.add(SectionBlock.builder().fields(sectionValues).build());
      blocks.add(DividerBlock.builder().build());
    }
    return blocks;
  }

  private List<LayoutBlock> addCurrentMonthLeaderboard(Map<String, List<KudosDto>> groupedHeroes) {
    var blocks = new ArrayList<LayoutBlock>();
    blocks.add(
        SectionBlock.builder().text(PlainTextObject.builder().text("Leaderboard table:").build())
            .build());
    var sectionValues = new ArrayList<TextObject>();
    sectionValues.add(MarkdownTextObject.builder().text("*Hero*").build());
    sectionValues.add(MarkdownTextObject.builder().text("*Vote count*").build());
    for (String hero : groupedHeroes.keySet()) {
      if (sectionValues.size() >= MAX_FIELDS_COUNT) {
        blocks.add(SectionBlock.builder().fields(sectionValues).build());
        sectionValues = new ArrayList<>();
      }
      sectionValues.add(MarkdownTextObject.builder().text("<@" + hero + ">").build());
      sectionValues.add(
          PlainTextObject.builder().text(String.valueOf(groupedHeroes.get(hero).size())).build());
    }
    blocks.add(SectionBlock.builder().fields(sectionValues).build());
    return blocks;
  }

  private PlainTextObject givenMonthHeroHeader(LocalDate date) {
    var currentMonth = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    return PlainTextObject.builder()
        .text(currentMonth + " heroes of the month \uD83E\uDDB8")
        .emoji(true)
        .build();
  }
}
