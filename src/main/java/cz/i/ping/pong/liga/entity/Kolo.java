package cz.i.ping.pong.liga.entity;

import java.time.LocalDate;

public class Kolo {
    private Long id;
    private LocalDate start;
    private LocalDate konec;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getKonec() {
        return konec;
    }

    public void setKonec(LocalDate konec) {
        this.konec = konec;
    }
}
