package com.ibm.cardinal.util;

public class CardinalException extends RuntimeException {
    public static final int APPLICATION_EXCEPTION = 555;

    public CardinalException(String msg, Throwable t) {
        super(msg, t);
    }

    public CardinalException(String msg) {
        super(msg);
    }
}
