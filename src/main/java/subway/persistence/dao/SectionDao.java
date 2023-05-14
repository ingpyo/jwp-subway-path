package subway.persistence.dao;

import subway.persistence.entity.SectionEntity;

import java.util.List;

public interface SectionDao {
    void saveSection(final Long lineId, final List<SectionEntity> sectionEntities);

    List<SectionEntity> findAllByLineId(final Long lineId);
}
