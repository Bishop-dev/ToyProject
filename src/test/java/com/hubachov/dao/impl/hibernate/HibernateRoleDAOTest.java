package com.hubachov.dao.impl.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
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
import com.hubachov.service.RoleService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test_config.xml")
@TestExecutionListeners({ DbUnitTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
public class HibernateRoleDAOTest {
    private static final String FILE_DATASET = "/dataset.xml";
    @Autowired
    private RoleService roleService;
    private Role adminRole = new Role(1, "admin");
    private Role userRole = new Role(2, "user");
    private Role otherRole = new Role(3, "other");

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testCreateNullRole() {
        try {
            roleService.create(null);
            fail("NPE must be thrown");
        } catch (NullPointerException e) {
        } catch (Exception e) {
            fail("Must be NPE");
        }
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testAddNotNullRole() {
        try {
            roleService.create(otherRole);
        } catch (Exception e) {
            fail("NPE must not be");
        }
        Role check = null;
        try {
            check = roleService.findByName(otherRole.getName());
        } catch (SQLException e) {
            fail("Can't read role from DB");
        }
        assertEquals("Roles must be equals", otherRole, check);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testFindByName() {
        Role role = null;
        try {
            role = roleService.findByName(adminRole.getName());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNotNull("Role admin exists in DB", role);
        try {
            role = roleService.findByName("unknown");
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNull("This role does not exists in DB", role);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testDeleteNull() {
        try {
            roleService.remove(null);
            fail("Exception should be thrown");
        } catch (NullPointerException e) {
        } catch (Exception e) {
            fail("Should be NPE");
        }
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testDeleteReferencedRole() {
        try {
            roleService.remove(userRole);
            fail("This role has references from User table");
        } catch (Exception e) {
        }
        Role role = null;
        try {
            role = roleService.findByName(userRole.getName());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNotNull("Role should exists in DB", role);
        assertEquals("Roles should be equals", userRole, role);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testDeleteNotNull() {
        Role role = null;
        try {
            roleService.findByName(otherRole.getName());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNull("Role should be not exists in DB", role);
        try {
            roleService.create(otherRole);
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        try {
            role = roleService.findByName(otherRole.getName());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNotNull("Role should be not null", role);
        assertEquals("Roles must be equals", role, otherRole);
    }

    @Test
    @DatabaseSetup(FILE_DATASET)
    public void testUpdate() {
        try {
            roleService.create(otherRole);
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        Role before = null;
        try {
            before = roleService.findByName(otherRole.getName());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        otherRole.setName("some_role");
        try {
            roleService.update(otherRole);
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        Role after = null;
        try {
            after = roleService.findByName(otherRole.getName());
        } catch (SQLException e) {
            fail("Exception must not be");
        }
        assertNotSame("Roles must be different after update", before, after);
    }

}
