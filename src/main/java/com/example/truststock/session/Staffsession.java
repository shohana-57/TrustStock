package com.example.truststock.session;

import com.example.truststock.model.Staff_User;

public class Staffsession {

    private static Staff_User currentUser;

    public static void setUser(Staff_User user) {
        currentUser = user;
    }

    public static Staff_User getUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}

