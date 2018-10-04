package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.Commander;
import cz.i.ping.pong.liga.dao.ZapasDao;
import cz.i.ping.pong.liga.entity.Hrac;
import cz.i.ping.pong.liga.dao.HracDao;
import cz.i.ping.pong.liga.entity.Zapas;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImportService {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("d.M.yy HH:mm");

    private Connection connection;
    private HracDao hracDao;
    private ZapasDao zapasDao;

    public ImportService(String db, String user, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby:" + db, user, password);
        hracDao = new HracDao(connection);
        zapasDao = new ZapasDao(connection);
    }

    public void importFile(Commander.Import command, File file) throws IOException, SQLException {
        switch (command) {
            case HRACI:
                importHrace(file);
                break;
            case ZAPASY:
                importZapas(file);
                break;
        }
        connection.close();
    }

    private void importHrace(File file) throws IOException, SQLException {
        List<Hrac> hraci = hracDao.list();
        Set<String> jmena = new HashSet<>();
        for (Hrac h : hraci)
            jmena.add(h.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String [] parts = line.split(" ");
                importLine(parts, jmena);
                line = reader.readLine();
            }
        }
        hracDao.commit();
    }

    private void importLine(String[] line, Set<String> jmena) throws SQLException {
        Hrac hrac = new Hrac();
        hrac.setId(Long.parseLong(line[0].replace(".", "")));
        hrac.setName(line[1] + " " + line[2]);
        hrac.setEmail(line[3].replace("(","").replace(")",""));

        if (! jmena.contains(hrac.getName()))
            hracDao.insert(hrac);
    }

    private void importZapas(File file) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String [] parts = line.split(";");
                importLine(parts);
                line = reader.readLine();
            }
        }
        zapasDao.commit();
    }

    private void importLine(String[] line) throws SQLException {
        Zapas zapas = new Zapas();
        zapas.setId(Long.parseLong(line[0]));
        zapas.setScore(line[4]);
        zapas.setBodyHrac1(Integer.parseInt(line[5]));
        zapas.setBodyHrac2(Integer.parseInt(line[6]));
        if (line.length > 7)
            zapas.setTime(LocalDateTime.parse(line[7], TIMESTAMP_FORMAT));

        zapasDao.update(zapas);
    }
}
