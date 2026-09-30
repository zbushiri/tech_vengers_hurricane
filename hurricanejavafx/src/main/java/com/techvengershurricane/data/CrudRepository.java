package com.techvengershurricane.data;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    /* JSON: reads every saved item. */
    List<T> getAll();

    /* JSON: finds one item by ID. */
    Optional<T> findById(String id);

    /* JSON: adds and saves one item. */
    void add(T item);

    /* JSON: replaces and saves one item. */
    boolean edit(T item);

    /* JSON: deletes and saves one item. */
    boolean delete(String id);
}
