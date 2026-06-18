package com.project.bankingSystem;

import com.project.bankingSystem.models.User;
import com.project.bankingSystem.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestLockHelper {

    private final UserRepository userRepository;

    public TestLockHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void lockUserAndSleep(Long userId, long sleepMillis) {
        System.out.println(Thread.currentThread().getName() + " - Requesting PESSIMISTIC_WRITE lock...");

        // 1. Acquire the pessimistic lock
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println(Thread.currentThread().getName() + " - 🔒 Lock ACQUIRED. Going to sleep.");

        // 2. Hold the lock open by sleeping the thread
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println(Thread.currentThread().getName() + " - 🔓 Waking up. Releasing lock.");
    }
}