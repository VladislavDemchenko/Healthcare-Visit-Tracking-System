package org.demchenko.dto;

import jakarta.persistence.Query;

public record QueryParameter<T>(String name, T value) {

    public void applyTo(Query query) {
        query.setParameter(name, value);
    }
}
