package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.entity.Uzivatel;
import cz.i.ping.pong.liga.dao.UzivatelDao;

import java.io.*;
import java.sql.SQLException;

public class ImportService {

    private UzivatelDao uzivatelDao;

    public void importFile(File file, String db, String user, String password) throws IOException, SQLException {
        uzivatelDao = new UzivatelDao(db, user, password);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String [] parts = line.split(" ");
                importLine(parts);
                line = reader.readLine();
            }
        }
        uzivatelDao.commit();
    }

    private void importLine(String[] line) throws SQLException {
        Uzivatel uzivatel = new Uzivatel();
        uzivatel.setId(Long.parseLong(line[0].replace(".", "")));
        uzivatel.setName(line[1] + " " + line[2]);
        uzivatel.setEmail(line[4].replace("(","").replace(")",""));

        uzivatelDao.insert(uzivatel);
    }
}
