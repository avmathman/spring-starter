package com.relativity.springstarter.starter.controllers;

import com.relativity.springstarter.starter.dtos.AbstractGenericDto;
import com.relativity.springstarter.starter.entities.AbstractGenericEntity;
import com.relativity.springstarter.starter.exceptions.PageNotFoundException;
import com.relativity.springstarter.starter.services.EntityNotFoundException;
import com.relativity.springstarter.starter.services.GenericService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

/**
 * Generic Entity Controller.
 *
 * <p>
 * Provides the typical CRUD operations. The implementation need to call (or override) the methods
 * here in order to map a a request path to each of them.
 * </p>
 *
 * @param <T> the entity type used to manage data in persistence storage.
 * @param <D> the DTO type to convert entities to/from.
 *
 * @author avakhobov
 */
public abstract class AbstractGenericController<T extends AbstractGenericEntity, D extends AbstractGenericDto> {

    /**
     * Logger for {@link AbstractGenericController}.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGenericController.class);

    /**
     * Default separator used for controller path.
     */
    public static final char SEP = '/';

    /**
     * Sort query parameter name.
     */
    public static final String SORT = "sort";

    /**
     * Page number query parameter name.
     */
    public static final String PAGE = "page";

    /**
     * Page size query parameter name.
     */
    public static final String SIZE = "size";

    /**
     * Unique identifiers query parameter name.
     */
    public static final String IDS = "ids";

    /**
     * Default page size.
     */
    public static final int DEFAULT_SIZE_INT = 10;

    /**
     * Default page size.
     */
    public static final String DEFAULT_SIZE = "" + DEFAULT_SIZE_INT;

    private final MessageSource messageSource;

    private final GenericService<T, D> service;

    /**
     * Create a {@link AbstractGenericController}.
     *
     * @param service the entity service.
     */
    public AbstractGenericController(final MessageSource messageSource, final GenericService<T, D> service) {
        super();
        this.messageSource = messageSource;
        this.service = service;
    }

    /**
     * Get the {@link #service}.
     *
     * @return the {@link #service}.
     */
    protected GenericService<T, D> getService() {
        return service;
    }

    /**
     * Get the controller base path.
     *
     * <p>
     * This is used to return additional information like the path an entity which was created.
     * </p>
     *
     * @return the controller base path.
     */
    protected abstract String getControllerPath();

    /**
     * Get a {@link T} entity by its unique identifier.
     *
     * <p>
     * Returns a {@link D} JSON representation about a single data.
     * </p>
     *
     * @param id <em>Required URL Path variable:</em> universal unique identifier (i.e. {@code UUID}).
     * @param request the Web Request.
     * @param response the HTTP response.
     *
     * @return
     *         <ul>
     *
     *         <li>
     *         <p>
     *         <strong>Success Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.OK</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> a {@link D} JSON representation of the entity {@link T}
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         <li>
     *         <p>
     *         <strong>Error Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.NOT_FOUND</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> <code>null</code>
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         </ul>
     *
     * @throws IllegalArgumentException if the specified is unique ID is not valid
     * @throws EntityNotFoundException if no entity matches the specified unique ID
     */
    public D getDataById(@PathVariable String id, WebRequest request, HttpServletResponse response) {
        // Convert ID
        final UUID uniqueId = UUID.fromString(id);

        // Search entity
        final T entity = this.service.findById(uniqueId).orElse(null);
        if (entity == null) {
            throw this.buildEntityNotFoundException(id, request);
        }

        // Convert entity to DTO
        final D dto = this.service.toDto(entity);

        return dto;
    }

    /**
     * Build an <em>entity not found</em> exception, ideally with an appropriate and translated error
     * message.
     *
     * @param id entity unique ID which could not be found.
     * @param request the Web Request.
     *
     * @return an <em>entity not found</em> exception.
     */
    protected abstract EntityNotFoundException buildEntityNotFoundException(String id, WebRequest request);


