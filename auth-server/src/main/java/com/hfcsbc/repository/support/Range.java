
package com.hfcsbc.repository.support;

import java.io.Serializable;


public class Range<E> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String field;
    private Comparable from;
    private Comparable to;
    private Boolean includeNull;


    public Range(String field) {
        this.field = field;
    }


    public Range(String field, Comparable from, Comparable to) {
        this.field = field;
        this.from = from;
        this.to = to;
    }

    public Range(String field, Comparable from, Comparable to, Boolean includeNull) {
        this.field = field;
        this.from = from;
        this.to = to;
        this.includeNull = includeNull;
    }


    public Range(Range<E> other) {
        this.field = other.getField();
        this.from = other.getFrom();
        this.to = other.getTo();
        this.includeNull = other.getIncludeNull();
    }

    public String getField() {
        return field;
    }

    public Comparable getFrom() {
        return from;
    }


    public void setFrom(Comparable from) {
        this.from = from;
    }

    public boolean isFromSet() {
        return getFrom() != null;
    }


    public Comparable getTo() {
        return to;
    }

    public void setTo(Comparable to) {
        this.to = to;
    }

    public boolean isToSet() {
        return getTo() != null;
    }

    public void setIncludeNull(boolean includeNull) {
        this.includeNull = includeNull;
    }

    public Boolean getIncludeNull() {
        return includeNull;
    }

    public boolean isIncludeNullSet() {
        return includeNull != null;
    }

    public boolean isBetween() {
        return isFromSet() && isToSet();
    }

    public boolean isSet() {
        return isFromSet() || isToSet() || isIncludeNullSet();
    }

    public boolean isValid() {
        if (isBetween()) {
            return getFrom().compareTo(getTo()) <= 0;
        }

        return true;
    }
}