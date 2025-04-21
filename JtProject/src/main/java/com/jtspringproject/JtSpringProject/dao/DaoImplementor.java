package com.jtspringproject.JtSpringProject.dao;

import java.util.List;

public interface DaoImplementor<T> {

    List<T> findAll();

    T save(T entity);

    T findById(int id);

    T update(T entity);

    boolean delete(int id);
}