    /**
     * Sort query separator for properties.
     *
     * @see AbstractGenericController#constructSortOrders(String)
     */
    public static final String SORT_PROP_SEP = ";";
    /**
     * Sort query separator for property options.
     *
     * @see AbstractGenericController#constructSortOrders(String)
     */
    public static final String SORT_OPT_SEP = ",";
    /**
     * Default sort applied in listing functions when none provided.
     */
    public static final String DEFAULT_SORT_QUERY =
            "createdAt" + SORT_OPT_SEP + "DESC" + SORT_PROP_SEP + "modifiedAt" + SORT_OPT_SEP + "DESC";

    /**
     * Construct a list of {@code Sort.Order} from a String query.
     *
     * <p>
     * The string query is expected to be of the following form:
     * {@code PROPERTY[,DIRECTION[,NULL_HINT]];...}.
     * </p>
     *
     * <p>
     * For instance:
     * </p>
     *
     * <pre>
     * property1;property2,ASC;property3,DESC,NULLS_FIRST;property4,asc,NULLS_LAST
     * </pre>
     *
     * @see AbstractGenericController#constructSortOrders(String)
     *
     * @param sortQuery the sort query.
     *
     * @return a {@code Sort} object or {@code null}.
     */
    protected static List<Sort.Order> constructSortOrders(String sortQuery) {
        final List<Sort.Order> orders;

        if (sortQuery != null && !sortQuery.isEmpty()) {
            orders = new ArrayList<>();

            final String[] propertiesWithOptions = sortQuery.split(SORT_PROP_SEP);
            for (String propertyWithOptions : propertiesWithOptions) {
                final Sort.Order order;

                final String[] propertyOptions = propertyWithOptions.split(SORT_OPT_SEP);
                switch (propertyOptions.length) {
                    case 1:
                        // Only property provided
                        order = new Order(null, propertyOptions[0]);
                        break;

                    case 2:
                        // Property and sort direction provided
                        order = new Order(Direction.fromString(propertyOptions[1]), propertyOptions[0]);
                        break;

                    case 3:
                        // Property, sort direction and null hint provided
                        order = new Order(Direction.fromString(propertyOptions[1]), propertyOptions[0],
                                Sort.NullHandling.valueOf(propertyOptions[2]));
                        break;

                    default:
                        LOG.debug("Invalid sort entry: {}", propertyWithOptions);
                        order = null;
                        break;
                }

                if (order != null) {
                    orders.add(order);
                }
            }

        } else {
            orders = null;
        }

        return orders;
    }

    /**
     * Construct a {@code Sort} object from a String query.
     *
     * @see AbstractGenericController#constructSortOrders(String)
     *
     * @param sortQuery the sort query.
     *
     * @return a {@code Sort} object or {@code null}.
     */
    protected static Sort constructSort(String sortQuery) {
        final List<Sort.Order> orders = constructSortOrders(sortQuery);

        final Sort order;
        if (orders != null && !orders.isEmpty()) {
            order = Sort.by(orders);
        } else {
            order = null;
        }

        return order;
    }

    /**
     * Get all available {@link T} entities.
     *
     * <p>
     * Returns a {@link D} JSON representation about a data array.
     * </p>
     *
     * @param sortQuery sort query to be converted and passed to repository
     *
     * @return
     *         <ul>
     *
     *         <li>
     *         <p>
     *         <strong>Success Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.OK</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> a {@link D} JSON representation of a {@link T} Array
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         </ul>
     */
    public List<D> getAllData(String sortQuery) {
        final Sort sort = constructSort(sortQuery);

        final List<T> result;

        if (sort != null) {
            result = service.findAll(sort);
        } else {
            result = service.findAll();
        }

        return service.toDto(result);
    }



