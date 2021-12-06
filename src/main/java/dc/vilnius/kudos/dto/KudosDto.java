package dc.vilnius.kudos.dto;

import java.time.LocalDateTime;

public record KudosDto(String channel, String username, String message, LocalDateTime created) {

}
