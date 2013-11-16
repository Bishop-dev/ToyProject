package com.hubachov.web.form;

import java.text.SimpleDateFormat;

import com.hubachov.entity.User;

public class UserForm {
    private long id;
    private String login;
    private String password;
    private String confirm;
    private String firstName;
    private String lastName;
    private String email;
    private String birthday;
    private String role;

    public UserForm() {

    }

    public UserForm(User user) {
        if (user.getId() != null) {
            this.id = user.getId();
        }
        this.login = user.getLogin() == null ? "" : user.getLogin();
        this.password = user.getPassword() == null ? "" : user.getPassword();
        this.confirm = user.getPassword() == null ? "" : user.getPassword();
        this.firstName = user.getFirstName() == null ? "" : user.getFirstName();
        this.lastName = user.getLastName() == null ? "" : user.getLastName();
        this.email = user.getEmail() == null ? "" : user.getEmail();
        this.birthday = user.getBirthday() == null ? "" : new SimpleDateFormat("MM/dd/yyyy")
                .format(user.getBirthday());
        if (user.getRole() != null) {
            this.role = user.getRole().getName();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserForm)) return false;

        UserForm userForm = (UserForm) o;

        if (id != userForm.id) return false;
        if (birthday != null ? !birthday.equals(userForm.birthday) : userForm.birthday != null) return false;
        if (confirm != null ? !confirm.equals(userForm.confirm) : userForm.confirm != null) return false;
        if (email != null ? !email.equals(userForm.email) : userForm.email != null) return false;
        if (firstName != null ? !firstName.equals(userForm.firstName) : userForm.firstName != null) return false;
        if (lastName != null ? !lastName.equals(userForm.lastName) : userForm.lastName != null) return false;
        if (login != null ? !login.equals(userForm.login) : userForm.login != null) return false;
        if (password != null ? !password.equals(userForm.password) : userForm.password != null) return false;
        if (role != null ? !role.equals(userForm.role) : userForm.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (confirm != null ? confirm.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserForm{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", confirm='" + confirm + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", birthday='" + birthday + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
