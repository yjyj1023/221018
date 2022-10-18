# 221018-toby-spring

# 토비의 스트링
# 1. UserDao 생성
```java
//User.java
package domain;
public class User {
    private String id;
    private String name;
    private String password;
    public User(String id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
    }
    public String getName(){
        return name;
    }

    public String getID(){
        return id;
    }

    public String getPassword(){
        return password;
    }
}
```
```java
package org.example;
import domain.User;
import java.sql.*;
import java.util.Map;

public class UserDao {
    public void add(User user) throws SQLException, ClassNotFoundException {

        //환경 변수 불러오기
        Map<String, String> env = System.getenv();
	@@ -22,13 +22,13 @@ public void add() throws SQLException, ClassNotFoundException {

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
	@@ -72,8 +72,9 @@ public User get(String id) throws ClassNotFoundException, SQLException {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        UserDao userDao = new UserDao();
        userDao.add(new User("4","Ruru","1534qwer"));
//        User user = userDao.get("1");
//        System.out.println(user.getName());

    }
}
```
- 기존에 만들었던 코드를 다시 불러오면서 add()메소드에 User를 파라미터로 받도록 수정했다.

- 위 코드의 단점: DB의 연결, SQL Query문, 리소스 반환등 add()와 get()메소드에 중복되는 부분이 많기 때문에 하나를 수정하려면 전체코드를 수정해야 한다.

# 2. 테스트코드 생성
```java
//UserDaoTest.java
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
        User user = new User("6", "EternityHwan","1123");
        userDao.add(user);

        User selectedUser = userDao.get("6");
        Assertions.assertEquals("EternityHwan", selectedUser.getName());
    }
}
```
- 그래들에서 add()메소드와 get()메소드를 테스트하는 테스트 코드를 생성한다.

# 3. 추상클래스
![](https://velog.velcdn.com/images/lyj1023/post/4aab108e-3d45-41d1-8d5c-0445e7fe7150/image.png)

```java
//UserDaoAbstract.java
package org.example;

import domain.User;

import java.sql.*;
import java.util.Map;

public abstract class UserDaoAbstract {
    public abstract Connection getConnection() throws SQLException, ClassNotFoundException;

    public void add(User user) throws SQLException, ClassNotFoundException {

        //db접속
        Connection c = getConnection();
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
        Connection c = getConnection();

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
        //UserDaoAbstract userDao = new UserDaoAbstract();
        //userDao.add(new User("7","Ruru","1534qwer"));
//        User user = userDao.get("1");
//        System.out.println(user.getName());

    }
}
```
```java
//AWSUserDaoImpl.java
package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class AWSUserDaoImpl extends UserDaoAbstract{

    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Map<String, String> env = System.getenv();

        //jdbc사용, 드라이버 로드
        Class.forName("com.mysql.cj.jdbc.Driver");

        //db접속
        Connection c = DriverManager.getConnection(env.get("DB_HOST"), env.get("DB_USER"), env.get("DB_PASSWORD"));
        return c;
    }
}
```
```java
//UserDaoTest.java
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
```
- 추상클래스란 Abstract Method를 최소한 1개를 포함 하고 있는 Class이다.
- 추상클래스 자체는 쓸 수 없고 꼭 구현체를 구현 해주어야 한다.
- 위 코드와 같이 공통부분을 남기고 변화가 있는 부분을 abstract method로 만드는 것을 Template Method Pattern이라고 한다.

>- Template Method Pattern?
>	- 상속을 통해 슈퍼클래스의 기능을 확장할때 사용하는 대표적인 방법으로, 변하지 않는 기능을 슈퍼클래스에 만들어두고 자주 변경되는 기능을 서브클래스에서 만든다.

- 기존에 있던 UserDao클래스를 UserDaoAbstract라는 추상클래스로 변경하고 getConnection()메소드를 추상메소드로 변경한다. getConnection()메소드는 AWSUserDaoImpl클래스가 직접구현한다.

- 위 코드의 장점: 새로운 DB를 연결할때 UserDao를 상속을 통해 확장해주기만 하면 된다.
- 위 코드의 단점: 상속을 사용한다, 자바는 다중상속을 허용하지 않기 때문에 만약 이미 UserDao가 상속을 사용하고 있다면 사용하지 못한다.

# 4. 클래스 분리
![](https://velog.velcdn.com/images/lyj1023/post/5011d875-0773-43b5-a714-c53635a56857/image.png)

```java
package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class SimpleConnectionMaker {
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Map<String, String> env = System.getenv();

        //jdbc사용, 드라이버 로드
        Class.forName("com.mysql.cj.jdbc.Driver");

        //db접속
        Connection c = DriverManager.getConnection(env.get("DB_HOST"), env.get("DB_USER"), env.get("DB_PASSWORD"));
        return c;
    }
}
```
```java
package org.example;
import domain.User;
import java.sql.*;
import java.util.Map;

public class UserDao {
    private SimpleConnectionMaker simpleConnectionMaker;
    public UserDao(){
        simpleConnectionMaker = new SimpleConnectionMaker();
    }
    public void add(User user) throws SQLException, ClassNotFoundException {

        //db접속
        Connection c = simpleConnectionMaker.getConnection();

        //쿼리문 작성(insert)
        PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES(?,?,?)");
	@@ -43,7 +37,7 @@ public void add(User user) throws SQLException, ClassNotFoundException {
    public User get(String id) throws ClassNotFoundException, SQLException {

        //db접속
        Connection c = simpleConnectionMaker.getConnection();

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
```
- 3번에서 사용한 추상화를 제거하고 클래스로 분리한다.
- SimpleConnectionMaker라는 새로운 클래스에 DB연결 메소드를 넣고 UserDao에서 오브젝트를 만들어두고 add()와 get()메소드에서 사용한다.

- 위 코드의 장점: 상속을 사용하지 않는다.
- 위 코드의 단점: 상속을 사용했을 때에 비해 UserDao와 특정 클래스가 강하게 결합되어 있기 때문에 UserDao코드의 수정없이 DB연결 기능을 변경 할수 없다.

# 5. 인터페이스 분리
```java
//AWSConnectionMaker.java
package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class AWSConnectionMaker implements ConnectionMaker{
    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Map<String, String> env = System.getenv();

        //jdbc사용, 드라이버 로드
        Class.forName("com.mysql.cj.jdbc.Driver");
        //db접속
        Connection c = DriverManager.getConnection(env.get("DB_HOST"), env.get("DB_USER"), env.get("DB_PASSWORD"));
        return c;
    }
}
```
```java
//ConnectionMaker.java
package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
    Connection getConnection() throws ClassNotFoundException, SQLException;
}
```
```java
//UserDao.java
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
	@@ -37,7 +37,7 @@ public void add(User user) throws SQLException, ClassNotFoundException {
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
```
```java
//UserDaoTest.java
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
```
- 앞선 문제점들을 해결하기 위해 인터페이스를 도입한다.
- ConnectionMaker라는 인터페이스를 선언하고 AWSConnectionMaker클래스가 인터페이스를 구현한다.

- 위 코드의 단점: 아직 UserDao클래스에 AWSConnectionMaker클래스의 생성자를 호출해서 오브젝트를 생성하는 코드가 남아있기 때문에 여전히 UserDao클래스를 수정해야 한다.
