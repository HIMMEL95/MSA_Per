package com.ibm.cardinal.util;

public class CardinalString {
    private String string;

    public CardinalString(String str) {
        this.string = str;
    }

    public String getString() {
        return this.string;
    }

    public void setString(String str) {
        this.string = str;
    }

    public int length() {
        return this.string.length();
    }

    public boolean equals(Object obj) {
        return this.string.equals(obj);
    }

    public String toString() {
        return this.string.toString();
    }

    @Override
    public int hashCode() {
        return this.string.hashCode();
    }
}
