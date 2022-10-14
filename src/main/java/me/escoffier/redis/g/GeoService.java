package me.escoffier.redis.g;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.geo.GeoCommands;
import io.quarkus.redis.datasource.geo.GeoPosition;
import io.quarkus.redis.datasource.geo.GeoSearchArgs;
import io.quarkus.redis.datasource.geo.GeoUnit;
import io.quarkus.redis.datasource.list.ListCommands;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GeoService {
    private final GeoCommands<String, String> geo;
    private final ListCommands<String, String> list;

    public GeoService(RedisDataSource dataSource) {
        geo = dataSource.geo(String.class);
        list = dataSource.list(String.class);
    }

    public List<String> getMembers() {
        return list.lrange(MemberImporter.MEMBERS_LIST_KEY, 0, -1);
    }

    public record TeamMember(String name, double latitude, double longitude) {

    }

    public List<TeamMember> getMemberPositions() {
        List<TeamMember> result = new ArrayList<>();
        for (String member : getMembers()) {
            List<GeoPosition> positions = geo.geopos(MemberImporter.MEMBERS_POS_KEY, member);
            if (! positions.isEmpty()) {
                result.add(new TeamMember(member, positions.get(0).latitude(), positions.get(0).longitude()));
            }
        }
        return result;
    }

    public record ClosestTeamMember(String name, double distance) {}

    public List<ClosestTeamMember> getClosestTeamMember(String member) {
        GeoSearchArgs<String> args = new GeoSearchArgs().fromMember(member).count(6).ascending().byRadius(5000, GeoUnit.KM).withDistance();
        return geo.geosearch(MemberImporter.MEMBERS_POS_KEY, args)
                .stream()
                .skip(1) // Skip the first member
                .map(gv -> new ClosestTeamMember(gv.member(), gv.distance().orElse(-1)))
                .collect(Collectors.toList());
    }
}
