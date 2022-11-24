package dao;

import entities.Organization;
import entities.Product;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

import static generated.Tables.*;
import static org.jooq.impl.DSL.sum;

public final class OrganizationDAO implements DAO<Organization> {

    private final @NotNull Connection connection;
    private final @NotNull DSLContext context;

    public OrganizationDAO(@NotNull Connection connection) {
        this.connection = connection;
        this.context = DSL.using(connection, SQLDialect.POSTGRES);
    }

    private static @NotNull IllegalStateException getException(int taxpayerIdentificationNumber){
        return new IllegalStateException("Organization with taxpayer_identification_number = " + taxpayerIdentificationNumber + " not found");
    }

    public @NotNull Map<Organization, Integer> getOrganizationsSuppliedProduct(int code, int limit){
        final Product product = getProduct(code);
        final Result<Record> records = context
                .select(ORGANIZATION.fields())
                .select(sum(CONSIGNMENT_NOTE_ITEM.QUANTITY).as("quantity_sum"))
                .from(CONSIGNMENT_NOTE_ITEM)
                .join(CONSIGNMENT_NOTE)
                .on(
                        CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID.eq(CONSIGNMENT_NOTE.NUMBER)
                        .and(CONSIGNMENT_NOTE_ITEM.PRODUCT_ID.eq(code))
                )
                .join(ORGANIZATION)
                .on(CONSIGNMENT_NOTE.ORGANIZATION_ID.eq(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER))
                .groupBy(CONSIGNMENT_NOTE_ITEM.PRODUCT_ID, ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER)
                .orderBy(sum(CONSIGNMENT_NOTE_ITEM.QUANTITY).desc())
                .limit(limit)
                .fetch();
        final var result = new LinkedHashMap<Organization, Integer>();
        for(Record record : records){
            result.put(Organization.create(record), ((BigDecimal) record.getValue("quantity_sum")).intValue());
        }
        return result;
    }

    private @NotNull Product getProduct(int code){
        return new ProductDAO(connection).get(code);
    }

    private @NotNull Map<Product, Integer> getProductsMap(@NotNull Map<Integer, Integer> originalMap){
        var map = new HashMap<Product, Integer>();
        ProductDAO productDAO = new ProductDAO(connection);
        for(Map.Entry<Integer, Integer> entry : originalMap.entrySet()){
            map.put(productDAO.get(entry.getKey()), entry.getValue());
        }
        return map;
    }

    public @NotNull Map<Product, List<Organization>> getOrganizationsSuppliedProductsMoreQuantity(@NotNull Map<Integer, Integer> originalMap){
        final var map = getProductsMap(originalMap);
        Condition conditionHaving = DSL.falseCondition();
        for(Map.Entry<Product, Integer> entry : map.entrySet()){
            conditionHaving = conditionHaving.or(
                    PRODUCT.CODE.eq(entry.getKey().code())
                    .and(
                            sum(CONSIGNMENT_NOTE_ITEM.QUANTITY)
                            .greaterThan(BigDecimal.valueOf(entry.getValue()))
                    )
            );
        }
        final Result<Record> records = context
                .select(PRODUCT.fields())
                .select(ORGANIZATION.fields())
                .select(sum(CONSIGNMENT_NOTE_ITEM.QUANTITY))
                .from(CONSIGNMENT_NOTE_ITEM)
                .join(PRODUCT)
                .on(CONSIGNMENT_NOTE_ITEM.PRODUCT_ID.eq(PRODUCT.CODE))
                .join(CONSIGNMENT_NOTE)
                .on(CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID.eq(CONSIGNMENT_NOTE.NUMBER))
                .join(ORGANIZATION)
                .on(CONSIGNMENT_NOTE.ORGANIZATION_ID.eq(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER))
                .groupBy(PRODUCT.CODE, ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER)
                .having(conditionHaving)
                .orderBy(sum(CONSIGNMENT_NOTE_ITEM.QUANTITY).desc())
                .fetch();
        final var result = new LinkedHashMap<Product, List<Organization>>();
        for(Record record : records){
            Product product = Product.create(record);
            List<Organization> list = result.getOrDefault(product, new LinkedList<>());
            list.add(Organization.create(record));
            result.put(product, list);
        }
        return result;
    }

    public @NotNull Map<Organization, List<Product>> getOrganizationsWithSuppliedProducts(@NotNull LocalDate start, @NotNull LocalDate end){
        final Result<Record> records = context
                .select(PRODUCT.fields())
                .select(ORGANIZATION.fields())
                .from(CONSIGNMENT_NOTE_ITEM)
                .join(PRODUCT)
                .on(CONSIGNMENT_NOTE_ITEM.PRODUCT_ID.eq(PRODUCT.CODE))
                .join(CONSIGNMENT_NOTE)
                .on(
                    CONSIGNMENT_NOTE_ITEM.CONSIGNMENT_NOTE_ID.eq(CONSIGNMENT_NOTE.NUMBER)
                    .and(CONSIGNMENT_NOTE.DATETIME.greaterOrEqual(start))
                    .and(CONSIGNMENT_NOTE.DATETIME.lessOrEqual(end))
                )
                .rightJoin(ORGANIZATION)
                .on(CONSIGNMENT_NOTE.ORGANIZATION_ID.eq(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER))
                .groupBy(PRODUCT.CODE, ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER)
                .fetch();
        final var result = new HashMap<Organization, List<Product>>();
        for(Record record : records){
            Organization organization = Organization.create(record);
            List<Product> list = result.getOrDefault(organization, new LinkedList<>());
            if(record.get(PRODUCT.CODE) != null){
                Product product = Product.create(record);
                list.add(product);
            }
            result.put(organization, list);
        }
        return result;
    }

    @Override
    public @NotNull Organization get(int taxpayerIdentificationNumber) {
        final var record = context
                .select()
                .from(ORGANIZATION)
                .where(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER.eq(taxpayerIdentificationNumber))
                .fetchOne();
        if(record == null) throw getException(taxpayerIdentificationNumber);
        return Organization.create(record);
    }

    @Override
    public @NotNull List<Organization> getAll() {
        final Result<Record> records = context
                .select()
                .from(ORGANIZATION)
                .fetch();
        final var result = new LinkedList<Organization>();
        for (Record record : records) {
            result.add(Organization.create(record));
        }
        return result;
    }

    @Override
    public int save(@NotNull Organization entity) {
        return context
                .insertInto(
                        ORGANIZATION,
                        ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER,
                        ORGANIZATION.NAME,
                        ORGANIZATION.PAYMENT_ACCOUNT
                )
                .values(
                        entity.taxpayerIdentificationNumber(),
                        entity.name(),
                        entity.paymentAccount()
                )
                .execute();
    }

    @Override
    public int update(@NotNull Organization entityToUpdate, @NotNull Organization entityToInsert) {
        return context.update(ORGANIZATION)
                .set(
                        ORGANIZATION.NAME,
                        entityToInsert.name()
                )
                .set(
                        ORGANIZATION.PAYMENT_ACCOUNT,
                        entityToInsert.paymentAccount()
                )
                .where(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER.eq(entityToUpdate.taxpayerIdentificationNumber()))
                .execute();
    }

    @Override
    public int delete(@NotNull Organization entity) {
        int taxpayerIdentificationNumber = entity.taxpayerIdentificationNumber();
        int result = context.delete(ORGANIZATION)
                .where(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER.eq(taxpayerIdentificationNumber))
                .execute();
        if(result == 0) throw getException(taxpayerIdentificationNumber);
        return result;
    }

}
