package com.jtspringproject.JtSpringProject.services;

import java.util.List;

public interface ServiceAbstraction<T> {

    List<T> getAll();

    T add(T entity);

    T getById(int id);

    T update(T entity);

    boolean delete(int id);
}