    /**
     * Get all available {@link T} entities paginated.
     *
     * <p>
     * Returns a {@link D} JSON representation about a data array.
     * </p>
     *
     * @param sortQuery sort query to be converted and passed to repository
     * @param page <em>Required Request parameter:</em> zero-based page index.
     * @param size <em>Required Request parameter:</em> the size of the page to be returned.
     * @param request the Web Request.
     * @param builder an URI builder to build the URI to the created {@link T} in the response.
     * @param response HTTP response.
     *
     * @return
     *         <ul>
     *
     *         <li>
     *         <p>
     *         <strong>Success Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.OK</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> a {@link D} JSON representation of a {@link T} Array
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         </ul>
     */
    public List<D> getAllDataPaginated(String sortQuery, int page, int size,
            WebRequest request, UriComponentsBuilder builder, HttpServletResponse response) {
        final Sort sort = constructSort(sortQuery);

        final Page<T> resultPage;

        if (sort != null) {
            resultPage = service.findAll(page, size, sort);
        } else {
            resultPage = service.findAll(page, size);
        }

        if (resultPage == null) {
            throw this.buildPageNotFoundException(page, 0, request);
        } else if (page > resultPage.getTotalPages()) {
            throw this.buildPageNotFoundException(page, resultPage.getTotalPages(), request);
        }

        final List<T> result = resultPage.getContent();

        return service.toDto(result);
    }

    /**
     * Build a <em>page not found</em> exception, ideally with an appropriate and translated error
     * message.
     *
     * @param page zero-based page index.
     * @param totalPages total number of available pages.
     * @param request the Web Request.
     *
     * @return an <em>entity not found</em> exception.
     */
    protected PageNotFoundException buildPageNotFoundException(int page, int totalPages,
            WebRequest request) {
        final Locale locale = request.getLocale();
        final String msg = messageSource.getMessage("controller.page_not_found",
                new String[] {"" + page, "" + totalPages}, locale);

        return new PageNotFoundException(msg);
    }

