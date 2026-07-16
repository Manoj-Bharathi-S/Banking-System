package com.project.bankingSystem.repositories;

import com.project.bankingSystem.models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public interface UserRepository extends JpaRepository<User,Long>{
        Optional<User> findByUsername(String username);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT u FROM User u WHERE u.id = :id")
        Optional<User> findByIdForUpdate(@Param("id") Long id);



}

