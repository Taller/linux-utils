package com.github.taller.lu.impl;


public class KeyDescriptions {
    private String key;
    private String descriptions;

    public KeyDescriptions(String key, String descriptions) {
        this.key = key;
        this.descriptions = descriptions;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyDescriptions that = (KeyDescriptions) o;

        return key.equals(that.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return key + "\t \t" + descriptions;
    }
}
