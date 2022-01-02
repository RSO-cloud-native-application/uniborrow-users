package si.fri.rso.uniborrow.users.models.converters;

import si.fri.rso.uniborrow.users.lib.User;
import si.fri.rso.uniborrow.users.models.entities.UserEntity;

public class UserConverter {

    public static User toDto(UserEntity entity) {
        User dto = new User();
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setUserId(entity.getId());
        dto.setUsername(entity.getUsername());

        return dto;
    }

    public static UserEntity toEntity(User dto) {
        UserEntity entity = new UserEntity();
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setUsername(dto.getUsername());

        return entity;
    }
}
