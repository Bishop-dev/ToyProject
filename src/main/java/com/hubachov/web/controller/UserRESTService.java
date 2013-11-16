package com.hubachov.web.controller;

import com.hubachov.entity.User;
import com.hubachov.service.RoleService;
import com.hubachov.service.UserService;
import com.hubachov.validator.UserValidator;
import com.hubachov.web.form.UserForm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/service")
public class UserRESTService {
    private static final Logger log = Logger.getLogger(UserRESTService.class);
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<User> getUsers(HttpServletResponse response) throws IOException {
        try {
            return userService.findAll();
        } catch (Exception e) {
            log.error("Can't read users from DB", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object createUser(@RequestBody UserForm form, BindingResult result, HttpServletResponse response)
            throws IOException {
        try {
            if (userService.findByLogin(form.getLogin()) != null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Login busy");
                return form;
            }
        } catch (Exception e) {
            log.error("Can't check login", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return form;
        }
        new UserValidator().validate(form, result);
        if (result.hasErrors()) {
            log.info("Form has errors");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Errors in user form");
            return form;
        }
        try {
            userService.create(constructUser(form));
            User answer = userService.findByLogin(form.getLogin());
            return answer;
        } catch (Exception e) {
            log.error("Can't save user " + form.getLogin(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return form;
        }
    }

    @RequestMapping(value = "/{login}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User deleteUser(@PathVariable("login") String login, HttpServletResponse response) throws IOException {
        User user = null;
        try {
            user = userService.findByLogin(login);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_OK, "User " + login + " does not exist");
                return null;
            }
            userService.remove(user);
            return user;
        } catch (Exception e) {
            log.error("Can't delete user " + login, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return user;
        }
    }

    @RequestMapping(value = "/{login}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getUserByLogin(@PathVariable("login") String login, HttpServletResponse response) throws IOException {
        try {
            return userService.findByLogin(login);
        } catch (Exception e) {
            log.error("Can't get user " + login, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object updateUser(@RequestBody UserForm form, HttpServletResponse response, BindingResult result)
            throws IOException {
        new UserValidator().validate(form, result);
        if (result.hasErrors()) {
            log.info("User form contains errors");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Errors in user form");
            return form;
        }
        try {
            if (userService.findByLogin(form.getLogin()) == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User " + form.getLogin() + " does not exist");
                return form;
            }
            User user = constructUser(form);
            userService.update(user);
            return user;
        } catch (Exception e) {
            log.error("Can't update user " + form.getLogin(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return form;
        }
    }

    private User constructUser(UserForm form) throws SQLException {
        User user = new User();
        if (form.getId() != 0) {
            user.setId(form.getId());
        }
        user.setLogin(form.getLogin());
        user.setEmail(form.getEmail());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setPassword(form.getPassword());
        try {
            user.setBirthday(new SimpleDateFormat("MM/dd/yyyy").parse(form
                    .getBirthday()));
        } catch (ParseException e) {
            log.error("Can't parse date", e);
        }
        try {
            user.setRole(roleService.findByName(form.getRole()));
        } catch (SQLException e) {
            log.error("Can't get role " + form.getRole(), e);
            throw e;
        }
        return user;
    }

}
