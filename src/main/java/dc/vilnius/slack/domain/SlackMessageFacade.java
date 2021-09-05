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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class SlackMessageFacade {

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
        var lastFridayAtTenOClock = LocalDate.now().with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY))
                .atTime(10, 0);
        int postAt = (int) lastFridayAtTenOClock.toEpochSecond(ZoneOffset.UTC);
        try {
            var scheduledMessage = ChatScheduleMessageRequest.builder()
                    .channel(user)
                    .text(message)
                    .postAt(postAt)
                    .token(appConfig.getSingleTeamBotToken())
                    .build();
            var response = slackApp.client().chatScheduleMessage(scheduledMessage);
            if (response.isOk()) {
                logger.info("Scheduled a private message {} for user {}", response.getScheduledMessageId(), user);
            } else {
                logger.error("Failed to schedule a message for user {}, reason: {}", user, response.getError());
            }
        } catch (IOException | SlackApiException e) {
            logger.error("Failed to schedule message for user: {}", user, e);
        }
    }

    public SlackMessage parseMessage(String commandArgText) {
        return CommandParser.parse(commandArgText);
    }

    public void handleHeroVote(GiveKudos giveKudos) {
        kudosFacade.submit(giveKudos);

        giveKudos.usernames().forEach(user -> scheduleMessageAtTheEndOfTheMonth(user, giveKudos.message()));
    }

    public void handleHeroOfTheMonth(String channelId) {
        var sortedHeroesByMessageSize = sortedCurrentMonthHeroesByMessageSize(channelId);
        var blocks = buildBlocks(sortedHeroesByMessageSize);
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
                logger.error("Failed to post a message in the channel {}, reason: {}", channelId, response.getError());
            }
        } catch (IOException | SlackApiException e) {
            logger.error("Failed to post hero of the month", e);
        }
    }

    private Map<String, List<KudosDto>> sortedCurrentMonthHeroesByMessageSize(String channelId) {
        var mapByHeroes = kudosFacade.findAllCurrentMonthKudosBy(channelId)
                .stream()
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

    private List<LayoutBlock> buildBlocks(Map<String, List<KudosDto>> groupedHeroes) {
        var blocks = new ArrayList<LayoutBlock>();
        blocks.add(HeaderBlock.builder().text(currentMonthHeroHeader()).build());
        blocks.add(DividerBlock.builder().build());
        blocks.addAll(addCurrentMonthLeaderboard(groupedHeroes));
        blocks.add(DividerBlock.builder().build());
        return blocks;
    }

    private List<LayoutBlock> addCurrentMonthLeaderboard(Map<String, List<KudosDto>> groupedHeroes) {
        var blocks = new ArrayList<LayoutBlock>();
        var heroes = groupedHeroes.keySet();
        var maxFieldsCount = 10;
        var sectionValues = new ArrayList<TextObject>();
        sectionValues.add(MarkdownTextObject.builder().text("*Hero*").build());
        sectionValues.add(MarkdownTextObject.builder().text("*Vote count*").build());
        for (String hero : heroes) {
            if (sectionValues.size() >= maxFieldsCount) {
                blocks.add(SectionBlock.builder().fields(sectionValues).build());
                sectionValues = new ArrayList<>();
            }
            sectionValues.add(MarkdownTextObject.builder().text("<@" + hero + ">").build());
            sectionValues.add(PlainTextObject.builder().text(String.valueOf(groupedHeroes.get(hero).size())).build());
        }
        blocks.add(SectionBlock.builder().fields(sectionValues).build());
        return blocks;
    }

    private PlainTextObject currentMonthHeroHeader() {
        var currentMonth = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return PlainTextObject.builder()
                .text(currentMonth + " heroes of the month \uD83E\uDDB8")
                .emoji(true)
                .build();
    }
}
