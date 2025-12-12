package com.example.truststock.db;

import java.sql.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class Database {

    private static final String URL = "jdbc:sqlite:truststock.db";

    // initialize DB & tables
    static {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {

            // users table (store salted hash)
            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    salt TEXT NOT NULL,
                    role TEXT NOT NULL
                )
            """);

            // products table (minimal)
            st.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price REAL,
                    stock INTEGER,
                    min_stock INTEGER DEFAULT 0,
                    quality_status TEXT DEFAULT 'GOOD'
                )
            """);

            // create a default admin if not exists
            if (!userExists(conn, "admin")) {
                addUser(conn, "admin", "admin123", "ADMIN");
            }
            if (!userExists(conn, "qc")) {
                addUser(conn, "qc", "qc123", "QC");
            }
            if (!userExists(conn, "customer")) {
                addUser(conn, "customer", "cust123", "CUSTOMER");
            }

            System.out.println("Database initialized.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ---- user utilities ----
    private static boolean userExists(Connection conn, String username) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void addUser(Connection conn, String username, String plainPassword, String role) throws Exception {
        byte[] salt = PasswordUtil.generateSalt();
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hash = PasswordUtil.hashPassword(plainPassword, salt);

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users(username, password_hash, salt, role) VALUES(?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, saltB64);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    // Public method to add user (callable from admin UI later)
    public static void addUser(String username, String plainPassword, String role) throws Exception {
        try (Connection conn = getConnection()) {
            addUser(conn, username, plainPassword, role);
        }
    }

    // authenticate and return role (or null)
    public static String authenticate(String username, String plainPassword) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT password_hash, salt, role FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String storedHash = rs.getString("password_hash");
                String saltB64 = rs.getString("salt");
                String role = rs.getString("role");

                byte[] salt = Base64.getDecoder().decode(saltB64);
                String computed = PasswordUtil.hashPassword(plainPassword, salt);
                if (MessageDigest.isEqual(storedHash.getBytes(), computed.getBytes())) {
                    return role;
                } else return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
