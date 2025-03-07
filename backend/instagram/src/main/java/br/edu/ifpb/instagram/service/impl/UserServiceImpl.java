package br.edu.ifpb.instagram.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.repository.UserRepository;
import br.edu.ifpb.instagram.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(userDto.username());
        userEntity.setEmail(userDto.email());
        userEntity.setFullName(userDto.fullName());
        userEntity.setEncryptedPassword(passwordEncoder.encode(userDto.password()));

        UserEntity storedUserEntity = userRepository.save(userEntity);

        return new UserDto(
                storedUserEntity.getId(),
                storedUserEntity.getFullName(),
                storedUserEntity.getUsername(),
                storedUserEntity.getEmail(),
                null,
                null);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        UserDto userDtoToSave;

        if (userDto.password() != null && !userDto.password().isEmpty()) {
            userDtoToSave = new UserDto(
                    userDto.id(),
                    userDto.fullName(),
                    userDto.username(),
                    userDto.email(),
                    null,
                    passwordEncoder.encode(userDto.password()));
        } else {
            userDtoToSave = new UserDto(
                    userDto.id(),
                    userDto.fullName(),
                    userDto.username(),
                    userDto.email(),
                    null,
                    null);
        }

        Integer linhasModificadas = userRepository.updatePartialUser(
                userDtoToSave.fullName(),
                userDtoToSave.email(),
                userDtoToSave.username(),
                userDtoToSave.encryptedPassword(),
                userDtoToSave.id());

        if (linhasModificadas == 0) {
            throw new RuntimeException("User not found");
        }

        return userDtoToSave;
    }

    @Override
    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }

    @Override
    public UserDto findById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDto(
                userEntity.getId(),
                userEntity.getFullName(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                null,
                userEntity.getEncryptedPassword());
    }

    @Override
    public List<UserDto> findAll() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        if (userEntities.isEmpty()) {
            throw new RuntimeException("Users not found");
        }
        for (UserEntity userEntity : userEntities) {
            UserDto userDto = new UserDto(
                    userEntity.getId(),
                    userEntity.getFullName(),
                    userEntity.getUsername(),
                    userEntity.getEmail(),
                    null,
                    userEntity.getEncryptedPassword());

            userDtos.add(userDto);
        }

        return userDtos;
    }

}
