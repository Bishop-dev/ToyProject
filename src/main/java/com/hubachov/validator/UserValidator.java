package com.hubachov.validator;

import com.hubachov.web.form.UserForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator implements Validator {
    private static Pattern emailPattern = Pattern
            .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    private UserForm target;
    private Errors errors;

    public UserValidator() {

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        this.target = (UserForm) target;
        this.errors = errors;
        validateLogin();
        validatePasswords();
        validateFirstName();
        validateLastName();
        validateEmail();
        validateBirthday();
        validateRole();
    }

    private void validateLogin() {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "login",
                "Type login");
        if (target.getLogin().length() > 100) {
            errors.reject("login", "Login too long");
        }
    }

    private void validatePasswords() {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
                "password", "Type password");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirm", "confirm",
                "Confirm password");
        if (target.getPassword().length() > 100) {
            errors.reject("password", "Password too long");
        }
        if (!target.getPassword().equals(target.getConfirm())) {
            errors.reject("password", "Passwords do not match");
        }
    }

    private void validateFirstName() {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
                "firstName", "Type first name");
        if (target.getFirstName().length() > 100) {
            errors.reject("firstName", "First Name too long");
        }
    }

    private void validateLastName() {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
                "lastName", "Type last name");
        if (target.getLastName().length() > 100) {
            errors.reject("lastName", "Last Name too long");
        }
    }

    private void validateEmail() {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email",
                "Type email");
        if (target.getEmail().length() > 100) {
            errors.reject("email", "Email too long");
        }
        Matcher matcher = emailPattern.matcher(target.getEmail());
        if (!matcher.matches()) {
            errors.reject("email", "Wrong email");
        }
    }

    private void validateBirthday() {
        try {
            Date date = new SimpleDateFormat("MM/dd/yyyy").parse(target
                    .getBirthday());
            if (date.getTime() > new Date().getTime()) {
                errors.reject("birthday", "Enter date in past");
            }
        } catch (ParseException e) {
            errors.reject("birthday", "Wrong birthday MM/DD/YYYY");
        }
    }

    private void validateRole() {
        if (target.getRole() == null) {
            errors.reject("role", "Role required");
        }
    }

}
