package me.escoffier.redis.d;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class ShoppingBasketService {

    @Inject
    SessionService sessions;

    public void addToBasket(String session, String product, int quantity) {
        sessions.set(session, product, quantity);
    }

    public void removeFromBasket(String session, String product) {
        sessions.remove(session, product);
    }

    public Map<String, Integer> getBasket(String session) {
        return sessions.getSession(session);
    }


}
