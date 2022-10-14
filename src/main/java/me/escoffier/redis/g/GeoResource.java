package me.escoffier.redis.g;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/members")
public class GeoResource {

    @Inject
    GeoService service;

    @GET
    public List<String> all() {
        return service.getMembers();
    }

    @GET
    @Path("/positions")
    public List<GeoService.TeamMember> members() {
        return service.getMemberPositions();
    }

    @GET
    @Path("/{name}")
    public List<GeoService.ClosestTeamMember> getClosest(String name) {
        return service.getClosestTeamMember(name);
    }
}
