package com.backEnd.genomebank.services.impl;

import com.backEnd.genomebank.dto.user.UserOutDTO;
import com.backEnd.genomebank.dto.user.UserUpdateDTO;
import com.backEnd.genomebank.entities.User;
import com.backEnd.genomebank.repositories.UserRepository;
import com.backEnd.genomebank.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    /**
     * Obtener todos los usuarios.
     * @return Lista de usuarios.
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> obtenerUsuarios() {
        return userRepository.findAll().stream().toList();
    }
    /**
     * Actualizar un usuario existente.
     * @param id ID del usuario a actualizar.
     * @param userUpdateDTO Datos de actualización del usuario.
     * @return Usuario actualizado si existe.
     */
    @Override
    @Transactional
    public Optional<UserOutDTO> actualizarUsuario(Long id, UserUpdateDTO userUpdateDTO) {
        return userRepository.findById(id).map(user -> {
            if (userUpdateDTO.getUsername() != null) {
                user.setUsername(userUpdateDTO.getUsername());
            }
            if (userUpdateDTO.getEmail() != null) {
                user.setEmail(userUpdateDTO.getEmail());
            }
            if (userUpdateDTO.getPassword() != null) {
                user.setPassword(userUpdateDTO.getPassword());
            }
            User updatedUser = userRepository.save(user);
            return convertToOutDTO(updatedUser);
        });
    }
    /**
     * Eliminar un usuario por ID.
     * @param id ID del usuario a eliminar.
     * @return true si el usuario fue eliminado, false si no existe.
     */
    @Override
    @Transactional
    public boolean eliminarUsuario(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    /**
     * Convertir una entidad User a UserOutDTO.
     * @param user Entidad User.
     * @return DTO de salida UserOutDTO.
     */
    private UserOutDTO convertToOutDTO(User user) {
        UserOutDTO userOutDTO = new UserOutDTO();
        userOutDTO.setId(user.getId());
        userOutDTO.setUsername(user.getUsername());
        userOutDTO.setEmail(user.getEmail());
        userOutDTO.setPassword(user.getPassword());
        return userOutDTO;
    }
}
