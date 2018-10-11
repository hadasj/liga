package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.dao.HracDao;
import cz.i.ping.pong.liga.entity.Hrac;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UpdateService {
    private HracDao hracDao;

    public UpdateService(String db, String user, String password) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:derby:" + db, user, password);
        hracDao = new HracDao(connection);
    }

    public Hrac getHrac(Long id) throws SQLException {
        return hracDao.get(id);
    }

    public void disableHrac(Long id) throws SQLException{
        Hrac hrac = new Hrac();
        hrac.setId(id);
        hrac.setAktivni(false);

        hracDao.update(hrac);
        hracDao.commit();
    }

    public void enableHrac(Long id) throws SQLException {
        Hrac hrac = new Hrac();
        hrac.setId(id);
        hrac.setAktivni(true);

        hracDao.update(hrac);
        hracDao.commit();
    }
 }
