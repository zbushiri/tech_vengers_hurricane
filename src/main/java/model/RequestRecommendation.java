package model;

/**
 * A request with an explanation of its match to the signed-in volunteer.
 * @param request open relief request, including visible existing assignments
 * @param recommended whether specialty and nearby driving distance both match
 * @param explanation readable reason or routing limitation
 * @param distanceMeters driving distance, or positive infinity when unavailable
 * @author Team Tech-vengers
 */
public record RequestRecommendation(ReliefRequest request, boolean recommended,
        String explanation, double distanceMeters) { }
