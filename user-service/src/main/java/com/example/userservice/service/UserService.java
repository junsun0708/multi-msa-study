package com.example.userservice.service;



import com.example.userservice.dto.UserDto;

public interface UserService {
//public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

//    UserDto getUserByUserId(String userId);
//
//    Iterable<UserEntity> getUserByAll();
//
//    UserDto getUserDetailsByEmail(String userName);
}