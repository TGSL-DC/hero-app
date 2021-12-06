package dc.vilnius.slack.dto;

import java.util.List;

public record SlackMessage(
    List<String> users,
    String message
) {

}
