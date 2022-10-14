package me.escoffier.redis.g;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.geo.GeoItem;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class MemberImporter {

    public static final String MEMBERS_POS_KEY = "quarkus-members-pos";
    public static final String MEMBERS_LIST_KEY = "quarkus-members";

    public void importMembers(@Observes StartupEvent ev, RedisDataSource ds) {
        ds.withTransaction(tx -> {
            var geo = tx.geo(String.class);
            var list = tx.list(String.class);
            tx.key().del(MEMBERS_LIST_KEY, MEMBERS_POS_KEY);
            list.lpush(MEMBERS_LIST_KEY, "Max", "Sanne", "Andy", "Guillaume", "Julien", "Clement", "Dimitri", "Georgios", "Ozan", "Stephane", "Auri");
            geo.geoadd(MEMBERS_POS_KEY,
                    GeoItem.of("Max", 10.493547, 55.281464),
                    GeoItem.of("Sanne", -0.074861, 51.117889),
                    GeoItem.of("Andy", 5.7096, 43.1826),
                    GeoItem.of("Guillaume", 4.8357, 45.7640),
                    GeoItem.of("Julien", 4.7769, 45.7613),
                    GeoItem.of("Clement", 4.8924, 44.9334),
                    GeoItem.of("Dimitri", 6.9293, 46.9900),
                    GeoItem.of("Georgios", 23.7440795, 37.9374893),
                    GeoItem.of("Ozan", -0.118092, 51.509865),
                    GeoItem.of("Auri", -3.703790, 40.416775),
                    GeoItem.of("Stephane", 7.4035, 43.8045)
            );
        });
    }


}
