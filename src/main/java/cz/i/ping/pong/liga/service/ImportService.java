package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.entity.Hrac;
import cz.i.ping.pong.liga.dao.HracDao;

import java.io.*;
import java.sql.SQLException;

public class ImportService {

    private HracDao hracDao;

    public void importFile(File file, String db, String user, String password) throws IOException, SQLException {
        hracDao = new HracDao(db, user, password);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String [] parts = line.split(" ");
                importLine(parts);
                line = reader.readLine();
            }
        }
        hracDao.commit();
    }

    private void importLine(String[] line) throws SQLException {
        Hrac hrac = new Hrac();
        hrac.setId(Long.parseLong(line[0].replace(".", "")));
        hrac.setName(line[1] + " " + line[2]);
        hrac.setEmail(line[3].replace("(","").replace(")",""));

        hracDao.insert(hrac);
    }
}
