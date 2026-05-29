package com.feupsplaza.chat.server.repository;

import com.feupsplaza.chat.shared.model.User;

import java.sql.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SqlUserRepository implements UserRepository {
    private static final String DB_URL = "jdbc:sqlite:database/database.db";
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public SqlUserRepository() {
        // Automatically create the table if it doesn't exist when the server starts
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL,"
                + "last_token TEXT UNIQUE DEFAULT NULL,"
                + "token_expires_at_ms INTEGER DEFAULT 0"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Could not initialize database: " + e.getMessage());
        }
    }

    public boolean userExists(String username) {
        lock.readLock().lock();
        String query = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if a row is found

        } catch (SQLException e) {
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean storeUser(User user) {
        lock.writeLock().lock();
        String insertSQL = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getHashedPassword());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Will fail if the username already exists due to the UNIQUE constraint
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public User getUserByUsername(String username) {
        lock.readLock().lock();
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                user.setToken(rs.getString("last_token"));
                user.setTokenExpiresAt(rs.getLong("token_expires_at_ms"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Database error fetching user: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    public User getUserByToken(String token) {
        lock.readLock().lock();
        String query = "SELECT * FROM users WHERE last_token = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                user.setToken(rs.getString("last_token"));
                user.setTokenExpiresAt(rs.getLong("token_expires_at_ms"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Database error fetching user: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    public boolean updateUserToken(User user, String token, long expiresAt) {
        lock.writeLock().lock();
        String updateSQL = "UPDATE users SET last_token = ?, token_expires_at_ms = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, token);
            pstmt.setLong(2, expiresAt);
            pstmt.setString(3, user.getUsername());

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}