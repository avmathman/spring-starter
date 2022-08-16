package com.relativity.springstarter.starter.repositories.user;

import com.relativity.springstarter.starter.repositories.GenericRepository;
import com.relativity.springstarter.starter.entities.user.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

/**
 * The {@link User} Data Access Object (DAO) interface.
 *
 * @author avakhobov
 */
@Repository
public interface UserRepository extends GenericRepository<User> {

    /**
     * Find all {@link User} entities from the repository containing the username while ignoring case.
     *
     * @param username the username content to search.
     *
     * @return The list of all the {@link User} entities matching the search through the repository.
     */
    @Transactional(readOnly = true)
    @Query("FROM User AS u WHERE LOWER(u.username) LIKE concat('%', LOWER(:username), '%')")
    List<User> findAllContainingUsernameIgnoreCase(@Param("username") final String username);

    /**
     * Find all {@link User} entities from the repository containing the email while ignoring case.
     *
     * @param email the email content to search.
     *
     * @return The list of all the {@link User} entities matching the search through the repository.
     */
    @Transactional(readOnly = true)
    @Query("FROM User AS u WHERE LOWER(u.email) LIKE concat('%', LOWER(:email), '%')")
    List<User> findAllContainingEmailIgnoreCase(@Param("email") final String email);

    /**
     * Find all {@link User} entities from the repository containing the username or email while ignoring case.
     *
     * @param username The username content to search.
     * @param email The email content to search.
     *
     * @return The list of all the {@link User} entities matching the search through the repository.
     */
    @Transactional(readOnly = true)
    @Query("FROM User AS u WHERE LOWER(u.username) LIKE concat('%', LOWER(:username), '%') "
            + "OR LOWER(u.email) LIKE concat('%', LOWER(:email), '%')")
    List<User> findAllContainingUsernameOrEmailIgnoreCase(@Param("username") final String username,
            @Param("email") final String email);

    /**
     * Find a {@link User} through its username while ignoring case.
     *
     * @param username the username to search.
     *
     * @return The {@link User} matching the username.
     *
     * @throws NoResultException if no user matches the username in the repository.
     * @throws NonUniqueResultException if several users match the username in the repository.
     */
    @Transactional(readOnly = true)
    User findByUsernameIgnoreCase(final String username);

    /**
     * Find a {@link User} through its email while ignoring case.
     *
     * @param email The email to search.
     *
     * @return The {@link User} matching the email.
     *
     * @throws NoResultException if no user matches the email in the repository.
     * @throws NonUniqueResultException if several users match the email in the repository.
     */
    @Transactional(readOnly = true)
    User findByEmailIgnoreCase(final String email);

    /**
     * Find a user account through its username or email while ignoring case.
     *
     * @param username The username to search.
     * @param email The email to search.
     *
     * @return The {@link User} matching the username or email.
     *
     * @throws NoResultException if no user matches the username or email in the repository.
     * @throws NonUniqueResultException if several users match the username or email in the repository.
     */
    @Transactional(readOnly = true)
    User findByUsernameOrEmailIgnoreCase(final String username, final String email);

    /**
     * Set the active status of a user account.
     *
     * @param userId The user account identifier.
     * @param enabled The enabled status to set.
     *
     * @return The updated {@link User}, {@code null} if user account was not found in persistence layer.
     */
    default User setEnabled(final UUID userId, final boolean enabled) {
        User updateEntity = this.findById(userId).orElse(null);

        if (updateEntity != null) {
            updateEntity.setEnabled(enabled);

            updateEntity = this.save(updateEntity);
        }

        return updateEntity;
    }

    /**
     * Set the active status of a user account.
     *
     * <p>
     * Secure method to ensure you only update if you own the data by providing the authenticated user
     * as owner. {@link #setEnabled(UUID, boolean)} should be used instead if authenticated user has
     * administration permissions.
     * </p>
     *
     * @param userId The user account identifier.
     * @param enabled The enabled status to set.
     * @param owner The entity owner.
     *
     * @return the updated {@link User}, {@code null} if user account was not found in persistence layer.
     */
    default User setEnabledByOwner(final UUID userId, final boolean enabled, final User owner) {
        User updateEntity = this.findByIdAndOwner(userId, owner);

        if (updateEntity != null) {
            updateEntity.setEnabled(enabled);

            updateEntity = this.save(updateEntity);
        }

        return updateEntity;
    }

    /**
     * Set the verified status of a user account.
     *
     * @param userId The identifier of {@link User}.
     * @param verified The verified status to set.
     *
     * @return The updated {@link User}, {@code null} if user account was not found in persistence layer.
     */
    default User setVerified(final UUID userId, final boolean verified) {
        User updateEntity = this.findById(userId).orElse(null);

        if (updateEntity != null) {
            updateEntity.setVerified(verified);

            updateEntity = this.save(updateEntity);
        }

        return updateEntity;
    }

    /**
     * Set the verified status of a user account.
     *
     * <p>
     * Secure method to ensure you only update if you own the data by providing the authenticated user
     * as owner. {@link #setVerified(UUID, boolean)} should be used instead if authenticated user has
     * administration permissions.
     * </p>
     *
     * @param userId The identifier of {@link User}.
     * @param verified the verified status to set.
     * @param owner The {@link User} entity as owner.
     *
     * @return The updated {@link User}, {@code null} if user account was not found in persistence layer.
     */
    default User setVerifiedByOwner(final UUID userId, final boolean verified, final User owner) {
        User updateEntity = this.findByIdAndOwner(userId, owner);

        if (updateEntity != null) {
            updateEntity.setVerified(verified);

            updateEntity = this.save(updateEntity);
        }

        return updateEntity;
    }

    /**
     * Tests if an entity exists in the repository for the given primary key or the username or the
     * email.
     *
     * @param userId The identifier of {@link User} to check existence.
     * @param username The username of the {@link User} to check existence.
     * @param email The email of the {@link User} to check existence.
     *
     * @return {@code true} if {@link User} exists, {@code false} otherwise.
     */
    @Transactional(readOnly = true)
    @Query("SELECT count(u) > 0 FROM User AS u WHERE u.id = :userId "
            + "OR LOWER(u.username) = LOWER(:username) OR LOWER(u.email) = LOWER(:email)")
    boolean exists(@Param("userId") final UUID userId, @Param("username") final String username,
            @Param("email") final String email);
}
