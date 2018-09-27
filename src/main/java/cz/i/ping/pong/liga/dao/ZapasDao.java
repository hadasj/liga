package cz.i.ping.pong.liga.dao;

import cz.i.ping.pong.liga.entity.Zapas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZapasDao {
    private static final String INSERT_ZAPAS =
            "insert into zapas(id, kolo, hrac1, hrac2, skore, body_hrac1, body_hrac2, ts) values(?,?,?,?,?,?,?,?)";
    private static final String LIST_ZAPAS =
            "select z.id, z.kolo, z.hrac1, h1.name, z.hrac2, h2.name, z.skore, z.body_hrac1, z.body_hrac2, z.ts " +
                    "from zapas z " +
                    "inner join hrac h1 on z.hrac1 = h1.id " +
                    "inner join hrac h2 on z.hrac2 = h2.id " +
                    "where kolo = ? " +
                    "order by kolo z.desc, z.hrac1";
    private static final String GET_MAX_ID = "select max(id) from zapas";

    private final Connection connection;

    public ZapasDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Zapas zapas) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_ZAPAS);
        statement.setLong(1, zapas.getId());
        statement.setLong(2, zapas.getKolo());
        statement.setLong(3, zapas.getHrac1());
        statement.setLong(4, zapas.getHrac2());
        statement.setString(5, zapas.getScore());
        statement.setInt(6, zapas.getBodyHrac1());
        statement.setInt(7, zapas.getBodyHrac2());
        if (zapas.getTime() != null)
            statement.setTimestamp(8, Timestamp.valueOf(zapas.getTime()));
        else
            statement.setNull(8, Types.TIMESTAMP);
        statement.executeUpdate();
        statement.close();
    }

    public List<Zapas> list(Long kolo) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(LIST_ZAPAS);
        statement.setLong(1, kolo);
        ResultSet resultSet = statement.executeQuery();
        List<Zapas> result = new ArrayList<>();
        while (resultSet.next()) {
            Zapas zapas = new Zapas();
            zapas.setId(resultSet.getLong(1));
            zapas.setKolo(resultSet.getLong(2));
            zapas.setHrac1(resultSet.getLong(3));
            zapas.setHrac1Jmeno(resultSet.getString(4));
            zapas.setHrac2(resultSet.getLong(5));
            zapas.setHrac2Jmeno(resultSet.getString(6));
            zapas.setScore(resultSet.getString(7));
            zapas.setBodyHrac1(resultSet.getInt(8));
            zapas.setBodyHrac2(resultSet.getInt(9));
            if (resultSet.getTimestamp(10) != null)
                zapas.setTime(resultSet.getTimestamp(10).toLocalDateTime());
            result.add(zapas);
        }
        resultSet.close();
        return result;
    }

    public long getMaxId() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(GET_MAX_ID);
        ResultSet resultSet = statement.executeQuery();
        Long maxId = null;
        if (resultSet.next())
            maxId =  resultSet.getLong(1);
        if (maxId == null)
            maxId = 0L;
        return maxId;
    }
}
