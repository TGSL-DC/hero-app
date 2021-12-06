package dc.vilnius.kudos.domain;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
interface KudosRepository extends CrudRepository<Kudos, UUID> {

  List<Kudos> findByChannelAndCreateDateBetweenOrderByUsername(String channel, LocalDateTime firstDayOfMonth, LocalDateTime lastDayOfMonth);
}
