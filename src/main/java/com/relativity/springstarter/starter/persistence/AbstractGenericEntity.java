package com.relativity.springstarter.starter.persistence;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.relativity.springstarter.starter.persistence.user.entity.User;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * An abstract for generic entities.
 *
 * <p>
 * Provides a Universally Unique Identifier as primary key, creation and modification date and user
 * reference and ownership reference.
 * </p>
 *
 * <p>
 * <strong>Note:</strong> Make sure to mark all properties getter/setter as non-final, otherwise
 * Javassist Lazy Loaded proxies will not be able override them.
 * </p>
 *
 * @author madmath03
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@MappedSuperclass
public abstract class AbstractGenericEntity {

    /**
     * The {@code serialVersionUID}.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Entity <em>id</em> property.
     */
    public static final String ID_PROPERTY = "id";

    /**
     * Entity <em>id</em> length.
     */
    public static final int ID_MAX_LENGTH = 16;

    /**
     * Entity <em>created at</em> property.
     */
    public static final String CREATED_AT_PROPERTY = "created_at";

    /**
     * Entity <em>created by</em> property.
     */
    public static final String CREATED_BY_PROPERTY = "created_by";

    /**
     * Entity <em>created by</em> field.
     *
     * @see AbstractGenericEntity#createdBy
     */
    public static final String CREATED_BY_FIELD = "createdBy";

    /**
     * Entity <em>modified at</em> property.
     */
    public static final String MODIFIED_AT_PROPERTY = "modified_at";

    /**
     * Entity <em>modified by</em> property.
     */

    public static final String MODIFIED_BY_PROPERTY = "modified_by";

    /**
     * Entity <em>modified by</em> field.
     *
     * @see AbstractGenericEntity#modifiedBy
     */
    public static final String MODIFIED_BY_FIELD = "modifiedBy";

    /**
     * Entity <em>owner</em> property.
     */
    public static final String OWNER_PROPERTY = "owner";

    /**
     * Entity <em>owner</em> field.
     *
     * @see AbstractGenericEntity#owner
     */
    public static final String OWNER_FIELD = "owner";

    /**
     * The Universally Unique Identifier (primary key) of this record.
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = ID_PROPERTY, length = ID_MAX_LENGTH, nullable = false, updatable = false)
    private UUID id;

    /**
     * An auto-populating date/time stamp of when the record was created.
     */
    @Column(name = CREATED_AT_PROPERTY, columnDefinition = "TIMESTAMP", nullable = false,
            updatable = false)
    @CreatedDate
    private Date createdAt;

    /**
     * Foreign key (relation) to whom created the record.
     *
     * <p>
     * TODO Use CreatedBy annotation and AuditorAware
     * </p>
     */
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = CREATED_BY_PROPERTY, nullable = true)
    private User createdBy = null;

    /**
     * An auto-populating date/time stamp of when the record was last modified.
     */
    @Column(name = MODIFIED_AT_PROPERTY, columnDefinition = "TIMESTAMP", nullable = true,
            insertable = false)
    @LastModifiedDate
    private Date modifiedAt = null;

    /**
     * Foreign key (relation) to whom last modified the record.
     *
     * <p>
     * TODO Use LastModifiedBy annotation and AuditorAware
     * </p>
     */
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = MODIFIED_BY_PROPERTY, nullable = true)
    private User modifiedBy = null;

    /**
     * Foreign key (relation) to whom owns the record.
     */
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = OWNER_PROPERTY, nullable = true)
    private User owner = null;

    /**
     * Create a {@link AbstractGenericEntity}.
     *
     */
    public AbstractGenericEntity() {
        super();
    }

    /**
     * Create a copy of a {@link AbstractGenericEntity}.
     *
     * @param other the {@link AbstractGenericEntity} entity to copy.
     *
     * @throws NullPointerException if the {@code other} entity is @{code null}.
     */
    public AbstractGenericEntity(final AbstractGenericEntity other) {
        super();

        this.id = other.getId();
        this.createdAt = other.getCreatedAt();
        this.createdBy = other.getCreatedBy();
        this.modifiedAt = other.getModifiedAt();
        this.modifiedBy = other.getModifiedBy();
        this.owner = other.getOwner();
    }

    /**
     * Get the {@link #id}.
     *
     * @return the {@link #id}.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Set the {@link #id}.
     *
     * @param id the {@link #id} to set.
     */
    public void setId(final UUID id) {
        this.id = id;
    }

    /**
     * Get the {@link #createdAt}.
     *
     * @return the {@link #createdAt}.
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the {@link #createdAt}.
     *
     * @param createdAt the {@link Date} to set {@link #createdAt}.
     */
    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the {@link #createdBy}.
     *
     * @return the {@link #createdBy}.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the {@link #createdBy}.
     *
     * @param createdBy the {@link User} to set {@link #createdBy}.
     */
    public void setCreatedBy(final User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the {@link #modifiedAt}.
     *
     * @return the {@link #modifiedAt}.
     */
    public Date getModifiedAt() {
        return modifiedAt;
    }

    /**
     * Set the {@link #modifiedAt}.
     *
     * @param modifiedAt the {@link Date} to set {@link #modifiedAt}.
     */
    public void setModifiedAt(final Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    /**
     * Get the {@link #modifiedBy}.
     *
     * @return the {@link #modifiedBy}.
     */
    public User getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Set the {@link #modifiedBy}.
     *
     * @param modifiedBy the {@link User} to set {@link #modifiedBy}.
     */
    public void setModifiedBy(final User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Get the {@link #owner}.
     *
     * @return the {@link #owner}.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Set the {@link #owner}.
     *
     * @param owner the {@link User} to set {@link #owner}.
     */
    public void setOwner(final User owner) {
        this.owner = owner;
    }

    /**
     * Update the entity with the properties of another entity.
     *
     * @param <T> Entity class.
     *
     * @param entity another entity.
     *
     * @throws NullPointerException if {@code entity} is {@code null}.
     */
    public <T extends AbstractGenericEntity> void update(final T entity) {
        if (!Objects.equals(this.getOwner(), entity.getOwner())) {
            this.setOwner(entity.getOwner());
        }
    }

    @Override
    public int hashCode() {
        int hash = 17;

        if (this.getId() != null) {
            hash = hash * 31 + this.getId().hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean equals;

        if (this == obj) {
            equals = true;
        } else if (obj == null) {
            equals = false;
        } else if (!(obj instanceof AbstractGenericEntity)) {
            equals = false;
        } else {
            final AbstractGenericEntity other = (AbstractGenericEntity) obj;
            equals = Objects.equals(getId(), other.getId());
        }

        return equals;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

    @PrePersist
    protected void preInsert() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
    }

    @PreUpdate
    protected void preUpdate() {
        this.modifiedAt = new Date();
    }
}
