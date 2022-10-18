package org.example;

import domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    @Test
    void addAndSelect() throws SQLException, ClassNotFoundException {
        UserDao userDao = new UserDao();
        User user = new User("10", "EternityHwan","1123");
        userDao.add(user);

        User selectedUser = userDao.get("10");
        Assertions.assertEquals("EternityHwan", selectedUser.getName());
    }
}