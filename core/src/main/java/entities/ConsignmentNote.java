package entities;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

import java.time.LocalDate;

import static generated.Tables.CONSIGNMENT_NOTE;

public record ConsignmentNote(int number, @NotNull LocalDate datetime, @NotNull Organization organization) {
    public static @NotNull ConsignmentNote create(@NotNull Record record){
        return new ConsignmentNote(
                record.getValue(CONSIGNMENT_NOTE.NUMBER),
                record.getValue(CONSIGNMENT_NOTE.DATETIME),
                Organization.create(record)
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsignmentNote that = (ConsignmentNote) o;
        return number == that.number;
    }
    @Override
    public int hashCode() {return number;}
}
