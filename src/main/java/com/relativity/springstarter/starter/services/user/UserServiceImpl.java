package com.relativity.springstarter.starter.services.user;

import com.relativity.springstarter.starter.dtos.user.UserDto;
import com.relativity.springstarter.starter.entities.user.User;
import com.relativity.springstarter.starter.repositories.user.UserRepository;
import com.relativity.springstarter.starter.services.AbstractGenericService;
import com.relativity.springstarter.starter.services.user.exception.UserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * {@link User} service.
 *
 * @author avakhobov
 */
@Service
public class UserServiceImpl extends AbstractGenericService<User, UserDto> implements UserService {

    /**
     * Logger for {@link UserServiceImpl}.
     */
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * Create a {@link UserServiceImpl}.
     *
     * @param userDao the user repository.
     *
     * @throws IllegalArgumentException if {@code roleDao} is {@code null}.
     */
    @Autowired
    public UserServiceImpl(
            final UserRepository userDao
    ) {
        super(userDao, userDao, new UserBridge(userDao));
    }

    @Override
    protected UserRepository getRepository() {
        return (UserRepository) super.getRepository();
    }

    @Override
    public UserBridge getBridge() {
        return (UserBridge) super.getBridge();
    }

    @Override
    protected boolean exists(User entity) {
        return getRepository().exists(entity.getId(), entity.getUsername(), entity.getEmail());
    }

    @Override
    protected UserNotFoundException createEntityNotFoundException(User entity) {
        return new UserNotFoundException("Following user not found:" + entity);
    }

    @Override
    protected UserNotFoundException createEntityNotFoundException(UUID entityId) {
        return new UserNotFoundException("No user for ID=" + entityId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllContainingUsername(final String username) {
        return getRepository().findAllContainingUsernameIgnoreCase(username);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllContainingEmail(final String email) {
        return getRepository().findAllContainingEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllContainingUsernameOrEmail(final String username, final String email) {
        return getRepository().findAllContainingUsernameOrEmailIgnoreCase(username, email);
    }

    @Transactional(readOnly = true)
    @Override
    public User findByUsername(String username) {
        return getRepository().findByUsernameIgnoreCase(username);
    }

    @Transactional(readOnly = true)
    @Override
    public User findByEmail(String email) {
        return getRepository().findByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    @Override
    public User findByUsernameOrEmail(String username, String email) {
        User user;
        try {
            user = getRepository().findByUsernameOrEmailIgnoreCase(username, email);
        } catch (UserNotFoundException e) {
            LOG.debug("No user found for specified username and email", e);
            user = null;
        }
        return user;
    }

    @Override
    public User setPassword(UUID userId, char[] password) {
        User user = this.getUserRepository().findById(userId).orElse(null);

        if (user == null) {
            throw new UserNotFoundException("User not found, id = " + userId);
        }
        user.setPassword(password);
        return this.getRepository().update(user);
    }

    @Override
    public User setPasswordByOwner(UUID userId, char[] password, User owner) {
        User user = this.getUserRepository().findByIdAndOwner(userId, owner);

        if (user == null) {
            throw new UserNotFoundException(String.format("User not found, id = %s, ownerId = %s", userId,
                    owner.getId()));
        }
        user.setPassword(password);
        return this.getRepository().update(user);
    }

    @Override
    public User setEnabled(final UUID userId, boolean enabled) {
        final User updatedEntity = getRepository().setEnabled(userId, enabled);

        if (updatedEntity == null) {
            throw this.createEntityNotFoundException(userId);
        }

        return updatedEntity;
    }

    @Override
    public User setEnabledByOwner(UUID userId, boolean enabled, User owner) {
        final User updatedEntity = getRepository().setEnabledByOwner(userId, enabled, owner);

        if (updatedEntity == null) {
            throw this.createEntityNotFoundException(userId);
        }

        return updatedEntity;
    }

    @Override
    public User setVerified(final UUID userId, boolean verified) {
        final User updatedEntity = getRepository().setVerified(userId, true);

        if (updatedEntity == null) {
            throw this.createEntityNotFoundException(userId);
        }

        return updatedEntity;
    }

    @Override
    public User setVerifiedByOwner(UUID userId, boolean verified, User owner) {
        final User updatedEntity = getRepository().setVerifiedByOwner(userId, true, owner);

        if (updatedEntity == null) {
            throw this.createEntityNotFoundException(userId);
        }

        return updatedEntity;
    }
}
