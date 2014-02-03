package com.hubachov.dao.impl.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.hubachov.entity.Role;
import com.hubachov.entity.User;
import com.hubachov.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test_config.xml")
@TestExecutionListeners({DbUnitTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class HibernateUserDAOTest {
    private static final String FILE_DATASET = "/dataset.xml";
    @Autowired
    private UserService userService;
    private Role userRole = new Role(2, "user");
    private User bob = new User(100, "bob", "bob", "bob@mail.com", "Bob",
            "Smith", new Date(), userRole);

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testCreateNullUser() {
        int before = 0;
        try {
            before = userService.findAll().size();
        } catch (SQLException e1) {
            fail("Can't read users from DB");
        }
        try {
            userService.create(null);
            fail("NPE must be thrown");
        } catch (NullPointerException e) {
        } catch (Exception e) {
            fail("Must be NPE");
        }
        int now = 0;
        try {
            now = userService.findAll().size();
        } catch (SQLException e) {
            fail("Can't read users from DB");
        }
        assertEquals("Amount of users must be the same after adding null user",
                before, now);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testAddNotNullUser() {
        int before = 0;
        try {
            before = userService.findAll().size();
        } catch (SQLException e1) {
            fail("Can't read users from DB");
        }
        try {
            userService.create(bob);
        } catch (Exception e) {
            fail("NPE must not be");
        }
        int now = 0;
        try {
            now = userService.findAll().size();
        } catch (SQLException e) {
            fail("Can't read users from DB");
        }
        assertEquals("Amount of users must be incremented after adding user",
                before + 1, now);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testFindByLogin() {
        User user = null;
        try {
            user = userService.findByLogin(bob.getLogin());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNull("User bob does not exists in DB", user);
        try {
            userService.create(bob);
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        try {
            user = userService.findByLogin(bob.getLogin());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertEquals("Users must be equals", bob, user);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testFindByEmail() {
        User user = null;
        try {
            user = userService.findByEmail(bob.getEmail());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNull("User bob does not exists in DB", user);
        try {
            userService.create(bob);
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        try {
            user = userService.findByEmail(bob.getEmail());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertEquals("Users must be equals", bob, user);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testDeleteNotNull() {
        int before = 0, now = 0;
        User check = null;
        try {
            before = userService.findAll().size();
            User userToRemove = userService.findByLogin("user");
            userService.remove(userToRemove);
            now = userService.findAll().size();
            check = userService.findByLogin("user");
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNull("User must be deleted from DB", check);
        assertEquals("Amount of users in DB should be changed", before, now + 1);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testUpdate() {
        int before = 0, now = 0;
        try {
            userService.create(bob);
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        try {
            before = userService.findAll().size();
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        User checkBefore = null;
        try {
            checkBefore = userService.findByLogin(bob.getLogin());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        bob.setLastName("Other");
        try {
            userService.update(bob);
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        User checkAfter = null;
        try {
            checkAfter = userService.findByLogin(bob.getLogin());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        try {
            now = userService.findAll().size();
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNotSame("User must be changed", checkBefore, checkAfter);
        assertEquals("Amount of users in DB should not changed", before, now);
    }

}
