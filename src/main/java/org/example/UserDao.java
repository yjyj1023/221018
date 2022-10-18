package org.example;

import domain.User;

import java.sql.*;
import java.util.Map;

public class UserDao {
    private ConnectionMaker connectionMaker;
    public UserDao(){
        this.connectionMaker = new AWSConnectionMaker();
    }
    public void add(User user) throws SQLException, ClassNotFoundException {

        //db접속
        Connection c = connectionMaker.getConnection();

        //쿼리문 작성(insert)
        PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES(?,?,?)");
        ps.setString(1, user.getID());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        //status 확인하기
//        int status = ps.executeUpdate();
//        System.out.println(status);

        //쿼리문 실행
        ps.executeUpdate();

        //닫기
        ps.close();
        c.close();
        System.out.println("DB연동 성공");
    }

    public User get(String id) throws ClassNotFoundException, SQLException {

        //db접속
        Connection c = connectionMaker.getConnection();

        //쿼리문 작성(select)
        PreparedStatement ps = c.prepareStatement("SELECT id,name,password FROM users WHERE id = ?");
        ps.setString(1, id);

        //executeQuery: resultset객체에 결과집합 담기, 주로 select문에서 실행
        ResultSet rs = ps.executeQuery();

        //select문의 존재여부 확인(다음 행이 존재하면 true 리턴)
        rs.next();
        User user = new User(rs.getString("id"),
                rs.getString("name"), rs.getString("password"));
        rs.close();
        ps.close();
        c.close();
        return user;
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        UserDao userDao = new UserDao();
        userDao.add(new User("7","Ruru","1534qwer"));
//        User user = userDao.get("1");
//        System.out.println(user.getName());

    }
}

