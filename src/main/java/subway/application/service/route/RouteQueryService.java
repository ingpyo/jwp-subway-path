package subway.application.service.route;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.adapter.in.web.route.dto.FindShortCutRequest;
import subway.adapter.out.graph.dto.RouteDto;
import subway.application.dto.RouteResponse;
import subway.application.port.in.route.FindRouteResultUseCase;
import subway.application.port.out.graph.ShortPathPort;
import subway.application.port.out.line.LineQueryPort;
import subway.application.port.out.section.SectionQueryPort;
import subway.application.port.out.station.StationQueryPort;
import subway.domain.*;
import subway.domain.discountpolicy.SubwayFarePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RouteQueryService implements FindRouteResultUseCase {

    private static final int FROM_STATION_INDEX = 0;
    private static final int TO_STATION_INDEX = 1;

    private final ShortPathPort shortPathPort;
    private final SectionQueryPort sectionQueryPort;
    private final StationQueryPort stationQueryPort;
    private final LineQueryPort lineQueryPort;

    public RouteQueryService(final ShortPathPort shortPathPort, final SectionQueryPort sectionQueryPort, final StationQueryPort stationQueryPort, final LineQueryPort lineQueryPort) {
        this.shortPathPort = shortPathPort;
        this.sectionQueryPort = sectionQueryPort;
        this.stationQueryPort = stationQueryPort;
        this.lineQueryPort = lineQueryPort;
    }

    @Override
    public RouteResponse findRouteResult(final FindShortCutRequest findShortCutRequest) {
        final List<Station> routeRequest = createBy(findShortCutRequest.getFromStation(), findShortCutRequest.getToStation());
        final Map<Long, Sections> sectionsByLine = groupingByLine();

        if (sectionsByLine.isEmpty()) {
            throw new IllegalArgumentException("노선에 구간을 추가해주세요");
        }

        SubwayFarePolicy subwayFarePolicy = new SubwayFarePolicy();

        RouteDto routeDto = shortPathPort.findSortPath(
                routeRequest.get(FROM_STATION_INDEX),
                routeRequest.get(TO_STATION_INDEX),
                sectionsByLine);

        final Fare fare = subwayFarePolicy.calculateFare(
                lineQueryPort.findLinesById(routeDto.getLineIds()),
                routeDto.getDistance(),
                findShortCutRequest.getAge()
        );

        return RouteResponse.of(new Route(routeDto.getStations(), routeDto.getDistance()), fare);
    }

    private Map<Long, Sections> groupingByLine() {
        return sectionQueryPort.findAll().stream()
                .collect(Collectors.groupingBy(Section::getLineId, Collectors.collectingAndThen(
                        Collectors.toList(),
                        Sections::new
                )));
    }

    private List<Station> createBy(final String fromStation, final String toStation) {
        final List<Station> route = new ArrayList<>();

        route.add(stationQueryPort.findByName(new Station(fromStation))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다.")));
        route.add(stationQueryPort.findByName(new Station(toStation))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다.")));

        return route;
    }
}