package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.Commander;
import cz.i.ping.pong.liga.dao.HracDao;
import cz.i.ping.pong.liga.dao.KoloDao;
import cz.i.ping.pong.liga.dao.ZapasDao;
import cz.i.ping.pong.liga.entity.Hrac;
import cz.i.ping.pong.liga.entity.Kolo;
import cz.i.ping.pong.liga.entity.Zapas;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class PrintService {

    private Connection connection;
    private HracDao hracDao;
    private KoloDao koloDao;
    private ZapasDao zapasDao;

    public PrintService(String db, String user, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby:" + db, user, password);
        hracDao = new HracDao(connection);
        koloDao = new KoloDao(connection);
        zapasDao = new ZapasDao(connection);
    }

    public void print(PrintStream out) throws SQLException {
        List<Hrac> hraci = hracDao.list();

        out.println();
        out.println("Hráči:");
        for (Hrac hrac : hraci)
            out.println(hrac.getId() + ", " + hrac.getName() + ", " + hrac.getEmail());

        List<Kolo> kola = koloDao.list();
        out.println();
        out.println("Zápasy");
        for (Kolo kolo : kola) {
            out.println(kolo.getId() + ". kolo (" + kolo.getStart().format(Commander.FORMAT)
                    + "-" + kolo.getKonec().format(Commander.FORMAT) + "):");
            List<Zapas> zapasy = zapasDao.list(kolo.getId());

            int row = 1;
            for (Zapas zapas : zapasy) {
                out.print("  " + row++ + ". " + zapas.getHrac1Jmeno() + " : " + zapas.getHrac2Jmeno());
                if (zapas.getScore() != null && !zapas.getScore().trim().isEmpty())
                    out.print(", " + zapas.getScore() + ", " + zapas.getTime());
                out.println();
            }
        }
        connection.close();
    }
}
