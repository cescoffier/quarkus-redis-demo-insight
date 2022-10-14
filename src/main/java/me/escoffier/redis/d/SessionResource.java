package me.escoffier.redis.d;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Map;

/**
 * Demo of the session storage.
 * <p>
 * <code><pre>
 *     > mvn quarkus:dev
 *     > http :8080/session/clement
 *     > http POST :8080/session/clement product=coffee quantity:=2
 *     > http POST :8080/session/clement product=beer quantity:=1
 *     > http POST :8080/session/max product=coffee quantity:=1
 *     > http :8080/session/clement
 *     > http :8080/session/max
 * </pre></code>
 */
@Path("/session")
public class SessionResource {

    @Inject
    ShoppingBasketService service;

    @GET
    @Path("/{name}")
    public Map<String, Integer> getShoppingBasket(String name) {
        return service.getBasket(name);
    }

    record Product(String product, int quantity) {}

    @POST
    @Path("/{name}")
    public void addToShoppingBasket(String name, Product product) {
        service.addToBasket(name, product.product, product.quantity);
    }


}
