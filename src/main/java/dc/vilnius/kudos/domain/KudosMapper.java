package dc.vilnius.kudos.domain;

import dc.vilnius.kudos.dto.KudosDto;

class KudosMapper {
  private KudosMapper() {}

  static KudosDto entity2Dto(Kudos kudos) {
    return new KudosDto(kudos.getChannel(), kudos.getUsername(), kudos.getMessage(),
        kudos.getCreateDate());
  }

}
