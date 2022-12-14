/*
 * This file is generated by jOOQ.
 */
package generated.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ConsignmentNote implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer number;
    private final LocalDate datetime;
    private final Integer organizationId;

    public ConsignmentNote(ConsignmentNote value) {
        this.number = value.number;
        this.datetime = value.datetime;
        this.organizationId = value.organizationId;
    }

    public ConsignmentNote(
        Integer number,
        LocalDate datetime,
        Integer organizationId
    ) {
        this.number = number;
        this.datetime = datetime;
        this.organizationId = organizationId;
    }

    /**
     * Getter for <code>public.consignment_note.number</code>.
     */
    public Integer getNumber() {
        return this.number;
    }

    /**
     * Getter for <code>public.consignment_note.datetime</code>.
     */
    public LocalDate getDatetime() {
        return this.datetime;
    }

    /**
     * Getter for <code>public.consignment_note.organization_id</code>.
     */
    public Integer getOrganizationId() {
        return this.organizationId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ConsignmentNote other = (ConsignmentNote) obj;
        if (this.number == null) {
            if (other.number != null)
                return false;
        }
        else if (!this.number.equals(other.number))
            return false;
        if (this.datetime == null) {
            if (other.datetime != null)
                return false;
        }
        else if (!this.datetime.equals(other.datetime))
            return false;
        if (this.organizationId == null) {
            if (other.organizationId != null)
                return false;
        }
        else if (!this.organizationId.equals(other.organizationId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.number == null) ? 0 : this.number.hashCode());
        result = prime * result + ((this.datetime == null) ? 0 : this.datetime.hashCode());
        result = prime * result + ((this.organizationId == null) ? 0 : this.organizationId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ConsignmentNote (");

        sb.append(number);
        sb.append(", ").append(datetime);
        sb.append(", ").append(organizationId);

        sb.append(")");
        return sb.toString();
    }
}
