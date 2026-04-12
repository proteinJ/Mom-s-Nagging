package com.mom.nagging.infra.external;

public record NaverDirectionsResponse(Route route) {
    public record Route(java.util.List<Traoptimal> trafast) {}
    public record Traoptimal(Summary summary) {}
    public record Summary(Long duration) {}

    public Long getFirstRouteDuration() {
        if (route.trafast != null && !route.trafast.isEmpty()) {
            return route.trafast.get(0).summary().duration();
        }
        return 0L;
    }
}
