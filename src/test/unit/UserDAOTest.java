package test.unit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import dao.UserDAO;
import model.Client;
import model.User;

public class UserDAOTest {

    private UserDAO userDAO;

    @Before
    public void setUp() {
        userDAO = new UserDAO();
    }

    // ─── checkLogin ───────────────────────────────────────────────────────────

    @Test
    public void testCheckLogin_ValidAdmin() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin123");
        boolean result = userDAO.checkLogin(user);
        assertTrue("Admin login should succeed with correct credentials", result);
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    public void testCheckLogin_ValidClient() {
        User user = new User();
        user.setUsername("client01");
        user.setPassword("pass123");
        boolean result = userDAO.checkLogin(user);
        assertTrue("Client login should succeed with correct credentials", result);
        assertEquals("CLIENT", user.getRole());
    }

    @Test
    public void testCheckLogin_WrongPassword() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("wrongpass");
        boolean result = userDAO.checkLogin(user);
        assertFalse("Login should fail with wrong password", result);
    }

    @Test
    public void testCheckLogin_NonExistentUser() {
        User user = new User();
        user.setUsername("ghost_user_xyz");
        user.setPassword("anypass");
        boolean result = userDAO.checkLogin(user);
        assertFalse("Login should fail for non-existent username", result);
    }

    // ─── registerClient ───────────────────────────────────────────────────────

    @Test
    public void testRegisterClient_Success() {
        Client c = new Client(
            "testuser_" + System.currentTimeMillis(),
            "pass123", "Test User",
            "0901234567", "123 Test Street", "test@email.com"
        );
        boolean result = userDAO.registerClient(c);
        assertTrue("Registration should succeed for new username", result);
        assertTrue("New client should get a positive id", c.getId() > 0);
    }

    @Test
    public void testRegisterClient_DuplicateUsername() {
        // First registration
        String username = "dup_" + System.currentTimeMillis();
        Client c1 = new Client(username, "pass1", "User One", "0900000001", "Addr 1", "a@b.com");
        userDAO.registerClient(c1);

        // Attempt duplicate
        Client c2 = new Client(username, "pass2", "User Two", "0900000002", "Addr 2", "c@d.com");
        boolean result = userDAO.registerClient(c2);
        assertFalse("Registration should fail for duplicate username", result);
    }

    // ─── isUsernameTaken ──────────────────────────────────────────────────────

    @Test
    public void testIsUsernameTaken_Existing() {
        assertTrue("'admin' should already exist", userDAO.isUsernameTaken("admin"));
    }

    @Test
    public void testIsUsernameTaken_New() {
        assertFalse("Random new username should not be taken",
            userDAO.isUsernameTaken("newuser_" + System.currentTimeMillis()));
    }

    // ─── changePassword ───────────────────────────────────────────────────────

    @Test
    public void testChangePassword_WrongOldPassword() {
        // Assumes client with id=2 exists
        boolean result = userDAO.changePassword(2, "wrongOldPass", "newPass");
        assertFalse("Change password should fail with wrong old password", result);
    }

    // ─── searchClients ────────────────────────────────────────────────────────

    @Test
    public void testSearchClients_ReturnsResults() {
        // Assumes at least one client with a common name character exists
        var list = userDAO.searchClients("");
        assertNotNull("Search result should not be null", list);
    }
}
