package com.github.kolegran;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentMapAccess {

    private static final int CYCLES = 10;
    private static final int THREADS_COUNT = 50;
    private static final Set<String> USERS = makeUsers();
    private static final Map<String, Double> MAP = initMap();

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService service = Executors.newFixedThreadPool(THREADS_COUNT);

        for (int i = 0; i < THREADS_COUNT; i += 1) {
            service.submit(() -> {
                final Random random = new Random();
                for (int j = 0; j < CYCLES; j += 1) {
                    final boolean isDeposit = random.nextBoolean();
                    if (isDeposit) {
                        deposit("user" + (random.nextInt(1) + 1), (double) random.nextInt(70));
                    } else {
                        withdraw("user" + (random.nextInt(1) + 1), (double) random.nextInt(70));
                    }
                }
            });
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Result map:");
        for (Map.Entry<String, Double> entry : MAP.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    private static void deposit(String user, Double amount) {
        System.out.printf("Deposit for %s amount of %f\n", user, amount);
        MAP.computeIfPresent(user, (u, currentAmount) -> currentAmount + amount);
    }

    private static void withdraw(String user, Double amount) {
        System.out.printf("Withdraw for %s amount of %f\n", user, amount);
        MAP.computeIfPresent(user, (u, currentAmount) -> {
            if (currentAmount > amount)
                return currentAmount - amount;
            return currentAmount;
        });
    }

    private static Set<String> makeUsers() {
        final Set<String> users = new HashSet<>();
        users.add("user1");
        users.add("user2");
        users.add("user3");
        return users;
    }

    private static Map<String, Double> initMap() {
        final Map<String, Double> map = new HashMap<>();
        USERS.forEach(u -> map.put(u, 100d));
        return map;
    }

}
