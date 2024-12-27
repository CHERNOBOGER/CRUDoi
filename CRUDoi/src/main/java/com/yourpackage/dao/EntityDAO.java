package com.yourpackage.dao;

import com.yourpackage.model.Entity;

import java.util.List;

public interface EntityDAO {
    void create(Entity entity);
    Entity read(int id);
    List<Entity> readAll();
    void update(Entity entity);
    void delete(int id);
}