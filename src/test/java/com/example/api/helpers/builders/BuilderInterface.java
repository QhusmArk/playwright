package com.example.api.helpers.builders;

public interface BuilderInterface<T> {

    void build();

    void setProvider(T provider);

    T getProvider();

    String buildJson();
}

