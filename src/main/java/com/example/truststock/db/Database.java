package com.example.truststock.db;

import com.example.truststock.model.Staff_User;

import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;
public class Database {

    private static final String URL = "jdbc:sqlite:truststock.db";

    static {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {


            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                     full_name TEXT NOT NULL,
                    password_hash TEXT NOT NULL,
                    salt TEXT NOT NULL,
                    role TEXT NOT NULL
                )
            """);


            st.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price REAL,
                    stock INTEGER,
                    min_stock INTEGER DEFAULT 0,
                    quality_status TEXT DEFAULT 'GOOD',
                    image_path TEXT DEFAULT ' '
                )
            """);

            st.execute("""
    CREATE TABLE IF NOT EXISTS sales (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        product_id INTEGER,
        quantity INTEGER,
        sale_date TEXT,
        FOREIGN KEY(product_id) REFERENCES products(id)
    )
         """);

            st.execute("""
    CREATE TABLE IF NOT EXISTS restocks (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        product_id INTEGER,
        quantity INTEGER,
        restock_date TEXT,
        FOREIGN KEY(product_id) REFERENCES products(id)
    )
        """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        product_id INTEGER,
                        qty INTEGER,
                        phone TEXT,
                        address TEXT
                    )
                    
            """);

            st.execute("""
                   CREATE TABLE IF NOT EXISTS order_items (
                               id INTEGER PRIMARY KEY AUTOINCREMENT,
                               product_id INTEGER NOT NULL,
                               qty INTEGER NOT NULL,
                               phone TEXT NOT NULL,
                               address TEXT NOT NULL,
                               delivered INTEGER DEFAULT 0,
                               FOREIGN KEY (product_id) REFERENCES products(id)
                           )
                    
                    
            """);




            if (!userExists(conn, "admin")) {
                addUser(conn, "admin", "System Admin", "admin123", "ADMIN");
            }
            if (!userExists(conn, "qc")) {
                addUser(conn, "qc", "Quality Checker", "qc123", "QC");
            }
            if (!userExists(conn, "customer")) {
                addUser(conn, "customer", "General Customer", "cust123", "CUSTOMER");
            }

            if (!userExists(conn, "deliver")) {
                addUser(conn, "deliver", "Delivery Man", "del123", "DELIVER");
            }

            st.execute("ALTER TABLE products ADD COLUMN image_path TEXT;");
        } catch (SQLException e) {

            if (!e.getMessage().contains("duplicate column name")) {
                e.printStackTrace();
            }


            System.out.println("Database initialized.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }


    private static boolean userExists(Connection conn, String username) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void addUser(Connection conn, String username, String fullName, String plainPassword, String role) throws Exception {
        byte[] salt = PasswordUtil.generateSalt();
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hash = PasswordUtil.hashPassword(plainPassword, salt);

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users(username, full_name, password_hash, salt, role) VALUES(?,?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, hash);
            ps.setString(4, saltB64);
            ps.setString(5, role);
            ps.executeUpdate();
        }

    }



    public static Staff_User authenticateUser(String username, String plainPassword) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, username, full_name, password_hash, salt, role FROM users WHERE username = ?")) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String storedHash = rs.getString("password_hash");
                byte[] salt = Base64.getDecoder().decode(rs.getString("salt"));
                String computed = PasswordUtil.hashPassword(plainPassword, salt);

                if (MessageDigest.isEqual(storedHash.getBytes(), computed.getBytes())) {
                    return new Staff_User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
