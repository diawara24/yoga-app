package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.dto.response.MessageResponse;

public interface UserService {
    MessageResponse delete(Long id);

    User findById(Long id);

    UserDto findDtoById(Long id);
}
