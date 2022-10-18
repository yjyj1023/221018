package org.example;

import domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    @Test
    void addAndSelect() throws SQLException, ClassNotFoundException {
        AWSUserDaoImpl userDao = new AWSUserDaoImpl();
        User user = new User("8", "EternityHwan","1123");
        userDao.add(user);

        User selectedUser = userDao.get("8");
        Assertions.assertEquals("EternityHwan", selectedUser.getName());
    }
}