package me.escoffier.redis.supes;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class FightSimulator {

    private final Random random = new Random();

    public Supes fight(Supes s1, Supes s2) {
        var l1 = s1.level() + random.nextInt(50);
        var l2 = s2.level() + random.nextInt(50);
        var time = random.nextInt(5000);

        nap(time);
        if (l1 >= l2) {
            return s1;
        }
        return s2;

    }

    private static void nap(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
