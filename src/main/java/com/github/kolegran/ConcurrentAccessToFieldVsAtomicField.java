package com.github.kolegran;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentAccessToFieldVsAtomicField {

    private static final int CYCLES = 300;
    private static final int THREADS_COUNT = 50;
    private static Integer amount = 100;
    private static AtomicInteger atomicAmount = new AtomicInteger(amount);

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService service = Executors.newFixedThreadPool(THREADS_COUNT);

        for (int i = 0; i < THREADS_COUNT; i += 1) {
            service.submit(() -> {
                final Random random = new Random();
                for (int j = 0; j < CYCLES; j += 1) {
                    final boolean isDeposit = random.nextBoolean();
                    int newAmount = random.nextInt(50);
                    if (isDeposit) {
                        System.out.println("Deposit " + newAmount + ". Thread - " + Thread.currentThread().getName());
                        ConcurrentAccessToFieldVsAtomicField.amount += newAmount;
                        atomicAmount.addAndGet(newAmount);
                    } else {
                        System.out.println("Withdraw " + newAmount + ". Thread - " + Thread.currentThread().getName());
                        if (amount > newAmount)
                            ConcurrentAccessToFieldVsAtomicField.amount -= newAmount;

                        atomicAmount.getAndAccumulate(newAmount, (current, newValue) -> {
                            if (current > newValue)
                                return current - newValue;
                            return current;
                        });
                    }
                }
            });
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Resulting amount: " + amount);
        System.out.println("Resulting atomic amount: " + atomicAmount.get());
    }

}
