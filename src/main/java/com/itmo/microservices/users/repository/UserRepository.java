package com.itmo.microservices.users.repository;

import com.itmo.microservices.users.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, String> {
}
