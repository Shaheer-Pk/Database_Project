package com.amusementpark;

import com.amusementpark.model.Admin;

/**
 * SessionManager: simple singleton to carry the authenticated admin
 * across controllers without passing it through constructors.
 *
 * Set on login, clear on logout.
 */
public class SessionManager {

    private static SessionManager instance;
    private Admin currentAdmin;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(Admin admin) {
        this.currentAdmin = admin;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public boolean isLoggedIn() {
        return currentAdmin != null;
    }

    public void logout() {
        this.currentAdmin = null;
    }
}
