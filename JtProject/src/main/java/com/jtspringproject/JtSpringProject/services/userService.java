package com.jtspringproject.JtSpringProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.jtspringproject.JtSpringProject.dao.impl.UserDaoImpl;
import com.jtspringproject.JtSpringProject.models.User;

@Service
public class UserService extends AbstractService<User> {

    @Autowired
    private UserDaoImpl userDao;

    @Override
    public User add(User user) {
        try {
            return userDao.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Add user error");
        }
    }

    public User checkLogin(String username, String password) {
        return userDao.getUser(username, password);
    }

    public boolean checkUserExists(String username) {
        return userDao.userExists(username);
    }

    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }
}
