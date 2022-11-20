package entities;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

import static generated.Tables.PRODUCT;

public record Product(int code, @NotNull String name) {
    public static @NotNull Product create(@NotNull Record record){
        return new Product(
                record.getValue(PRODUCT.CODE),
                record.getValue(PRODUCT.NAME)
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return code == product.code;
    }
    @Override
    public int hashCode() {return code;}
}
