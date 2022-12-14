package com.relativity.springstarter.starter.persistence.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.relativity.springstarter.starter.persistence.AbstractGenericEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User extends AbstractGenericEntity {

    /**
     * The {@code serialVersionUID}.
     */
    private static final long serialVersionUID = -7706401632533879097L;

    /**
     * Username maximum length.
     */
    public static final int MAX_LENGTH_USERNAME = 255;

    /**
     * Email maximum length.
     */
    public static final int MAX_LENGTH_EMAIL = 255;

    /**
     * Password maximum length.
     */
    public static final int MAX_LENGTH_PASSWORD = 512;

    /**
     * The user's firstname name.
     */
    @Column(name = "firstname", nullable = false, length = MAX_LENGTH_USERNAME)
    private String firstname = null;

    /**
     * The user's lastname name.
     */
    @Column(name = "lastname", nullable = false, length = MAX_LENGTH_USERNAME)
    private String lastname = null;

    /**
     * The user's account name.
     */
    @Column(name = "username", unique = true, nullable = false, length = MAX_LENGTH_USERNAME)
    private String username = null;

    /**
     * The user's account email.
     */
    @Column(name = "email", unique = true, nullable = false, length = MAX_LENGTH_EMAIL)
    private String email = null;

    /**
     * The user's account hashed password.
     */
    @Column(name = "password", length = MAX_LENGTH_PASSWORD, updatable = true)
    private String password = null;

    /**
     * Is the user's account active.
     */
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /**
     * Is the user's account verified.
     */
    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    // ################################################################
    // Relationships with all other entities
    // ################################################################

    /**
     * User accounts created by this user.
     */
    @JsonIgnore
    @OneToMany(mappedBy = CREATED_BY_FIELD, fetch = FetchType.LAZY)
    private final Set<User> createdUsers = new HashSet<>();
    /**
     * User accounts modified by this user.
     */
    @JsonIgnore
    @OneToMany(mappedBy = MODIFIED_BY_FIELD, fetch = FetchType.LAZY)
    private final Set<User> modifiedUsers = new HashSet<>();
    /**
     * User accounts owned by this user.
     */
    @JsonIgnore
    @OneToMany(mappedBy = OWNER_FIELD, fetch = FetchType.LAZY)
    private final Set<User> ownedUsers = new HashSet<>();

    /**
     * Create a {@link User}.
     *
     */
    public User() {
        super();
    }

    /**
     * Create a {@link User}.
     *
     * @param username username of this user.
     */
    public User(final String username) {
        super();
        this.username = username;
    }

    /**
     * Create a {@link User}.
     *
     * @param username username of this user.
     * @param email email of this user.
     */
    public User(final String username, final String email) {
        super();
        this.username = username;
        this.email = email;
    }

    /**
     * Create a copy of a {@link User}.
     *
     * @param other the other entity to copy.
     *
     * @throws NullPointerException if the {@code other} entity is @{code null}.
     */
    public User(final User other) {
        super(other);

        this.username = other.getUsername();
        this.email = other.getEmail();
        this.password = other.getPassword();
        this.enabled = other.isEnabled();
        this.verified = other.isVerified();
        this.username = other.getUsername();
    }

    /**
     * Get the {@link #username}.
     *
     * @return the {@link #username}.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the {@link #username}.
     *
     * @param username the {@link #username} to set.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Get the {@link #email}.
     *
     * @return the {@link #email}.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the {@link #email}.
     *
     * <p>
     * Note that changing the email will set the user as unverified.
     * </p>
     *
     * @param email the {@link #email} to set.
     */
    public void setEmail(final String email) {
        if (!Objects.equals(this.email, email)) {
            this.email = email;
            this.setVerified(false);
        }
    }

    /**
     * Get the {@link #password}.
     *
     * @return the {@link #password}.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the {@link #password}.
     *
     * @param password the clear {@link #password} to hash and set.
     */
    public final void setPassword(final char... password) {
        if (password == null) {
            this.password = null;
        } else {
            this.password = null; // TODO: need to implement hash generator for password
        }
    }

    /**
     * Set the {@link #password}.
     *
     * @param password the hashed {@link #password} to set.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Get the {@link #enabled}.
     *
     * @return the {@link #enabled}.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the {@link #enabled}.
     *
     * @param enabled the {@link #enabled} to set.
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the {@link #verified}.
     *
     * @return the {@link #verified}.
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * Set the {@link #verified}.
     *
     * @param verified the {@link #verified} to set.
     */
    public void setVerified(final boolean verified) {
        this.verified = verified;
    }

    @Override
    public <T extends AbstractGenericEntity> void update(T entity) {
        super.update(entity);

        if (entity instanceof User) {
            final User user = (User) entity;

            this.setUsername(user.getUsername());
            this.setEmail(user.getEmail());
            if (user.getPassword() != null) {
                this.setPassword(user.getPassword());
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();

        if (this.getUsername() != null) {
            hash = hash * 31 + this.getUsername().hashCode();
        }

        if (this.getEmail() != null) {
            hash = hash * 31 + this.getEmail().hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean equals = super.equals(obj);

        if (equals && obj instanceof User) {
            final User other = (User) obj;
            equals = Objects.equals(getUsername(), other.getUsername());

            if (equals) {
                equals = Objects.equals(getEmail(), other.getEmail());
            }
        }

        return equals;
    }

    @PreRemove
    protected void preRemove() {
        // Set to null references to this user
        resetRelationships(createdUsers, modifiedUsers, ownedUsers);
    }

    /**
     * Set usual relationships to {@code null}.
     *
     * @see #preRemove()
     *
     * @param created entities whose <em>"created by"</em> relation must be set to {@code null}
     * @param modified entities whose <em>"modified by"</em> relation must be set to {@code null}
     * @param owned entities whose <em>"owned by"</em> relation must be set to {@code null}
     *
     * @throws NullPointerException if any of the collection is {@code null} or contains {@code null}
     *         values
     */
    protected static void resetRelationships(Collection<? extends AbstractGenericEntity> created,
            Collection<? extends AbstractGenericEntity> modified,
            Collection<? extends AbstractGenericEntity> owned) {
        for (final AbstractGenericEntity entity : created) {
            entity.setCreatedBy(null);
        }
        for (final AbstractGenericEntity entity : modified) {
            entity.setModifiedBy(null);
        }
        for (final AbstractGenericEntity entity : owned) {
            entity.setOwner(null);
        }
    }
}
