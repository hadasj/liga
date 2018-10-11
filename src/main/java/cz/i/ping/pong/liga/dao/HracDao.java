package cz.i.ping.pong.liga.dao;

import cz.i.ping.pong.liga.entity.Hrac;

import java.sql.*;
import java.util.*;

public class HracDao {
    private static final String INSERT_HRAC = "insert into hrac(id,name,email) values(?,?,?)";
    private static final String UPDATE_HRAC = "update hrac set aktivni = ? where id = ?";
    private static final String GET_HRAC = "select id, name, email, aktivni from hrac where id = ?";
    private static final String LIST_HRAC = "select id, name, email, aktivni from hrac order by id";
    private static final String SEARCH_SPOLUHRACE_1 = "select hrac1 from zapas where hrac2 = ?";
    private static final String SEARCH_SPOLUHRACE_2 = "select hrac2 from zapas where hrac1 = ?";

    private final Connection connection;

    public HracDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Hrac hrac) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_HRAC);
        statement.setLong(1, hrac.getId());
        statement.setString(2, hrac.getName());
        statement.setString(3, hrac.getEmail());
        statement.executeUpdate();
        statement.close();
    }

    public void update(Hrac hrac) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_HRAC);
        statement.setBoolean(1, hrac.isAktivni());
        statement.setLong(2, hrac.getId());
        statement.executeUpdate();
        statement.close();
    }

    public Hrac get(Long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(GET_HRAC);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();

        Hrac hrac = null;
        if (resultSet.next()) {
            hrac = new Hrac();
            hrac.setId(resultSet.getLong(1));
            hrac.setName(resultSet.getString(2));
            hrac.setEmail(resultSet.getString(3));
            hrac.setAktivni(resultSet.getBoolean(4));
        }
        resultSet.close();
        statement.close();
        return hrac;
    }

    public List<Hrac> list() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(LIST_HRAC);
        ResultSet resultSet = statement.executeQuery();
        List<Hrac> result = new ArrayList<>();
        while (resultSet.next()) {
            Hrac hrac = new Hrac();
            hrac.setId(resultSet.getLong(1));
            hrac.setName(resultSet.getString(2));
            hrac.setEmail(resultSet.getString(3));
            hrac.setAktivni(resultSet.getBoolean(4));
            result.add(hrac);
        }
        resultSet.close();
        return result;
    }

    /**
     * @return - kolekce ID hracu, se kterymi uz hral
     */
    public Collection<Long> searchSpoluhrace(long idHrace) throws SQLException {
        Set<Long> result = new HashSet<>();

        PreparedStatement statement = connection.prepareStatement(SEARCH_SPOLUHRACE_1);
        statement.setLong(1, idHrace);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
            result.add(resultSet.getLong(1));
        resultSet.close();

        statement = connection.prepareStatement(SEARCH_SPOLUHRACE_2);
        statement.setLong(1, idHrace);
        resultSet = statement.executeQuery();
        while (resultSet.next())
            result.add(resultSet.getLong(1));
        resultSet.close();

        return result;
    }

    public void commit() throws SQLException {
        connection.commit();
    }
}
