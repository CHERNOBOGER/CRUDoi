package com.yourpackage.dao;

import com.yourpackage.model.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EntityDAOImplTest {

    private EntityDAO entityDAO;

    @BeforeEach
    void setUp() {
        entityDAO = new EntityDAOImpl();
    }

    @Test
    void testCreateAndRead() {
        Entity entity = new Entity();
        entity.setName("Test Entity");
        entity.setDescription("Test Description");

        entityDAO.create(entity);

        Entity retrievedEntity = entityDAO.read(entity.getId());
        assertNotNull(retrievedEntity);
        assertEquals("Test Entity", retrievedEntity.getName());
        assertEquals("Test Description", retrievedEntity.getDescription());
        entityDAO.delete(entity.getId());
    }

    @Test
    void testUpdate() {
        Entity entity = new Entity();
        entity.setName("Old Name");
        entity.setDescription("Old Description");

        entityDAO.create(entity);

        entity.setName("New Name");
        entity.setDescription("New Description");
        entityDAO.update(entity);

        Entity updatedEntity = entityDAO.read(entity.getId());
        assertEquals("New Name", updatedEntity.getName());
        assertEquals("New Description", updatedEntity.getDescription());
        entityDAO.delete(entity.getId());
    }

    @Test
    void testDelete() {
        Entity entity = new Entity();
        entity.setName("Entity to Delete");
        entity.setDescription("Description");

        entityDAO.create(entity);
        entityDAO.delete(entity.getId());

        Entity deletedEntity = entityDAO.read(entity.getId());
        assertNull(deletedEntity);
    }

    @Test
    void testReadAll() {
        List<Entity> before = entityDAO.readAll();
        Entity entity1 = new Entity();
        entity1.setName("Entity 1");
        entity1.setDescription("Description 1");

        Entity entity2 = new Entity();
        entity2.setName("Entity 2");
        entity2.setDescription("Description 2");

        entityDAO.create(entity1);
        entityDAO.create(entity2);

        List<Entity> after = entityDAO.readAll();
        assertEquals(before.size()+2, after.size());
        entityDAO.delete(entity1.getId());
        entityDAO.delete(entity2.getId());
    }
}