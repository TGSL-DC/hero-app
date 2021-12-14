package dc.vilnius.slack.domain;

import dc.vilnius.slack.dto.SlackMessage;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CommandParser {

  private static final Pattern MESSAGE_PATTERN = Pattern.compile("(\\s+\\w.*)", Pattern.CASE_INSENSITIVE);
  private static final Pattern USER_PATTERN = Pattern.compile("(@[\\w|.]+)", Pattern.CASE_INSENSITIVE);

  private CommandParser() {}

  public static SlackMessage parse(String message) {
    var usersMatcher = USER_PATTERN.matcher(message);
    var users = new ArrayList<String>();
    while(usersMatcher.find()) {
      for(int i = 1; i <= usersMatcher.groupCount(); i++) {
        var raw = usersMatcher.group(i).split("\\|");
        var id = raw[0].replace("@", "");
        users.add(id);
      }
    }
    var messageMatcher = MESSAGE_PATTERN.matcher(message);
    var parsedMessage =  messageMatcher.find() ? messageMatcher.group(1).strip() : null;

    return new SlackMessage(users, parsedMessage);
  }
}
