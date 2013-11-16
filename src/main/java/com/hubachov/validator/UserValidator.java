package com.hubachov.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.hubachov.web.form.UserForm;

public class UserValidator implements Validator {
    private Pattern emailPattern = Pattern
            .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public UserValidator() {

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "login",
                "Type login");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
                "password", "Type password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirm", "confirm",
                "Confirm password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email",
                "Type email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
                "firstName", "Type first name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
                "lastName", "Type last name");
        UserForm form = (UserForm) target;
        if (form.getLogin().length() > 100) {
            errors.reject("login", "Login too long");
        }
        if (form.getFirstName().length() > 100) {
            errors.reject("firstName", "First Name too long");
        }
        if (form.getLastName().length() > 100) {
            errors.reject("lastName", "Last Name too long");
        }
        if (form.getEmail().length() > 100) {
            errors.reject("email", "Email too long");
        }
        if (form.getPassword().length() > 100) {
            errors.reject("password", "Password too long");
        }
        if (!form.getPassword().equals(form.getConfirm())) {
            errors.reject("password", "Passwords do not match");
        }
        Matcher matcher = emailPattern.matcher(form.getEmail());
        if (!matcher.matches()) {
            errors.reject("email", "Wrong email");
        }
        try {
            Date date = new SimpleDateFormat("MM/dd/yyyy").parse(form
                    .getBirthday());
            if (date.getTime() > new Date().getTime()) {
                errors.reject("birthday", "Enter date in past");
            }
        } catch (ParseException e) {
            errors.reject("birthday", "Wrong birthday MM/DD/YYYY");
        }
        if (form.getRole() == null) {
            errors.reject("role", "Role required");
        }
    }

}
