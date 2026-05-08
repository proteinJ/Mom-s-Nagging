package com.mom.nagging.infra.external;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class NaverMapClient {

    private final RestClient naverRestClient;

    @Value("${external.naver.directions-url}")
    private String directionsUrl;

    public Long getDurationInSeconds(Point start, Point goal) {
        // 좌표를 "경도,위도" 문자열 포맷으로 변환
        String startPos = start.getX() + "," + start.getY();
        String goalPos = goal.getX() + "," + goal.getY();

        // API 호출
        NaverDirectionsResponse response = naverRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(directionsUrl)
                        .queryParam("start", startPos)
                        .queryParam("goal", goalPos)
                        .queryParam("option", "trafast") // 실시간 빠른길 옵션
                        .build())
                .retrieve()
                .body(NaverDirectionsResponse.class);

        // 결과에서 소요 시간(ms) 추출 후 초 단위로 반환
        return response.getFirstRouteDuration() / 1000;
    }
}