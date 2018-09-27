package cz.i.ping.pong.liga.service;

import cz.i.ping.pong.liga.dao.HracDao;
import cz.i.ping.pong.liga.dao.KoloDao;
import cz.i.ping.pong.liga.dao.ZapasDao;
import cz.i.ping.pong.liga.entity.Hrac;
import cz.i.ping.pong.liga.entity.Kolo;
import cz.i.ping.pong.liga.entity.Zapas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class GenerateService {
    private Connection connection;
    private HracDao hracDao;
    private KoloDao koloDao;
    private ZapasDao zapasDao;
    private Random random;

    public GenerateService(String db, String user, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby:" + db, user, password);
        hracDao = new HracDao(connection);
        koloDao = new KoloDao(connection);
        zapasDao = new ZapasDao(connection);
        random = new Random();
    }

    public void generate(LocalDate zacatek, LocalDate konec) throws SQLException {
        long kolo = createKolo(zacatek, konec);
        long idZapasu = getMaxIdZapasu() + 1;

        List<Long> hraciList = hracDao.list().stream().map(Hrac::getId).collect(Collectors.toList());
        Map<Long, Set<Long>> hraciMap = new HashMap<>();
        Map<Long, Long> zapasy = new HashMap<>();
        Set<Long> vylosovani = new HashSet<>();

        for (Long hrac : hraciList) {
            // na zacatku ma vsechny soupere
            Set<Long> ostatni = new HashSet<>(hraciList);
            // krome sebe
            ostatni.remove(hrac);
            // a krome spoluhracu se kterymi uz hral
            ostatni.removeAll(hracDao.searchSpoluhrace(hrac));
            hraciMap.put(hrac, ostatni);
        }

        for (Map.Entry<Long, Set<Long>> entry : hraciMap.entrySet()) {
            Long hrac = entry.getKey();
            if (zapasy.get(hrac) == null) {
                // hrac jeste nema urceneho soupere
                Set<Long> souperi = entry.getValue();
                // odeber hrace kteri jsou uz toto kolo vylosovani
                souperi.removeAll(vylosovani);

                // vylosuj soupere
                int size = souperi.size();
                int indexSoupere = random.nextInt(size);
                Long souper = entry.getValue().toArray(new Long[size])[indexSoupere];

                // zapis dvojici do zapasu
                vylosovani.addAll(asList(hrac, souper));
                zapasy.put(hrac, souper);
                zapasy.put(souper, hrac);

                // perzistuj zapasy
                Zapas zapas = new Zapas();
                zapas.setId(idZapasu++);
                zapas.setKolo(kolo);
                zapas.setHrac1(hrac);
                zapas.setHrac2(souper);
                zapasDao.insert(zapas);

                zapas = new Zapas();
                zapas.setId(idZapasu++);
                zapas.setKolo(kolo);
                zapas.setHrac1(souper);
                zapas.setHrac2(hrac);
                zapasDao.insert(zapas);
            }
        }

        connection.commit();
    }

    private long createKolo(LocalDate zacatek, LocalDate konec) throws SQLException {
        List<Kolo> kola = koloDao.list();
        long posledniKolo = 0;
        if (!kola.isEmpty())
            posledniKolo = kola.get(kola.size()-1).getId();

        Kolo kolo = new Kolo();
        kolo.setId(posledniKolo + 1);
        kolo.setStart(zacatek);
        kolo.setKonec(konec);
        koloDao.insert(kolo);
        return kolo.getId();
    }

    private long getMaxIdZapasu() throws SQLException {
        return zapasDao.getMaxId();
    }
}