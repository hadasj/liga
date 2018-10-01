package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.entity.Hrac;
import cz.i.ping.pong.liga.dao.HracDao;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImportService {

    private Connection connection;
    private HracDao hracDao;

    public ImportService(String db, String user, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby:" + db, user, password);
        hracDao = new HracDao(connection);
    }

    public void importFile(File file) throws IOException, SQLException {
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
        connection.close();
    }

    private void importLine(String[] line, Set<String> jmena) throws SQLException {
        Hrac hrac = new Hrac();
        hrac.setId(Long.parseLong(line[0].replace(".", "")));
        hrac.setName(line[1] + " " + line[2]);
        hrac.setEmail(line[3].replace("(","").replace(")",""));

        if (! jmena.contains(hrac.getName()))
            hracDao.insert(hrac);
    }
}
