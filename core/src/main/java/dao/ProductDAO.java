package dao;

import entities.Product;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import utils.Pair;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

import static generated.Tables.*;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

public final class ProductDAO implements DAO<Product> {

    private final @NotNull Connection connection;
    private final @NotNull DSLContext context;

    public ProductDAO(@NotNull Connection connection) {
        this.connection = connection;
        this.context = DSL.using(connection, SQLDialect.POSTGRES);
    }

    private static @NotNull IllegalStateException getException(int code){
        return new IllegalStateException("Product with code " + code + " not found");
    }

    public @NotNull Map<LocalDate, Map<Product, Pair<Integer, Integer>>> getProductQuantityAndTotalWithDate(@NotNull LocalDate start, @NotNull LocalDate end){
        final Result<Record> records = context
                .select(PRODUCT.fields())
                .select(sum(CONSIGNMENT_NOTE_ITEM.QUANTITY).as("quantity_sum"))
                .select(sum(CONSIGNMENT_NOTE_ITEM.PRICE).as("price_sum"))
                .select(CONSIGNMENT_NOTE.DATETIME)
                .from(CONSIGNMENT_NOTE_ITEM)
                .join(PRODUCT)
                .on(CONSIGNMENT_NOTE_ITEM.PRODUCT_ID.eq(PRODUCT.CODE))
                .join(CONSIGNMENT_NOTE)
                .on(CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID.eq(CONSIGNMENT_NOTE.NUMBER))
                .where(
                        CONSIGNMENT_NOTE.DATETIME.greaterOrEqual(start)
                        .and(CONSIGNMENT_NOTE.DATETIME.lessOrEqual(end))
                )
                .groupBy(PRODUCT.CODE, CONSIGNMENT_NOTE.DATETIME)
                .orderBy(CONSIGNMENT_NOTE.DATETIME.asc())
                .fetch();
        final var result = new LinkedHashMap<LocalDate, Map<Product, Pair<Integer, Integer>>>();
        LocalDate currentDate = start;
        for(Record record : records){
            LocalDate date = record.getValue(CONSIGNMENT_NOTE.DATETIME);
            if(!date.equals(currentDate)) currentDate = date;
            Map<Product, Pair<Integer, Integer>> productsMap = result.getOrDefault(currentDate, new HashMap<>());
            int quantitySum = ((BigDecimal) record.getValue("quantity_sum")).intValue();
            int priceSum = ((BigDecimal) record.getValue("price_sum")).intValue();
            productsMap.put(Product.create(record), new Pair<>(quantitySum, priceSum));
            result.put(currentDate, productsMap);
        }
        return result;
    }

    public @NotNull Map<Product, Double> getProductQuantityAndTotal(@NotNull LocalDate start, @NotNull LocalDate end){
        final Result<Record> records = context
                .select(PRODUCT.fields())
                .select(count().as("product_count"))
                .select(sum(CONSIGNMENT_NOTE_ITEM.PRICE).as("price_sum"))
                .from(CONSIGNMENT_NOTE_ITEM)
                .join(PRODUCT)
                .on(CONSIGNMENT_NOTE_ITEM.PRODUCT_ID.eq(PRODUCT.CODE))
                .join(CONSIGNMENT_NOTE)
                .on(CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID.eq(CONSIGNMENT_NOTE.NUMBER))
                .where(
                        CONSIGNMENT_NOTE.DATETIME.greaterOrEqual(start)
                        .and(CONSIGNMENT_NOTE.DATETIME.lessOrEqual(end))
                )
                .groupBy(PRODUCT.CODE)
                .fetch();
        final var result = new HashMap<Product, Double>();
        for(Record record : records){
            Product product = Product.create(record);
            int sum = ((BigDecimal) record.getValue("price_sum")).intValue();
            int count = (int) record.getValue("product_count");
            Double average = (sum*1.0)/count;
            result.put(product, average);
        }
        return result;
    }

    @Override
    public @NotNull Product get(int code) {
        final var record = context
                .select()
                .from(PRODUCT)
                .where(PRODUCT.CODE.eq(code))
                .fetchOne();
        if(record == null) throw getException(code);
        return Product.create(record);
    }

    @Override
    public @NotNull List<Product> getAll() {
        final Result<Record> records = context
                .select()
                .from(PRODUCT)
                .fetch();
        final var result = new LinkedList<Product>();
        for (Record record : records) {
            result.add(Product.create(record));
        }
        return result;
    }

    @Override
    public int save(@NotNull Product entity) {
        return context
                .insertInto(
                        PRODUCT,
                        PRODUCT.CODE,
                        PRODUCT.NAME
                )
                .values(
                        entity.code(),
                        entity.name()
                )
                .execute();
    }

    @Override
    public int update(@NotNull Product entityToUpdate, @NotNull Product entityToInsert) {
        return context.update(PRODUCT)
                .set(
                        PRODUCT.NAME,
                        entityToInsert.name()
                )
                .where(PRODUCT.CODE.eq(entityToUpdate.code()))
                .execute();
    }

    @Override
    public int delete(@NotNull Product entity) {
        int code = entity.code();
        int result = context.delete(PRODUCT)
                .where(PRODUCT.CODE.eq(code))
                .execute();
        if(result == 0) throw getException(code);
        return result;
    }

}
