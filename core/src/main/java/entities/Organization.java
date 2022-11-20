package entities;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

import static generated.Tables.ORGANIZATION;

public record Organization(int taxpayerIdentificationNumber, @NotNull String name, int paymentAccount) {
    public static @NotNull Organization create(@NotNull Record record){
        return new Organization(
                record.getValue(ORGANIZATION.TAXPAYER_IDENTIFICATION_NUMBER),
                record.getValue(ORGANIZATION.NAME),
                record.getValue(ORGANIZATION.PAYMENT_ACCOUNT)
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return taxpayerIdentificationNumber == that.taxpayerIdentificationNumber;
    }
    @Override
    public int hashCode() {return taxpayerIdentificationNumber;}
}
