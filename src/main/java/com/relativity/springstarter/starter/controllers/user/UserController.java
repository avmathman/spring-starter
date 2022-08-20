package com.relativity.springstarter.starter.controllers.user;

import com.relativity.springstarter.starter.controllers.AbstractGenericController;
import com.relativity.springstarter.starter.dtos.user.UserDto;
import com.relativity.springstarter.starter.entities.user.User;
import com.relativity.springstarter.starter.services.EntityNotFoundException;
import com.relativity.springstarter.starter.services.user.UserService;
import com.relativity.springstarter.starter.services.user.exception.UserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

/**
 * The {@link User} API Controller.
 *
 * @author avakhobov
 */
@RestController
public class UserController extends AbstractGenericController<User, UserDto> {

    /**
     * Logger for {@link UserController}.
     */
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    /**
     * The main data type handled by this controller.
     */
    public static final String TYPE = "Users";

    /**
     * The request base path of this controller.
     */
    public static final String CONTROLLER_PATH = SEP + "users";

    /**
     * Create a {@link UserController}.
     *
     * @param messageSource the i18n message source.
     * @param userService the users service.
     */
    @Autowired
    public UserController(
            MessageSource messageSource,
            UserService userService
    ) {
        super(messageSource, userService);
    }

    @Override
    protected String getControllerPath() {
        return CONTROLLER_PATH;
    }

    @Override
    protected EntityNotFoundException buildEntityNotFoundException(String id, WebRequest request) {
        return null;
    }

    @Override
    protected UserService getService() {
        return (UserService) super.getService();
    }

    @Override
    @GetMapping(value = CONTROLLER_PATH + "/{id}")
    public UserDto getDataById(@PathVariable String id, WebRequest request, HttpServletResponse response) {
        return super.getDataById(id, request, response);
    }

    @Override
    @GetMapping(value = CONTROLLER_PATH)
    public List<UserDto> getAllData(@RequestParam(value = SORT, defaultValue = DEFAULT_SORT_QUERY) String sort) {
        return super.getAllData(sort);
    }

    @Override
    @GetMapping(value = CONTROLLER_PATH, params = {PAGE})
    public List<UserDto> getAllDataPaginated(
            @RequestParam(value = SORT, defaultValue = DEFAULT_SORT_QUERY) String sort,
            @RequestParam(value = PAGE) int page,
            @RequestParam(value = SIZE, defaultValue = DEFAULT_SIZE) int size,
            WebRequest request,
            UriComponentsBuilder builder,
            HttpServletResponse response
    ) {
        return super.getAllDataPaginated(sort, page, size, request, builder, response);
    }

    @Override
    @PostMapping(value = CONTROLLER_PATH, consumes = "application/json")
    public ResponseEntity<UserDto> addData(
            @RequestBody UserDto dto,
            UriComponentsBuilder builder,
            HttpServletResponse response
    ) {
        return super.addData(dto, builder, response);
    }

    @Override
    public ResponseEntity<UserDto> updateData(@PathVariable String id, @RequestBody UserDto dto) {
        // TODO If user does not have ROLES_LIST authority, forbid role update
        return super.updateData(id, dto);
    }

    @Override
    @DeleteMapping(value = CONTROLLER_PATH + "/{id}")
    public ResponseEntity<Void> deleteData(@PathVariable String id) {
        return super.deleteData(id);
    }

    @Override
    @DeleteMapping(value = CONTROLLER_PATH, params = {IDS})
    public ResponseEntity<Void> deleteAllData(@RequestParam(value = IDS) String ids) {
        return super.deleteAllData(ids);
    }

    @GetMapping(value = CONTROLLER_PATH + "/get")
    public ResponseEntity<UserDto> getUserByUsernameOrEmail(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        UserDto dto = null;
        HttpStatus status;

        final User entity = this.getService().findByUsernameOrEmail(username, email);
        if (entity == null) {
            status = HttpStatus.NOT_FOUND;
        } else {
            dto = this.getService().toDto(entity);
            status = HttpStatus.OK;
        }

        return new ResponseEntity<>(dto, status);
    }
}
