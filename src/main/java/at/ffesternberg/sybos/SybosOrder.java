package at.ffesternberg.sybos;

/**
 * Order of the returned entities
 */
public enum SybosOrder {
    DESCENDING("desc"), ASCENDING("asc");

    public static final SybosOrder DEFAULT=DESCENDING;

    private final String orderString;

    private SybosOrder(String orderString){
        this.orderString=orderString;
    }

    public String getOrderString() {
        return orderString;
    }
}
