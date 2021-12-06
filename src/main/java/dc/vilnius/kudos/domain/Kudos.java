package dc.vilnius.kudos.domain;

import io.micronaut.data.annotation.DateCreated;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
class Kudos {

  @Id
  @GeneratedValue
  private UUID id;

  @NotNull
  private String channel;

  @NotNull
  private String username;

  @NotNull
  private String message;

  @DateCreated
  private LocalDateTime createDate;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String comment) {
    this.message = comment;
  }

  public LocalDateTime getCreateDate() {
    return createDate;
  }

  public void setCreateDate(LocalDateTime createDate) {
    this.createDate = createDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Kudos kudos = (Kudos) o;
    return channel.equals(kudos.channel) && username.equals(kudos.username) && message.equals(
        kudos.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, username, message);
  }
}
