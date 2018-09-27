package cz.i.ping.pong.liga.dao;

import cz.i.ping.pong.liga.entity.Kolo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KoloDao {
    private static final String INSERT_KOLO = "insert into hrac(id, od, do) values(?,?,?)";
    private static final String LIST_KOLO = "select id, od, do from kolo order by id";

    private final Connection connection;

    public KoloDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Kolo kolo) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_KOLO);
        statement.setLong(1, kolo.getId());
        statement.setDate(2, Date.valueOf(kolo.getStart()));
        statement.setDate(3, Date.valueOf(kolo.getKonec()));
        statement.executeUpdate();
        statement.close();
    }

    public List<Kolo> list() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(LIST_KOLO);
        ResultSet resultSet = statement.executeQuery();
        List<Kolo> result = new ArrayList<>();
        while (resultSet.next()) {
            Kolo hrac = new Kolo();
            hrac.setId(resultSet.getLong(1));
            hrac.setStart(resultSet.getDate(2).toLocalDate());
            hrac.setKonec(resultSet.getDate(3).toLocalDate());
            result.add(hrac);
        }
        resultSet.close();
        return result;
    }

    public void commit() throws SQLException {
        connection.commit();
    }
}