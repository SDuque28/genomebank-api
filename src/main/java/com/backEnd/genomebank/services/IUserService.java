package com.backEnd.genomebank.services;

import com.backEnd.genomebank.dto.user.UserOutDTO;
import com.backEnd.genomebank.dto.user.UserUpdateDTO;
import com.backEnd.genomebank.entities.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> obtenerUsuarios();
    Optional<UserOutDTO> actualizarUsuario(Long id, UserUpdateDTO userUpdateDTO);
    boolean eliminarUsuario(Long id);
}
