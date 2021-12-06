package dc.vilnius.kudos.dto;

import java.util.List;

public record GiveKudos (String channel, List<String> usernames, String message) {

}
