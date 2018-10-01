package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.dao.KoloDao;
import cz.i.ping.pong.liga.dao.ZapasDao;
import cz.i.ping.pong.liga.entity.Kolo;
import cz.i.ping.pong.liga.entity.Zapas;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ExportService {
    private static final String FILE_NAME = "zapas_";
    private static final String EXTENSION = ".csv";
    private static final String DELIMITER = ";";

    private ZapasDao zapasDao;
    private KoloDao koloDao;

    public ExportService(String db, String user, String password) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:derby:" + db, user, password);
        zapasDao = new ZapasDao(connection);
        koloDao = new KoloDao(connection);
    }

    public void exportKolo(long kolo) throws SQLException, IOException {
        List<Zapas> zapasy = zapasDao.list(kolo);
        File file = new File(FILE_NAME + kolo + EXTENSION);
        if (file.exists())
            throw new IllegalStateException("soubor " + file.getAbsolutePath() + " existuje!");

        PrintWriter out = new PrintWriter(new FileWriter(file));
        for (Zapas zapas : zapasy) {
            out.println(zapas.getId() + DELIMITER + zapas.getKolo() + DELIMITER +
                    zapas.getHrac1Jmeno() + DELIMITER + zapas.getHrac2Jmeno() + DELIMITER +
                    trim(zapas.getScore()) + DELIMITER + trim(zapas.getBodyHrac1()) + DELIMITER +
                    trim(zapas.getBodyHrac2()) + DELIMITER + trim(zapas.getTime()));
        }
        out.close();
    }

    public long getLastKolo() throws SQLException {
        List<Kolo> kola = koloDao.list();

        if (kola.isEmpty())
            throw new IllegalStateException("DB je prazdna!");

        return kola.get(kola.size() - 1).getId();
    }

    private Object trim(Object o) {
        if (o == null)
            return "";
        else
            return o;
    }

}
