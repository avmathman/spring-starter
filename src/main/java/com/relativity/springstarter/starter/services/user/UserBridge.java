package com.relativity.springstarter.starter.services.user;

import com.relativity.springstarter.starter.dtos.user.UserDto;
import com.relativity.springstarter.starter.entities.user.User;
import com.relativity.springstarter.starter.repositories.user.UserRepository;
import com.relativity.springstarter.starter.services.AbstractGenericBridge;

/**
 * Bridge to convert a {@link UserDto} to a {@link User} and vice versa.
 *
 * @author avakhobov
 */
public class UserBridge extends AbstractGenericBridge<User, UserDto> {

    /**
     * Create a {@link UserBridge}.
     *
     * <p>
     * <strong>Use with caution:</strong> this constructor will not set the @{code userRepository},
     * preventing any search in the Persistence Storage for the relations objects. This might be
     * dangerous when converting {@link #toEntity(UserDto)} as no consistency check will be done but
     * it will definitely improve performance.
     * </p>
     *
     */
    public UserBridge() {
        super();
    }

    /**
     * Create a {@link UserBridge}.
     *
     * @param userRepository repository to lookup users.
     */
    public UserBridge(final UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    protected User buildEntity() {
        return new User();
    }

    @Override
    protected UserDto buildDto() {
        return new UserDto();
    }

    @Override
    public User toEntity(final UserDto dto) {
        final User entity = super.toEntity(dto);

        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setEnabled(dto.isEnabled());
        entity.setVerified(dto.isVerified());

        return entity;
    }

    @Override
    public UserDto toDto(final User entity) {
        final UserDto dto = super.toDto(entity);

        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setPassword(null);
        dto.setEnabled(entity.isEnabled());
        dto.setVerified(entity.isVerified());

        return dto;
    }
}
