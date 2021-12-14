package dc.vilnius.tasks;

import java.util.List;

public record SubmitKudosEvent(String channelId, List<String> usernames, String message) {}
