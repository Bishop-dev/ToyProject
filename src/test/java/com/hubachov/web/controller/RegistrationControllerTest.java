package com.hubachov.web.controller;

import com.hubachov.entity.Role;
import com.hubachov.entity.User;
import com.hubachov.service.RoleService;
import com.hubachov.service.UserService;
import com.hubachov.web.form.UserForm;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegistrationControllerTest {
    private static final String ATTRIBUTE_NAME__MESSAGE = "message";
    private static final String ATTRIBUTE_NAME__USERFORM = "UserForm";
    private static final String ATTRIBUTE_VALUE = "Wrong login/password";


    private static final String PATH__PREFIX = "/WEB-INF/pages/";
    private static final String PATH__SUFFIX = ".jsp";

    private static final String PATH_REQUEST__ROOT = "/";
    private static final String PATH_REQUEST__INDEX = "/index";
    private static final String PATH_REQUEST__CABINET = "/cabinet";
    private static final String PATH_REQUEST__CHECK = "/check";
    private static final String PATH__REQUEST_LOGIN_PAGE = "/login";
    private static final String PATH__REQUEST_LOGIN_FAIL = "/login-fail";
    private static final String PATH__REQUEST_LOGOUT = "/logout";
    private static final String PATH__REQUEST_REGISTRATION = "/registration";
    private static final String PATH__REQUEST_REGISTRATE = "/registrate";

    private static final String PATH__LOGIN = "login";
    private static final String PATH__ADMIN_HOME = "admin/cabinet";
    private static final String PATH__USER_HOME = "user/index";
    private static final String PATH__REDIRECT = "redirect:";
    private static final String PATH__REGISTRATION = "registration";
    private static final List<String> INDEX_LIST = Arrays.asList(PATH_REQUEST__INDEX, PATH_REQUEST__CABINET);
    @Mock
    private UserService userService;
    @Mock
    private RoleService roleService;
    @Mock
    private ReCaptcha reCaptcha;
    @InjectMocks
    private SpringController controller;
    private MockMvc mockMvc;
    private User admin;
    private User user;

    @Before
    public void setup() throws Exception {
        Date now = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/2000");
        admin = new User.UserBuilder().id(1L).login("admin").password("admin").email("admin@mail.com").
                firstName("Admin").lastName("Adminovich").birthday(now).role(new Role.RoleBuilder().
                id(1L).name("admin").build()).build();
        user = new User.UserBuilder().id(2L).login("user").password("user").email("user@mail.com").firstName("User").
                lastName("Userovich").birthday(now).role(new Role.RoleBuilder().id(2L).name("user").build()).build();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver()).build();
    }

    @Test
    public void testGetLoginPage() throws Exception {
        List<String> list = Arrays.asList(PATH_REQUEST__ROOT, PATH__REQUEST_LOGIN_PAGE);
        for (String path : list) {
            ResultActions actions = mockMvc.perform(get(path));
            actions.andExpect(status().isOk());
            actions.andExpect(view().name(PATH__LOGIN));
            actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__LOGIN + PATH__SUFFIX));
        }
    }

    @Test
    public void testGetLoginPageAfterLoginFail() throws Exception {
        ResultActions actions = mockMvc.perform(get(PATH__REQUEST_LOGIN_FAIL));
        actions.andExpect(status().isOk());
        actions.andExpect(view().name(PATH__LOGIN));
        actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__LOGIN + PATH__SUFFIX));
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__MESSAGE, equalTo(ATTRIBUTE_VALUE)));
    }

    @Test
    public void testLogout() throws Exception {
        ResultActions actions = mockMvc.perform(get(PATH__REQUEST_LOGOUT));
        actions.andExpect(status().isMovedTemporarily());
        actions.andExpect(view().name(PATH__REDIRECT + PATH__LOGIN));
        actions.andExpect(redirectedUrl(PATH__LOGIN));
        actions.andExpect(forwardedUrl(null));
        MvcResult result = actions.andReturn();
        assertNull("Session should be invalidated", result.getRequest().getSession().getAttribute("user"));
    }

    @Test
    public void testGetRegistrationPage() throws Exception {
        ResultActions actions = mockMvc.perform(get(PATH__REQUEST_REGISTRATION));
        actions.andExpect(status().isOk());
        actions.andExpect(view().name(PATH__REGISTRATION));
        actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__REGISTRATION + PATH__SUFFIX));
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__USERFORM, instanceOf(UserForm.class)));
    }

    @Test
    public void testCheckExistingLogin() throws Exception {
        String rightLogin = "admin";
        when(userService.findByLogin(rightLogin)).thenReturn(new User());
        ResultActions actions = mockMvc.perform(get(PATH_REQUEST__CHECK).param("login", rightLogin));
        actions.andExpect(status().isOk()).andReturn();
        String answer = actions.andReturn().getResponse().getContentAsString();
        assertThat("This login must be busy", "false", is(answer));
    }

    @Test
    public void testCheckNotExistingLogin() throws Exception {
        String rightLogin = "unknown";
        when(userService.findByLogin(rightLogin)).thenReturn(null);
        ResultActions actions = mockMvc.perform(get(PATH_REQUEST__CHECK).param("login", rightLogin));
        actions.andExpect(status().isOk()).andReturn();
        String answer = actions.andReturn().getResponse().getContentAsString();
        assertThat("This login must be busy", "true", is(answer));
    }

    @Test
    public void testCheckLoginWith500Error() throws Exception {
        String login = "login";
        when(userService.findByLogin(anyString())).thenThrow(new SQLException());
        ResultActions actions = mockMvc.perform(get(PATH_REQUEST__CHECK).param("login", login));
        actions.andExpect(status().isInternalServerError());
        String answer = actions.andReturn().getResponse().getContentAsString();
        assertTrue("Service should not return anything", answer.isEmpty());
    }

    @Test
    public void testRegistration() throws Exception {
        ReCaptchaResponse reCaptchaResponse = prepareCaptcha();
        when(userService.findByLogin(admin.getLogin())).thenReturn(null);
        when(roleService.findByName(admin.getRole().getName())).thenReturn(admin.getRole());
        doNothing().when(userService).create(any(User.class));
        ResultActions actions = callRegistration(new UserForm(admin));
        actions.andExpect(status().isMovedTemporarily());
        actions.andExpect(model().hasNoErrors());
        actions.andExpect(flash().attribute(ATTRIBUTE_NAME__MESSAGE, "You may enter using your login and password."));
        actions.andExpect(view().name(PATH__REDIRECT + PATH__LOGIN));
        actions.andExpect(redirectedUrl(PATH__LOGIN));
        actions.andExpect(forwardedUrl(null));
        MvcResult result = actions.andReturn();
        String content = result.getResponse().getContentAsString();
        assertTrue("Service should not return anything", content.isEmpty());
        InOrder order = inOrder(reCaptchaResponse, reCaptcha, userService, roleService);
        order.verify(reCaptcha).checkAnswer(anyString(), anyString(), anyString());
        order.verify(reCaptchaResponse).isValid();
        order.verify(userService).findByLogin(admin.getLogin());
        order.verify(roleService).findByName(admin.getRole().getName());
        order.verify(userService).create(admin);
    }

    @Test
    public void testRegistrationCaptchaNotValid() throws Exception {
        ReCaptchaResponse reCaptchaResponse = mock(ReCaptchaResponse.class);
        when(reCaptchaResponse.isValid()).thenReturn(false);
        when(reCaptcha.checkAnswer(anyString(), anyString(), anyString())).thenReturn(reCaptchaResponse);
        UserForm form = new UserForm(admin);
        form.setId(0);
        ResultActions actions = callRegistration(form);
        actions.andExpect(status().isOk());
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__USERFORM, form));
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__MESSAGE, "Wrong captcha"));
        actions.andExpect(view().name(PATH__REGISTRATION));
        actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__REGISTRATION + PATH__SUFFIX));
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testRegistrationValidationFail() throws Exception {
        prepareCaptcha();
        UserForm form = new UserForm(admin);
        form.setId(0);
        form.setEmail("");
        ResultActions actions = callRegistration(form);
        actions.andExpect(status().isOk());
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__USERFORM, form));
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__MESSAGE, "Wrong data"));
        actions.andExpect(model().hasErrors());
        actions.andExpect(view().name(PATH__REGISTRATION));
        actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__REGISTRATION + PATH__SUFFIX));
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testRegistrationWithCheckLoginError() throws Exception {
        prepareCaptcha();
        doThrow(new SQLException()).when(userService).findByLogin(anyString());
        UserForm form = new UserForm(admin);
        form.setId(0);
        ResultActions actions = callRegistration(form);
        actions.andExpect(status().isInternalServerError());
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__USERFORM, form));
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__MESSAGE, "Internal error. Try again."));
        actions.andExpect(view().name(PATH__REGISTRATION));
        assertTrue("Service should not return anything", actions.andReturn().getResponse().getContentAsString().isEmpty());
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testRegistrationWithCreateError() throws Exception {
        prepareCaptcha();
        doThrow(new SQLException()).when(userService).create(any(User.class));
        when(userService.findByLogin(anyString())).thenReturn(null);
        UserForm form = new UserForm(admin);
        form.setId(0);
        ResultActions actions = callRegistration(form);
        actions.andExpect(status().isInternalServerError());
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__USERFORM, form));
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__MESSAGE, "Can't save new user. Try again."));
        actions.andExpect(view().name(PATH__REGISTRATION));
    }

    @Test
    public void testRegistrationLoginBusy() throws Exception {
        prepareCaptcha();
        when(userService.findByLogin(admin.getLogin())).thenReturn(admin);
        UserForm form = new UserForm(admin);
        form.setId(0);
        ResultActions actions = callRegistration(form);
        actions.andExpect(status().isOk());
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__USERFORM, form));
        actions.andExpect(model().attribute(ATTRIBUTE_NAME__MESSAGE, "Login busy. Try another one."));
        actions.andExpect(view().name(PATH__REGISTRATION));
        actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__REGISTRATION + PATH__SUFFIX));
        assertTrue("Service should not return anything", actions.andReturn().getResponse().getContentAsString().isEmpty());
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testGetIndexPageByAdmin() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(admin.getLogin());
        when(userService.findByLogin(admin.getLogin())).thenReturn(admin);
        for (String path : INDEX_LIST) {
            ResultActions actions = mockMvc.perform(get(path).principal(principal).with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.addUserRole(admin.getRole().getName());
                    return request;
                }
            }));
            actions.andExpect(status().isOk());
            actions.andExpect(view().name(PATH__ADMIN_HOME));
            actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__ADMIN_HOME + PATH__SUFFIX));
            MvcResult result = actions.andReturn();
            HttpSession session = result.getRequest().getSession();
            assertThat("Session should contain admin", (User) session.getAttribute("user"), is(admin));
        }
    }

    @Test
    public void testGetIndexPageByUser() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getLogin());
        when(userService.findByLogin(user.getLogin())).thenReturn(user);
        for (String path : INDEX_LIST) {
            ResultActions actions = mockMvc.perform(get(path).principal(principal).with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.addUserRole(user.getRole().getName());
                    return request;
                }
            }));
            actions.andExpect(status().isOk());
            actions.andExpect(view().name(PATH__USER_HOME));
            actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__USER_HOME + PATH__SUFFIX));
            MvcResult result = actions.andReturn();
            HttpSession session = result.getRequest().getSession();
            assertThat("Session should contain user", (User) session.getAttribute("user"), is(user));
        }
    }

    @Test
    public void testGetIndexPageByAnonymous() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(null);
        for (String path : INDEX_LIST) {
            ResultActions actions = mockMvc.perform(get(path).principal(principal));
            actions.andExpect(status().isOk());
            actions.andExpect(view().name(PATH__LOGIN));
            actions.andExpect(forwardedUrl(PATH__PREFIX + PATH__LOGIN + PATH__SUFFIX));
            MvcResult result = actions.andReturn();
            HttpSession session = result.getRequest().getSession();
            assertThat("Session should not contain any users", session.getAttribute("user"), is(nullValue()));
            verify(userService, never()).findByLogin(anyString());
        }
    }

    @Test
    public void testGetIndexPageWith500Error() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(admin.getLogin());
        when(userService.findByLogin(anyString())).thenThrow(new SQLException());
        ResultActions actions = mockMvc.perform(get(PATH_REQUEST__INDEX).principal(principal));
        actions.andExpect(status().isInternalServerError());
        actions.andExpect(view().name(PATH__LOGIN));
        MvcResult result = actions.andReturn();
        HttpSession session = result.getRequest().getSession();
        assertThat("Session should not contain any users", session.getAttribute("user"), is(nullValue()));
    }

    private static ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(PATH__PREFIX);
        viewResolver.setSuffix(PATH__SUFFIX);
        return viewResolver;
    }

    private ResultActions callRegistration(UserForm form) throws Exception {
        return mockMvc.perform(post(PATH__REQUEST_REGISTRATE).param("recaptcha_challenge_field", "ololo").
                param("recaptcha_response_field", "ololo").param("login", form.getLogin()).
                param("password", form.getPassword()).param("confirm", form.getConfirm()).param("email", form.getEmail()).
                param("firstName", form.getFirstName()).param("lastName", form.getLastName()).
                param("birthday", form.getBirthday()).param("role", form.getRole()));
    }

    private ReCaptchaResponse prepareCaptcha() {
        ReCaptchaResponse reCaptchaResponse = mock(ReCaptchaResponse.class);
        when(reCaptchaResponse.isValid()).thenReturn(true);
        when(reCaptcha.checkAnswer(anyString(), anyString(), anyString())).thenReturn(reCaptchaResponse);
        return reCaptchaResponse;
    }
}
