package dao;

import database.JDBCCredentialsTest;
import database.MigrationsInitializerTest;
import entities.ConsignmentNote;
import entities.Organization;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

class ConsignmentNoteDAOTest {

    private static final @NotNull Connection connection;
    private static final @NotNull ConsignmentNoteDAO consignmentNoteDAO;

    static {
        try {
            connection = JDBCCredentialsTest.getConnection();
            consignmentNoteDAO = new ConsignmentNoteDAO(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void initializeDB(){
        MigrationsInitializerTest.initialize();
    }

    @Test
    void get() {
        final var consignmentNote = new ConsignmentNote(1, LocalDate.of(1900, 1, 1), new Organization(1, "org1", 11));
        Assertions.assertEquals(consignmentNote, consignmentNoteDAO.get(1));
    }

    @Test
    void getAll() {
        final var consignmentNote1 = new ConsignmentNote(1, LocalDate.of(1900, 1, 1), new Organization(1, "org1", 11));
        final var consignmentNote2 = new ConsignmentNote(2, LocalDate.of(1900, 1, 2), new Organization(2, "org2", 22));
        final var consignmentNote3 = new ConsignmentNote(3, LocalDate.of(1900, 1, 3), new Organization(3, "org3", 33));
        final List<ConsignmentNote> list = new LinkedList<>(List.of(consignmentNote1, consignmentNote2, consignmentNote3));
        Assertions.assertEquals(list, consignmentNoteDAO.getAll());
    }

    @Test
    void save() {
        final var consignmentNote = new ConsignmentNote(4, LocalDate.of(1900, 1, 1), new Organization(1, "org1", 11));
        Assertions.assertEquals(1, consignmentNoteDAO.save(consignmentNote));
    }

    @Test
    void update() {
        final var consignmentNoteToUpdate = new ConsignmentNote(1, LocalDate.of(1900, 1, 1), new Organization(1, "org1", 11));
        final var consignmentNoteToInsert = new ConsignmentNote(1, LocalDate.of(1900, 1, 1), new Organization(2, "org2", 22));
        Assertions.assertEquals(1, consignmentNoteDAO.update(consignmentNoteToUpdate, consignmentNoteToInsert));
    }

    @Test
    void delete() {
        final var consignmentNote = new ConsignmentNote(1, LocalDate.of(1900, 1, 1), new Organization(1, "org1", 11));
        Assertions.assertEquals(1, consignmentNoteDAO.delete(consignmentNote));
        Assertions.assertThrows(IllegalStateException.class, () -> consignmentNoteDAO.delete(consignmentNote));
    }
}