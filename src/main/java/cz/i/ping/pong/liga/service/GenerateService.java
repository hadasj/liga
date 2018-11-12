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
    private static final int MAX_POCET_OPAKOVANI = 5;

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
        Kolo kolo = createKolo(zacatek, konec);
        long idZapasu = getMaxIdZapasu() + 1;

        List<Long> hraciList = hracDao.list().stream().filter(Hrac::isAktivni).map(Hrac::getId).collect(Collectors.toList());
        Map<Long, Set<Long>> hraciMap = new HashMap<>();
        Set<Long> vylosovani = new HashSet<>();
        List<Zapas> zapasy = new ArrayList<>();

        for (Long hrac : hraciList) {
            // na zacatku ma vsechny soupere
            Set<Long> ostatni = new HashSet<>(hraciList);
            // krome sebe
            ostatni.remove(hrac);
            // a krome spoluhracu se kterymi uz hral
            ostatni.removeAll(hracDao.searchSpoluhrace(hrac));
            hraciMap.put(hrac, ostatni);
        }

        SortedSet<Map.Entry<Long, Set<Long>>> sestupneTrideniHraci = new TreeSet<>(new DescendingSizeComparator());
        sestupneTrideniHraci.addAll(hraciMap.entrySet());

        for (Map.Entry<Long, Set<Long>> entry : sestupneTrideniHraci) {
            Long hrac = entry.getKey();
            if (!vylosovani.contains(hrac)) {
                // hrac jeste nema urceneho soupere
                Set<Long> souperi = entry.getValue();
                // odeber hrace kteri jsou uz toto kolo vylosovani
                souperi.removeAll(vylosovani);

                if (!souperi.isEmpty()) {
                    // vylosuj soupere
                    Long souper = drawAdversary(souperi);
                    // kontrola, jestli jsme jinemu jeste nevylosovanemu hraci nesebrali posledniho mozneho soupere
                    boolean pokracovat = checkRemainingPlayers(hrac, souper, vylosovani, hraciMap);

                    for (int zopakovano = 0; !pokracovat; zopakovano++) {
                        souper = drawAdversary(souperi);
                        pokracovat = checkRemainingPlayers(hrac, souper, vylosovani, hraciMap);
                        if (zopakovano > MAX_POCET_OPAKOVANI)
                            fail(hrac, zapasy);
                    }
                    // zapis dvojici do zapasu
                    vylosovani.addAll(asList(hrac, souper));

                    // perzistuj zapasy
                    Zapas zapas = new Zapas();
                    zapas.setId(idZapasu++);
                    zapas.setKolo(kolo.getId());
                    zapas.setHrac1(hrac);
                    zapas.setHrac2(souper);
                    zapasy.add(zapas);
                } else {
                    // nepodarilo se pro hrace najit zadneho soupere,
                    // protoze vsichni hraci, se kterymi jeste nehral, uz maji vylosovane soupere
                    fail(hrac, zapasy);
                }
            }
        }

        koloDao.insert(kolo);
        for (Zapas zapas : zapasy)
            zapasDao.insert(zapas);
        connection.commit();
    }

    private void fail(final Long hrac, List<Zapas> zapasy) throws IllegalStateException {
        StringBuilder message = new StringBuilder("Vylosovane dvojice: ");
        for (Zapas zapas : zapasy) {
            message.append(zapas.getHrac1()).append(":").append(zapas.getHrac2()).append(", ");
        }
        message.append("\nNepodařilo se najít soupeře pro hráče s ID ").append(hrac).append(".")
                .append("\nKolo nebylo vygenerováno.");
        throw new IllegalStateException(message.toString());
    }

    private Long drawAdversary(Set<Long> souperi) {
        int size = souperi.size();
        int indexSoupere = random.nextInt(size);
        return souperi.toArray(new Long[size])[indexSoupere];
    }

    private Kolo createKolo(LocalDate zacatek, LocalDate konec) throws SQLException {
        List<Kolo> kola = koloDao.list();
        long posledniKolo = 0;
        if (!kola.isEmpty())
            posledniKolo = kola.get(kola.size()-1).getId();

        Kolo kolo = new Kolo();
        kolo.setId(posledniKolo + 1);
        kolo.setStart(zacatek);
        kolo.setKonec(konec);
        return kolo;
    }

    private boolean checkRemainingPlayers(final long hrac, final long souper, final Set<Long> vylosovani,
                                          final Map<Long, Set<Long>> hraciMap) {
        for (Map.Entry<Long, Set<Long>> entry : hraciMap.entrySet()) {
            final Long kontrolovanyHrac = entry.getKey();
            // preskoc kontrolu vylosovane dvojice - ta uz je vylosovana -> je OK
            if (kontrolovanyHrac.equals(hrac) || kontrolovanyHrac.equals(souper))
                continue;
            // hrac uz je vylosovany - nemusis ho kontrolovat
            if (vylosovani.contains(kontrolovanyHrac))
                continue;
            Set<Long> potencialniSouperi = entry.getValue();
            // odeber jiz vylosovane hrace
            potencialniSouperi.removeAll(vylosovani);
            // a aktualni vylosovanou dvojici
            potencialniSouperi.remove(hrac);
            potencialniSouperi.remove(souper);
            // a zkontroluj, ze hraci jeste zustal nejaky souper
            if (potencialniSouperi.isEmpty())
                // pokud ne, tak neni mozne pokracovat
                return false;
        }
        return true;
    }

    private long getMaxIdZapasu() throws SQLException {
        return zapasDao.getMaxId();
    }

    private class DescendingSizeComparator implements Comparator<Map.Entry<Long, Set<Long>>> {
        @Override
        public int compare(Map.Entry<Long, Set<Long>> firstPLayer, Map.Entry<Long, Set<Long>> secondPlayer) {
            if (firstPLayer.getValue().size() == secondPlayer.getValue().size())
                // stejna velikost -> tridime podle IDcka vzestupne
                return firstPLayer.getKey().compareTo(secondPlayer.getKey());
            else
                // ruzna velikost -> tridime podle velikosti sestupne
                return Integer.compare(secondPlayer.getValue().size(), firstPLayer.getValue().size());
        }
    }
}