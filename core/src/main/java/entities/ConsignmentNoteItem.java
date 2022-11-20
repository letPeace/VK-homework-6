package entities;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

import static generated.Tables.CONSIGNMENT_NOTE_ITEM;

public record ConsignmentNoteItem(int id, @NotNull Product product, int price, int quantity, @NotNull ConsignmentNote consignmentNote) {
    public static @NotNull ConsignmentNoteItem create(@NotNull Record record){
        return new ConsignmentNoteItem(
                record.getValue(CONSIGNMENT_NOTE_ITEM.ID),
                Product.create(record),
                record.getValue(CONSIGNMENT_NOTE_ITEM.PRICE),
                record.getValue(CONSIGNMENT_NOTE_ITEM.QUANTITY),
                ConsignmentNote.create(record)
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsignmentNoteItem that = (ConsignmentNoteItem) o;
        return id == that.id;
    }
    @Override
    public int hashCode() {return id;}
}
