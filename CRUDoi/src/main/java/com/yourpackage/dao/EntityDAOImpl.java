package com.yourpackage.dao;

import com.yourpackage.model.Entity;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntityDAOImpl implements EntityDAO {
    private Connection connection;

    public EntityDAOImpl() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/database.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void create(Entity entity) {
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Поле 'name' не может быть пустым.");
        }

        String sql = "INSERT INTO entities (name, description, createdAt, updatedAt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getDescription());
            stmt.setString(3, entity.getCreatedAt().toString());
            stmt.setString(4, entity.getUpdatedAt().toString());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Entity read(int id) {
        String sql = "SELECT * FROM entities WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Entity entity = new Entity();
                entity.setId(rs.getInt("id"));
                entity.setName(rs.getString("name"));
                entity.setDescription(rs.getString("description"));
                entity.setUpdatedAt(LocalDateTime.parse(rs.getString("updatedAt")));
                return entity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Entity> readAll() {
        List<Entity> entities = new ArrayList<>();
        String sql = "SELECT * FROM entities";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Entity entity = new Entity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        LocalDateTime.parse(rs.getString("createdAt")),
                        LocalDateTime.parse(rs.getString("updatedAt"))
                );
                entities.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }



    @Override
    public void update(Entity entity) {
        String sql = "UPDATE entities SET name = ?, description = ?, updatedAt = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getDescription());
            stmt.setString(3, entity.getUpdatedAt().toString());
            stmt.setInt(4, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM entities WHERE id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        String updateSeqSql = "UPDATE sqlite_sequence SET seq = (SELECT MAX(id) FROM entities) WHERE name = 'entities'";
        try (Statement updateSeqStmt = connection.createStatement()) {
            updateSeqStmt.executeUpdate(updateSeqSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}