    /**
     * Add a {@link T}.
     *
     * <p>
     * Create an entity and return a {@link D} JSON representation about the entity created.
     * </p>
     *
     * @param dto <em>Required Body Content:</em> a {@link D} JSON representation about the {@link T}
     *        to create.
     * @param builder an URI builder to build the URI to the created {@link T} in the response.
     * @param response HTTP response.
     *
     * @return
     *         <ul>
     *
     *         <li>
     *         <p>
     *         <strong>Success Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.CREATED</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> a {@link D} JSON representation of the {@link T} created
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         <li>
     *         <p>
     *         <strong>Error Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.CONFLICT</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> <code>{}</code>
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         </ul>
     */
    public ResponseEntity<D> addData(@RequestBody D dto, UriComponentsBuilder builder, HttpServletResponse response) {
        final T entity = this.addEntity(dto);

        final ResponseEntity<D> responseEntity;
        if (entity != null) {
            final HttpHeaders headers = new HttpHeaders();
            headers.setLocation(
                    builder.path(this.getControllerPath() + "/{id}").buildAndExpand(dto.getId()).toUri());

            final D responseDto = this.service.toDto(entity);
            responseEntity = new ResponseEntity<>(responseDto, headers, HttpStatus.CREATED);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    /**
     * Add a {@link T}.
     *
     * @param dto a {@link D} to create.
     *
     * @return a result DTO
     */
    protected D addData(@RequestBody D dto) {
        final T entity = this.addEntity(dto);

        final D responseDto;
        if (entity != null) {
            responseDto = this.service.toDto(entity);
        } else {
            responseDto = null;
        }

        return responseDto;
    }

    private T addEntity(@RequestBody D dto) {

        final T entity = this.service.toEntity(dto);

        final boolean added = service.add(entity);

        final T response;
        if (added) {
            response = entity;
        } else {
            response = null;
        }

        return response;
    }

    /**
     * Update a {@link T} entity.
     *
     * <p>
     * Update an entity and return a {@link D} JSON representation about the entity updated.
     * </p>
     *
     * @param id <em>Required URL Path variable:</em> universal unique identifier ( i.e.
     *        {@code UUID}).
     * @param dto <em>Required Body Content:</em> a {@link D} JSON representation about the {@link T}
     *        to update.
     *
     * @return
     *         <ul>
     *
     *         <li>
     *         <p>
     *         <strong>Success Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.OK</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> a {@link D} JSON representation of the {@link T} updated
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         <li>
     *         <p>
     *         <strong>Error Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.NOT_FOUND</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> <code>{}</code>
     *         </p>
     *         </li>
     *         </ul>
     *
     *         <p>
     *         OR
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.BAD_REQUEST</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> <code>{}</code>
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         </ul>
     */
    public ResponseEntity<D> updateData(@PathVariable String id, @RequestBody D dto) {
        HttpStatus status;
        D updatedDto = null;

        try {
            if (dto == null || dto.getId() == null || !Objects.equals(id, dto.getId().toString())) {
                status = HttpStatus.BAD_REQUEST;
            } else {
                // Only update if owner or has administration authorities
                final T updatedEntity;

                updatedEntity = service.update(this.service.toEntity(dto));

                if (updatedEntity == null) {
                    status = HttpStatus.NOT_FOUND;
                } else {
                    updatedDto = this.service.toDto(updatedEntity);
                    status = HttpStatus.OK;
                }
            }
        } catch (EntityNotFoundException e) {
            LOG.debug("updateData(id=" + id + ")", e);
            status = HttpStatus.NOT_FOUND;
        }

        return new ResponseEntity<>(updatedDto, status);
    }

    /**
     * Delete a {@link T}.
     *
     * @param id <em>Required URL Path variable:</em> universal unique identifier (i.e. {@code UUID}).
     *
     * @return
     *         <ul>
     *
     *         <li>
     *         <p>
     *         <strong>Success Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.NO_CONTENT</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> <code>null</code>
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         <li>
     *         <p>
     *         <strong>Error Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.NOT_FOUND</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> <code>null</code>
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         </ul>
     */
    public ResponseEntity<Void> deleteData(@PathVariable String id) {

        // Convert ID
        final UUID uniqueId = UUID.fromString(id);

        HttpStatus status;
        final boolean deleted = this.deleteDataById(uniqueId);
        if (deleted) {
            status = HttpStatus.NO_CONTENT;
        } else {
            status = HttpStatus.NOT_FOUND;
        }

        return new ResponseEntity<>(status);
    }

    /**
     * Delete all {@link T} by their unique identifiers.
     *
     * @param ids <em>Required URL Parameter:</em> comma separated universal unique identifiers (i.e.
     *        {@code UUID}).
     *
     * @return
     *         <ul>
     *
     *         <li>
     *         <p>
     *         <strong>Success Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.NO_CONTENT</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> <code>null</code>
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         <li>
     *         <p>
     *         <strong>Error Response:</strong>
     *         </p>
     *
     *         <ul>
     *         <li>
     *         <p>
     *         <strong>Code:</strong> <code>HttpStatus.NOT_FOUND</code>
     *         </p>
     *         <p>
     *         <strong>Content:</strong> <code>null</code>
     *         </p>
     *         </li>
     *         </ul>
     *
     *         </li>
     *
     *         </ul>
     */
    public ResponseEntity<Void> deleteAllData(String ids) {
        // Convert IDs
        final String[] idArray = ids.split(",");
        final List<UUID> uniqueIds = new ArrayList<>(idArray.length);
        for (String id : idArray) {
            try {
                uniqueIds.add(UUID.fromString(id));
            } catch (IllegalArgumentException e) {
                LOG.debug("invalid id=" + id, e);
            }
        }

        HttpStatus status;
        final boolean deleted = this.deleteAllDataById(uniqueIds);
        if (deleted) {
            status = HttpStatus.NO_CONTENT;
        } else {
            status = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(status);
    }

    private boolean deleteAllDataById(List<UUID> ids) {
        boolean deleted = true;

        for (UUID id : ids) {
            deleted &= this.deleteDataById(id);
        }

        return deleted;
    }

    private boolean deleteDataById(UUID id) {
        boolean deleted;

        try {
            service.deleteById(id);

            deleted = true;
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            LOG.debug("deleteData(id=" + id + ")", e);
            deleted = false;
        }

        return deleted;
    }
}
