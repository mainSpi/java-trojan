package greek.horse.models;

import java.io.Serializable;
import java.util.Objects;

public class FunctionTicket implements Serializable {
    private final RequestFunctionType requestFunctionType;
    private final String id;
    private final boolean fixed;

    public FunctionTicket(RequestFunctionType requestFunctionType, String id, boolean fixed) {
        this.requestFunctionType = requestFunctionType;
        this.id = id;
        this.fixed = fixed;
    }

    @Override
    public String toString() {
        return "FunctionTicket{" +
                "function=" + requestFunctionType +
                ", id='" + id + '\'' +
                ", fixed=" + fixed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionTicket that = (FunctionTicket) o;
        return requestFunctionType == that.requestFunctionType && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestFunctionType, id);
    }

    public String getId() {
        return id;
    }

    public RequestFunctionType getFunction() {
        return requestFunctionType;
    }

    public boolean isFixed() {
        return fixed;
    }
}
