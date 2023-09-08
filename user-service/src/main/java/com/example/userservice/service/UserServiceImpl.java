package com.example.userservice.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import com.example.userservice.dto.UserDto;
//import com.example.userservice.entity.UserEntity;
//import com.example.userservice.repository.UserRepository;
//import com.example.userservice.vo.ResponseOrder;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    //private final BCryptPasswordEncoder passwordEncoder;
    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);//딱 맞아야 됨
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd("sdsfdfsdfd");//구현이 안되었으므로 임의로 넣음
        //userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        userRepository.save(userEntity);

        return mapper.map(userEntity, UserDto.class);
    }

//    @Override
//    public UserDto getUserByUserId(String userId) {
//        UserEntity userEntity = userRepository.findByUserId(userId);
//        if (userEntity == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//
//        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
//        List<ResponseOrder> orders = new ArrayList<>();
//        userDto.setOrders(orders);
//        return userDto;
//    }
//
//    @Override
//    public Iterable<UserEntity> getUserByAll() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity userEntity = userRepository.findByEmail(username);
//
//        if (userEntity == null) {
//            throw new UsernameNotFoundException(username);
//        }
//
//        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), true, true,
//                true, true, new ArrayList<>());
//    }
//
//    @Override
//    public UserDto getUserDetailsByEmail(String email) {
//        UserEntity userEntity = userRepository.findByEmail(email);
//
//        if (userEntity == null) {
//            throw new UsernameNotFoundException(email);
//        }
//        return new ModelMapper().map(userEntity, UserDto.class);
//    }
}