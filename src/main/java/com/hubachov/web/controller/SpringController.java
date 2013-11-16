package com.hubachov.web.controller;

import com.hubachov.entity.User;
import com.hubachov.service.RoleService;
import com.hubachov.service.UserService;
import com.hubachov.validator.UserValidator;
import com.hubachov.web.form.UserForm;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Controller
public class SpringController {
    private static final Logger log = Logger.getLogger(SpringController.class);
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReCaptcha reCaptcha;

    private static final String PATH_LOGIN = "login";
    private static final String PATH_ADMIN_HOME = "admin/cabinet";
    private static final String PATH_USER_HOME = "user/index";
    private static final String PATH_REDIRECT = "redirect:";
    private static final String PATH_REGISTRATION = "registration";

    @RequestMapping(value = {"/login", "/"}, method = RequestMethod.GET)
    public String login() {
        return PATH_LOGIN;
    }

    @RequestMapping(value = "login-fail", method = RequestMethod.GET)
    public String loginFail(Map<String, Object> map) {
        map.put("message", "Wrong login/password");
        return PATH_LOGIN;
    }

    @RequestMapping(value = {"/index", "/cabinet"})
    public String index(HttpServletRequest request, Principal principal, HttpServletResponse response) throws IOException {
        String login = principal.getName();
        if (login != null) {
            try {
                request.getSession().setAttribute("user", userService.findByLogin(login));
            } catch (SQLException e) {
                log.error("Can't read current user from DB " + login, e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return PATH_LOGIN;
            }
        }
        if (request.isUserInRole("admin")) {
            return PATH_ADMIN_HOME;
        }
        if (request.isUserInRole("user")) {
            return PATH_USER_HOME;
        }
        return PATH_LOGIN;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        return PATH_REDIRECT + PATH_LOGIN;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("UserForm", new UserForm());
        return PATH_REGISTRATION;
    }

    @RequestMapping(value = "/registrate", method = RequestMethod.POST)
    public String register(@ModelAttribute("UserForm") UserForm userForm,
                           BindingResult result, HttpServletRequest request, Model model,
                           Principal principal,
                           @RequestParam("recaptcha_challenge_field") String challangeField,
                           @RequestParam("recaptcha_response_field") String responseField,
                           RedirectAttributes attributes, HttpServletResponse response) throws IOException {
        // check captcha
        String remoteAddress = request.getRemoteAddr();
        ReCaptchaResponse reCaptchaResponse = this.reCaptcha.checkAnswer(
                remoteAddress, challangeField, responseField);
        if (!reCaptchaResponse.isValid()) {
            model.addAttribute("message", "Wrong captcha");
            model.addAttribute("UserForm", userForm);
            log.info("Wrong captcha");
            return PATH_REGISTRATION;
        }
        // validate form
        new UserValidator().validate(userForm, result);
        if (result.hasErrors()) {
            model.addAttribute("message", "Wrong data");
            model.addAttribute("UserForm", userForm);
            log.warn("Wrong registration data");
            return PATH_REGISTRATION;
        }
        // check login
        String login = userForm.getLogin();
        User user = null;
        try {
            user = userService.findByLogin(login);
        } catch (SQLException e) {
            model.addAttribute("message", "Internal error. Try again.");
            model.addAttribute("UserForm", userForm);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Can't check login " + login, e);
            return PATH_REGISTRATION;
        }
        if (user != null) {
            model.addAttribute("message", "Login busy. Try another one.");
            model.addAttribute("UserForm", userForm);
            log.warn("Can't check login " + login);
            return PATH_REGISTRATION;
        }
        // if OK
        try {
            userService.create(createUser(userForm));
        } catch (SQLException e) {
            log.error("Can't save new user " + userForm.getLogin(), e);
            model.addAttribute("message", "Can't save new user. Try again.");
            model.addAttribute("UserForm", userForm);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return PATH_REGISTRATION;
        }
        attributes.addFlashAttribute("message",
                "You may enter using your login and password.");
        return PATH_REDIRECT + PATH_LOGIN;
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ResponseBody
    public String checkLogin(@RequestParam("login") String login, HttpServletResponse response) throws IOException {
        User user = null;
        try {
            user = userService.findByLogin(login);
        } catch (SQLException e) {
            log.error("Can't check login " + login, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        return user == null ? "true" : "false";
    }

    private User createUser(UserForm form) {
        User user = new User();
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
        }
        return user;
    }
}
