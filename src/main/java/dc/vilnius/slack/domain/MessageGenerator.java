package dc.vilnius.slack.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MessageGenerator {

    private static final List<String> SUCCESS_MESSAGES = Arrays.asList(
            "Thanks for voting 🥰 <@%s> will be really happy 🥳🥳",
            "Oh Yeah! 🥳 <@%s> is Rockstar this month 🤩",
            "<@%s> will be really happy 🥰",
            "<@%s> will be really happy 🥳🥳",
            "<@%s> will be really happy 😊",
            "<@%s> will be really happy 🤘"
    );

    public static String randomSuccessMessage(String username) {
        var random = new Random();
        var messageIndex = random.ints(0, SUCCESS_MESSAGES.size() - 1).findFirst().orElseThrow();
        var messageTemplate = SUCCESS_MESSAGES.get(messageIndex);
        return String.format(messageTemplate, username);
    }
}
