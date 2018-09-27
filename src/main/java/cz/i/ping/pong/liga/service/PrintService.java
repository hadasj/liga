package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.dao.HracDao;
import cz.i.ping.pong.liga.entity.Hrac;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

public class PrintService {

    private HracDao hracDao;

    public PrintService(String db, String user, String password) throws SQLException {
        hracDao = new HracDao(db, user, password);
    }

    public void print(PrintStream out) throws SQLException {
        List<Hrac> hraci = hracDao.list();
        hracDao.close();

        out.println();
        out.println("Hraci:");
        for (Hrac hrac : hraci)
            out.println(hrac.getId() + ", " + hrac.getName() + ", " + hrac.getEmail());
    }
}
