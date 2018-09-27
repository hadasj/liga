package cz.i.ping.pong.liga.dao;

import cz.i.ping.pong.liga.entity.Uzivatel;

import java.sql.*;

public class UzivatelDao {
    private static final String INSERT_USER = "insert into hrac(id,name,email) values(?,?,?)";

    private final Connection connection;

    public UzivatelDao(String db, String user, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby:" + db, user, password);
    }

    public int insert(Uzivatel uzivatel) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_USER);
        statement.setLong(1, uzivatel.getId());
        statement.setString(2, uzivatel.getName());
        statement.setString(3, uzivatel.getEmail());
        return statement.executeUpdate();
    }

    public void commit() throws SQLException {
        connection.commit();
    }
}
