package dao;

import entities.ConsignmentNote;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import static generated.Tables.*;

public final class ConsignmentNoteDAO implements DAO<ConsignmentNote> {

    private final @NotNull Connection connection;
    private final @NotNull DSLContext context;

    public ConsignmentNoteDAO(@NotNull Connection connection) {
        this.connection = connection;
        this.context = DSL.using(connection, SQLDialect.POSTGRES);
    }

    private static @NotNull IllegalStateException getException(int number){
        return new IllegalStateException("ConsignmentNote with number " + number + " not found");
    }

    @Override
    public @NotNull ConsignmentNote get(int number) {
        final var record = context
                .select()
                .from(CONSIGNMENT_NOTE)
                .join(ORGANIZATION)
                .on(CONSIGNMENT_NOTE.ORGANIZATION_ID.eq(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER))
                .where(CONSIGNMENT_NOTE.ORGANIZATION_ID.eq(number))
                .fetchOne();
        if(record == null) throw getException(number);
        return ConsignmentNote.create(record);
    }

    @Override
    public @NotNull List<ConsignmentNote> getAll() {
        final Result<Record> records = context
                .select()
                .from(CONSIGNMENT_NOTE)
                .join(ORGANIZATION)
                .on(CONSIGNMENT_NOTE.ORGANIZATION_ID.eq(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER))
                .fetch();
        final var result = new LinkedList<ConsignmentNote>();
        for (Record record : records) {
            result.add(ConsignmentNote.create(record));
        }
        return result;
    }

    @Override
    public int save(@NotNull ConsignmentNote entity) {
        return context
                .insertInto(
                        CONSIGNMENT_NOTE,
                        CONSIGNMENT_NOTE.NUMBER,
                        CONSIGNMENT_NOTE.DATETIME,
                        CONSIGNMENT_NOTE.ORGANIZATION_ID
                )
                .values(
                        entity.number(),
                        entity.datetime(),
                        entity.organization().taxpayerIdentificationNumber()
                )
                .execute();
    }

    @Override
    public int update(@NotNull ConsignmentNote entityToUpdate, @NotNull ConsignmentNote entityToInsert) {
        return context.update(CONSIGNMENT_NOTE)
                .set(
                        CONSIGNMENT_NOTE.DATETIME,
                        entityToInsert.datetime()
                )
                .set(
                        CONSIGNMENT_NOTE.ORGANIZATION_ID,
                        entityToInsert.organization().taxpayerIdentificationNumber()
                )
                .where(CONSIGNMENT_NOTE.NUMBER.eq(entityToUpdate.number()))
                .execute();
    }

    @Override
    public int delete(@NotNull ConsignmentNote entity) {
        int number = entity.number();
        int result = context.delete(CONSIGNMENT_NOTE)
                .where(CONSIGNMENT_NOTE.NUMBER.eq(number))
                .execute();
        if(result == 0) throw getException(number);
        return result;
    }

}
