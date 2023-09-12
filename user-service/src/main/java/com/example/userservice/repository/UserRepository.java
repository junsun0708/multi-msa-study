package com.example.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.userservice.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findByUserId(String userId);
	UserEntity findByEmail(String username);
}