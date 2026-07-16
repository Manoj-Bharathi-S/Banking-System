package com.project.bankingSystem;

import com.project.bankingSystem.models.User;
import com.project.bankingSystem.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest
public class PessimisticLockIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestLockHelper testLockHelper;

    @Test
    public void testPessimisticLockConcurrency() throws InterruptedException, ExecutionException {
        // 1. Create and save a dummy user to test against

        String uniqueUsername = "test_user_" + UUID.randomUUID().toString();
        User testUser = new User();
        testUser.setUsername(uniqueUsername);
        testUser.setBalance(1000.00);
        testUser.setPassword("dummyPassword123"); // 💡 Add this line!
        testUser = userRepository.save(testUser);

        Long userId = testUser.getId();

        // 2. Thread 1: Acquires lock and holds it open for 3000ms (3 seconds)
        CompletableFuture<Void> thread1 = CompletableFuture.runAsync(() -> {
            testLockHelper.lockUserAndSleep(userId, 3000);
        });

        // Small delay to guarantee Thread 1 starts execution and grabs the lock first
        Thread.sleep(500);

        // 3. Thread 2: Tries to grab the exact same user row immediately while Thread 1 is sleeping
        CompletableFuture<Void> thread2 = CompletableFuture.runAsync(() -> {
            System.out.println("Thread-2 - Requesting PESSIMISTIC_WRITE lock...");

            long startTime = System.currentTimeMillis();

            // This call should block! It will wait here until Thread 1 finishes.
            testLockHelper.lockUserAndSleep(userId, 0);

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Thread-2 - Successfully completed after waiting " + duration + " ms.");
        });

        // Wait for both concurrent threads to wrap up execution
        CompletableFuture.allOf(thread1, thread2).get();
    }
}