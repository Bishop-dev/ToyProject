package com.hubachov.web.controller;

import com.hubachov.entity.Role;
import com.hubachov.entity.User;
import com.hubachov.service.RoleService;
import com.hubachov.service.UserService;
import com.hubachov.web.form.UserForm;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserRESTServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private RoleService roleService;
    @InjectMocks
    private UserRESTService controller;
    private MockMvc mockMvc;
    private User admin;
    private User user;
    private List<User> defaultUsers = Arrays.asList(admin, user);
    private static final String PATH__REST_SERVICE = "/service";

    @Before
    public void setUp() throws Exception {
        Date now = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/2000");
        admin = new User.UserBuilder().id(1L).login("admin").password("admin").email("admin@mail.com").
                firstName("Admin").lastName("Adminovich").birthday(now).role(new Role.RoleBuilder().
                id(1L).name("admin").build()).build();
        user = new User.UserBuilder().id(2L).login("user").password("user").email("user@mail.com").firstName("User").
                lastName("Userovich").birthday(now).role(new Role.RoleBuilder().id(2L).name("user").build()).build();
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetAll() throws Exception {
        when(userService.findAll()).thenReturn(defaultUsers);
        ResultActions actions = mockMvc.perform(get(PATH__REST_SERVICE));
        actions.andExpect(status().isOk());
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        List<User> users = getListFromJSON(content);
        assertThat(defaultUsers, is(users));
        verify(userService, times(1)).findAll();
    }

    @Test
    public void testGetAllWith500Error() throws Exception {
        when(userService.findAll()).thenThrow(new SQLException());
        ResultActions actions = mockMvc.perform(get(PATH__REST_SERVICE));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue("Content should be empty", content.isEmpty());
    }

    @Test
    public void testGetUserByLogin() throws Exception {
        when(userService.findByLogin(admin.getLogin())).thenReturn(admin);
        ResultActions actions = mockMvc.perform(get(PATH__REST_SERVICE + "/" + admin.getLogin()));
        actions.andExpect(status().isOk());
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        User user = getUserFromJSON(content);
        assertThat("Server returned not the same user", admin, is(user));
        verify(userService, times(1)).findByLogin(admin.getLogin());
    }

    @Test
    public void testGetByNotExistingLogin() throws Exception {
        String login = "unknown";
        when(userService.findByLogin(login)).thenReturn(null);
        ResultActions actions = mockMvc.perform(get(PATH__REST_SERVICE + "/" + login));
        actions.andExpect(status().isOk());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue(content.isEmpty());
        verify(userService, times(1)).findByLogin(any(String.class));
    }

    @Test
    public void testGetUserByLoginWith500Error() throws Exception {
        String login = "login";
        when(userService.findByLogin(login)).thenThrow(new SQLException());
        ResultActions actions = mockMvc.perform(get(PATH__REST_SERVICE + "/" + login));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue("Service should not return anything", content.isEmpty());
        verify(userService, times(1)).findByLogin(any(String.class));
    }

    @Test
    public void testCreateUser() throws Exception {
        doNothing().when(userService).create(user);
        when(userService.findByLogin(user.getLogin())).thenReturn(null).thenReturn(user);
        when(roleService.findByName(user.getRole().getName())).thenReturn(user.getRole());
        ResultActions actions = mockMvc.perform(post(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(new UserForm(user))));
        actions.andExpect(status().isOk());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        User created = getUserFromJSON(content);
        assertThat("User posted for create does not match with returned", user, is(created));
        InOrder order = inOrder(userService, roleService);
        order.verify(userService).findByLogin(user.getLogin());
        order.verify(roleService).findByName(user.getRole().getName());
        order.verify(userService).create(user);
        order.verify(userService).findByLogin(user.getLogin());
    }

    @Test
    public void createUserWithBusyLogin() throws Exception {
        when(userService.findByLogin(any(String.class))).thenReturn(new User());
        UserForm form = new UserForm(user);
        ResultActions actions = mockMvc.perform(post(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(status().reason("Login busy"));
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Service should return user form", form, is(returned));
        verify(userService, times(1)).findByLogin(user.getLogin());
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void createUserWith500ErrorOnLoginCheck() throws Exception {
        when(userService.findByLogin(any(String.class))).thenThrow(new SQLException());
        UserForm form = new UserForm(user);
        ResultActions actions = mockMvc.perform(post(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Service should return user form", form, is(returned));
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void createUserWith500ErrorOnCreate() throws Exception {
        doThrow(new SQLException()).when(userService).create(any(User.class));
        when(userService.findByLogin(any(String.class))).thenReturn(null);
        UserForm form = new UserForm(user);
        ResultActions actions = mockMvc.perform(post(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Service should return user form", form, is(returned));
        verify(userService).findByLogin(user.getLogin());
    }

    @Test
    public void testCreateEmptyUser() throws Exception {
        User emptyUser = new User();
        when(userService.findByLogin(user.getLogin())).thenReturn(null);
        doThrow(new IllegalArgumentException()).when(userService).create(emptyUser);
        UserForm form = new UserForm(emptyUser);
        byte[] emptyUserFormByteArray = getUserFormAsByteArray(form);
        ResultActions actions = mockMvc.perform(post(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(emptyUserFormByteArray));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(status().reason("Errors in user form"));
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Servlet should not return any user", form, is(returned));
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testCreateNullUser() throws Exception {
        doThrow(new IllegalArgumentException()).when(userService).create(any(User.class));
        ResultActions actions = mockMvc.perform(post(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(""));
        actions.andExpect(status().isBadRequest());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue("Servlet should not return any user", content.isEmpty());
        verify(userService, never()).create(any(User.class));
        verify(userService, never()).findByLogin(any(String.class));
    }

    @Test
    public void testCreateUserInOtherContentType() throws Exception {
        ResultActions actions = mockMvc.perform(post(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_XML).
                content(getUserAsByteArray(user)));
        actions.andExpect(status().isUnsupportedMediaType());
        verify(userService, never()).create(user);
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userService.findByLogin(user.getLogin())).thenReturn(user);
        doNothing().when(userService).remove(user);
        ResultActions actions = mockMvc.perform(delete(PATH__REST_SERVICE + "/" + user.getLogin()));
        actions.andExpect(status().isOk());
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        MvcResult result = actions.andReturn();
        User deleted = getUserFromJSON(result.getResponse().getContentAsString());
        verify(userService, times(1)).remove(user);
        assertThat("Service must return deleted user", user, is(deleted));
        InOrder order = inOrder(userService);
        order.verify(userService).findByLogin(user.getLogin());
        order.verify(userService).remove(user);
    }

    @Test
    public void testDeleteUserWith500ErrorOnDelete() throws Exception {
        when(userService.findByLogin(anyString())).thenReturn(user);
        doThrow(new SQLException()).when(userService).remove(any(User.class));
        ResultActions actions = mockMvc.perform(delete(PATH__REST_SERVICE + "/" + user.getLogin()));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        User deleted = getUserFromJSON(content);
        assertThat("Service should return user for delete", user, is(deleted));
        verify(userService, times(1)).findByLogin(user.getLogin());
    }

    @Test
    public void testDeleteUserWith500ErrorOnFindUser() throws Exception {
        doThrow(new SQLException()).when(userService).findByLogin(anyString());
        ResultActions actions = mockMvc.perform(delete(PATH__REST_SERVICE + "/" + user.getLogin()));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue("Service should not return anything", content.isEmpty());
        verify(userService, never()).remove(user);
    }

    @Test
    public void testDeleteNotExistingUser() throws Exception {
        String login = "empty";
        when(userService.findByLogin(login)).thenReturn(null);
        ResultActions actions = mockMvc.perform(delete(PATH__REST_SERVICE + "/" + login));
        actions.andExpect(status().isOk());
        actions.andExpect(status().reason("User " + login + " does not exist"));
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue("Servlet should not return any user", content.isEmpty());
        verify(userService, never()).remove(null);
    }

    @Test
    public void testUserBornInFuture() throws Exception {
        user.setBirthday(new SimpleDateFormat("MM/dd/yyyy").parse("12/01/2050"));
        UserForm form = new UserForm(user);
        when(userService.findByLogin(user.getLogin())).thenReturn(user);
        ResultActions actions = mockMvc.perform(put(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isBadRequest());
        actions.andExpect(status().reason("Errors in user form"));
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Servlet should return user form", form, is(returned));
        verify(userService, never()).update(any(User.class));
    }

    @Test
    public void testUpdateUser() throws Exception {
        doNothing().when(userService).update(user);
        when(userService.findByLogin(user.getLogin())).thenReturn(user).thenReturn(user);
        when(roleService.findByName(user.getRole().getName())).thenReturn(user.getRole());
        user.setFirstName("FirstName");
        ResultActions actions = mockMvc.perform(put(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(new UserForm(user))));
        actions.andExpect(status().isOk());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        User updated = getUserFromJSON(content);
        assertThat("Server returned not the same user", user, is(updated));
        verify(userService, times(1)).update(user);
        InOrder order = inOrder(userService, roleService);
        order.verify(userService).findByLogin(user.getLogin());
        order.verify(roleService).findByName(user.getRole().getName());
        order.verify(userService).update(user);
    }

    @Test
    public void testUpdateNotExistingUser() throws Exception {
        User notExisting = new User.UserBuilder().id(100500L).login("empty").firstName("Empty").lastName("Emptievich").
                email("empty@mail.com").password("empty").birthday(new Date()).role(user.getRole()).build();
        UserForm form = new UserForm(notExisting);
        when(userService.findByLogin(notExisting.getLogin())).thenReturn(null);
        ResultActions actions = mockMvc.perform(put(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isBadRequest());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Response should not send content in case of unknown user", form, is(returned));
        verify(userService, times(1)).findByLogin(notExisting.getLogin());
        verify(userService, never()).update(any(User.class));
    }

    @Test
    public void testUpdateWrongUser() throws Exception {
        user.setRole(null);
        UserForm form = new UserForm(user);
        when(userService.findByLogin(user.getLogin())).thenReturn(user);
        ResultActions actions = mockMvc.perform(put(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isBadRequest());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Service should not send user in case of wrong attributes", form, is(returned));
        verify(userService, never()).findByLogin(user.getLogin());
        verify(userService, never()).update(user);
    }

    @Test
    public void testUpdateNullUser() throws Exception {
        ResultActions actions = mockMvc.perform((put(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content("")));
        actions.andExpect(status().isBadRequest());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue("Servlet should not return any user", content.isEmpty());
        verify(userService, never()).update(any(User.class));
    }

    @Test
    public void testUpdateUserWith500ErrorOnFindUser() throws Exception {
        doThrow(new SQLException()).when(userService).findByLogin(anyString());
        UserForm form = new UserForm(user);
        ResultActions actions = mockMvc.perform(put(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Service should return form", returned, is(form));
        verify(userService, never()).update(user);
    }

    @Test
    public void testUpdateUserWith500ErrorOnUpdate() throws Exception {
        when(userService.findByLogin(anyString())).thenReturn(user);
        doThrow(new SQLException()).when(userService).update(any(User.class));
        UserForm form = new UserForm(user);
        ResultActions actions = mockMvc.perform(put(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Service should return form", returned, is(form));
        verify(userService, never()).update(user);
    }

    @Test
    public void testCreateUserOnConstructUserError() throws Exception {
        doThrow(new SQLException()).when(roleService).findByName(user.getRole().getName());
        when(userService.findByLogin(anyString())).thenReturn(null);
        UserForm form = new UserForm(user);
        ResultActions actions = mockMvc.perform(post(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Service should return form", form, is(returned));
        verify(userService, never()).create(user);
    }

    @Test
    public void testUpdateUserOnConstructUserError() throws Exception {
        doThrow(new SQLException()).when(roleService).findByName(user.getRole().getName());
        when(userService.findByLogin(anyString())).thenReturn(user);
        UserForm form = new UserForm(user);
        ResultActions actions = mockMvc.perform(put(PATH__REST_SERVICE).contentType(MediaType.APPLICATION_JSON).
                content(getUserFormAsByteArray(form)));
        actions.andExpect(status().isInternalServerError());
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        UserForm returned = getUserFormFromJson(content);
        assertThat("Service should return form", form, is(returned));
        verify(userService, never()).update(user);
    }

    private List<User> getListFromJSON(String json) throws IOException {
        return new ObjectMapper().readValue(json, new TypeReference<List<User>>() {
        });
    }

    private User getUserFromJSON(String json) throws IOException {
        return new ObjectMapper().readValue(json, User.class);
    }

    private UserForm getUserFormFromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, UserForm.class);
    }

    private byte[] getUserAsByteArray(User user) throws Exception {
        return new ObjectMapper().writeValueAsBytes(user);
    }

    private byte[] getUserFormAsByteArray(UserForm form) throws Exception {
        return new ObjectMapper().writeValueAsBytes(form);
    }
}
