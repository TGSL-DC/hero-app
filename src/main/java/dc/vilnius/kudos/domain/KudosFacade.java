package dc.vilnius.kudos.domain;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import dc.vilnius.kudos.dto.GiveKudos;
import dc.vilnius.kudos.dto.KudosDto;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class KudosFacade {

  private final KudosRepository kudosRepository;

  @Inject
  public KudosFacade(KudosRepository kudosRepository) {
    this.kudosRepository = kudosRepository;
  }

  public List<KudosDto> submit(GiveKudos giveKudos) {
    var kudosList = new ArrayList<Kudos>();
    for (String username : giveKudos.usernames()) {
      var kudos = new Kudos();
      kudos.setUsername(username);
      kudos.setChannel(giveKudos.channel());
      kudos.setMessage(giveKudos.message());
      kudosList.add(kudos);
    }

    var savedKudos = kudosRepository.saveAll(kudosList);

    return StreamSupport.stream(savedKudos.spliterator(), true)
        .map(KudosMapper::entity2Dto)
        .collect(toList());
  }

  public List<KudosDto> findAllCurrentMonthKudosBy(String channelId) {
    var currentTime = LocalDate.now().atStartOfDay();
    var firstDayOfMonth = currentTime.with(TemporalAdjusters.firstDayOfMonth());
    var lastDayOfMonth = currentTime.with(TemporalAdjusters.lastDayOfMonth());
    return kudosRepository.findByChannelAndCreateDateBetweenOrderByUsername(channelId,
            firstDayOfMonth, lastDayOfMonth).stream().map(KudosMapper::entity2Dto).collect(toSet())
        .stream().collect(toList());
  }
}
