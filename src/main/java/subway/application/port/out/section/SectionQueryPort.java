package subway.application.port.out.section;

import subway.domain.Section;

import java.util.List;

public interface SectionQueryPort {
    List<Section> findAllByLineId(Long lineId);

    List<Section> findAll();
}
