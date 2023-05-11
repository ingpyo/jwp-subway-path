package subway.domain.repository;

import subway.domain.Line;

import java.util.List;

public interface LineRepository {
    void createLine(Line line);

    void deleteById(Long lineIdRequest);

    List<Line> findAll();

    Line findById(Long lineIdRequest);
}
