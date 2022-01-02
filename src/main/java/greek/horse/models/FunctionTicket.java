package greek.horse.models;

import java.io.Serializable;
import java.util.Objects;

public class FunctionTicket implements Serializable {
    private final RequestFunction requestFunction;
    private final String id;
    private final boolean fixed;

    public FunctionTicket(RequestFunction requestFunction, String id, boolean fixed) {
        this.requestFunction = requestFunction;
        this.id = id;
        this.fixed = fixed;
    }

    @Override
    public String toString() {
        return "FunctionTicket{" +
                "function=" + requestFunction +
                ", id='" + id + '\'' +
                ", fixed=" + fixed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionTicket that = (FunctionTicket) o;
        return requestFunction == that.requestFunction && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestFunction, id);
    }

    public String getId() {
        return id;
    }

    public RequestFunction getFunction() {
        return requestFunction;
    }

    public boolean isFixed() {
        return fixed;
    }
}
