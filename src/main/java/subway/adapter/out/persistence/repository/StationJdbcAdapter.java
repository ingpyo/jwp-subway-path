package subway.adapter.out.persistence.repository;

import org.springframework.stereotype.Repository;
import subway.adapter.out.persistence.dao.StationDao;
import subway.adapter.out.persistence.entity.StationEntity;
import subway.application.port.out.station.StationCommandPort;
import subway.application.port.out.station.StationQueryPort;
import subway.domain.Station;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class StationJdbcAdapter implements StationCommandPort, StationQueryPort {

    private final StationDao stationDao;

    public StationJdbcAdapter(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public Long createStation(final Station station) {
        final StationEntity stationEntity = new StationEntity(station.getName());

        return stationDao.createStation(stationEntity);
    }

    @Override
    public Optional<Station> findByName(final Station station) {
        final Set<StationEntity> stations = new HashSet<>(stationDao.findAll());

        return stations.stream()
                .filter(stationEntity -> stationEntity.getName().equals(station.getName()))
                .map(stationEntity -> new Station(stationEntity.getName()))
                .findFirst();
    }
}
