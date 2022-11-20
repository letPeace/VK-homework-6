package dao;

import entities.ConsignmentNoteItem;
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
import static generated.Tables.ORGANIZATION;

public final class ConsignmentNoteItemDAO implements DAO<ConsignmentNoteItem> {

    private final @NotNull Connection connection;
    private final @NotNull DSLContext context;

    public ConsignmentNoteItemDAO(@NotNull Connection connection) {
        this.connection = connection;
        this.context = DSL.using(connection, SQLDialect.POSTGRES);
    }

    private static @NotNull IllegalStateException getException(int id){
        return new IllegalStateException("ConsignmentNoteItem with id " + id + " not found");
    }

    @Override
    public @NotNull ConsignmentNoteItem get(int id) {
        final Record record = context
                .select()
                .from(CONSIGNMENT_NOTE_ITEM)
                .join(PRODUCT)
                .on(CONSIGNMENT_NOTE_ITEM.PRODUCT_ID.eq(PRODUCT.CODE))
                .join(CONSIGNMENT_NOTE)
                .on(CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID.eq(CONSIGNMENT_NOTE.NUMBER))
                .join(ORGANIZATION)
                .on(CONSIGNMENT_NOTE.ORGANIZATION_ID.eq(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER))
                .where(CONSIGNMENT_NOTE_ITEM.ID.eq(id))
                .fetchOne();
        if(record == null) throw getException(id);
        return ConsignmentNoteItem.create(record);
    }

    @Override
    public @NotNull List<ConsignmentNoteItem> getAll() {
        final Result<Record> records = context
                .select()
                .from(CONSIGNMENT_NOTE_ITEM)
                .join(PRODUCT)
                .on(CONSIGNMENT_NOTE_ITEM.PRODUCT_ID.eq(PRODUCT.CODE))
                .join(CONSIGNMENT_NOTE)
                .on(CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID.eq(CONSIGNMENT_NOTE.NUMBER))
                .join(ORGANIZATION)
                .on(CONSIGNMENT_NOTE.ORGANIZATION_ID.eq(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER))
                .fetch();
        final var result = new LinkedList<ConsignmentNoteItem>();
        for (Record record : records) {
            result.add(ConsignmentNoteItem.create(record));
        }
        return result;
    }

    @Override
    public int save(@NotNull ConsignmentNoteItem entity) {
        return context
                .insertInto(
                        CONSIGNMENT_NOTE_ITEM,
                        CONSIGNMENT_NOTE_ITEM.ID,
                        CONSIGNMENT_NOTE_ITEM.PRODUCT_ID,
                        CONSIGNMENT_NOTE_ITEM.PRICE,
                        CONSIGNMENT_NOTE_ITEM.QUANTITY,
                        CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID
                )
                .values(
                        entity.id(),
                        entity.product().code(),
                        entity.price(),
                        entity.quantity(),
                        entity.consignmentNote().number()
                )
                .execute();
    }

    @Override
    public int update(@NotNull ConsignmentNoteItem entityToUpdate, @NotNull ConsignmentNoteItem entityToInsert) {
        return context.update(CONSIGNMENT_NOTE_ITEM)
                .set(
                        CONSIGNMENT_NOTE_ITEM.PRODUCT_ID,
                        entityToInsert.product().code()
                )
                .set(
                        CONSIGNMENT_NOTE_ITEM.PRICE,
                        entityToInsert.price()
                )
                .set(
                        CONSIGNMENT_NOTE_ITEM.QUANTITY,
                        entityToInsert.quantity()
                )
                .set(
                        CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID,
                        entityToInsert.consignmentNote().number()
                )
                .where(CONSIGNMENT_NOTE_ITEM.ID.eq(entityToUpdate.id()))
                .execute();
    }

    @Override
    public int delete(@NotNull ConsignmentNoteItem entity) {
        int id = entity.id();
        int result = context.delete(CONSIGNMENT_NOTE_ITEM)
                .where(CONSIGNMENT_NOTE_ITEM.ID.eq(id))
                .execute();
        if(result == 0) throw getException(id);
        return result;
    }

}
