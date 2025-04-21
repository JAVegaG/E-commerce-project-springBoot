package com.jtspringproject.JtSpringProject.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jtspringproject.JtSpringProject.dao.DaoImplementor;

public abstract class AbstractService<T> implements ServiceAbstraction<T> {

    @Autowired
    protected DaoImplementor<T> implementor;

    @Override
    public List<T> getAll() {
        return implementor.findAll();
    }

    @Override
    public T add(T entity) {
        return implementor.save(entity);
    }

    @Override
    public T getById(int id) {
        return implementor.findById(id);
    }

    @Override
    public T update(T entity) {
        return implementor.update(entity);
    }

    @Override
    public boolean delete(int id) {
        return implementor.delete(id);
    }
}
