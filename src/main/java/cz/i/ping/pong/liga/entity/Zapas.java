package cz.i.ping.pong.liga.entity;

import java.time.LocalDateTime;

public class Zapas {
    private Long id;
    private Long kolo;
    private Long hrac1;
    private String hrac1Jmeno;
    private Long hrac2;
    private String hrac2Jmeno;
    private String score;
    private Integer bodyHrac1;
    private Integer bodyHrac2;
    private LocalDateTime time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKolo() {
        return kolo;
    }

    public void setKolo(Long kolo) {
        this.kolo = kolo;
    }

    public Long getHrac1() {
        return hrac1;
    }

    public void setHrac1(Long hrac1) {
        this.hrac1 = hrac1;
    }

    public String getHrac1Jmeno() {
        return hrac1Jmeno;
    }

    public void setHrac1Jmeno(String hrac1Jmeno) {
        this.hrac1Jmeno = hrac1Jmeno;
    }

    public Long getHrac2() {
        return hrac2;
    }

    public void setHrac2(Long hrac2) {
        this.hrac2 = hrac2;
    }

    public String getHrac2Jmeno() {
        return hrac2Jmeno;
    }

    public void setHrac2Jmeno(String hrac2Jmeno) {
        this.hrac2Jmeno = hrac2Jmeno;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Integer getBodyHrac1() {
        return bodyHrac1;
    }

    public void setBodyHrac1(Integer bodyHrac1) {
        this.bodyHrac1 = bodyHrac1;
    }

    public Integer getBodyHrac2() {
        return bodyHrac2;
    }

    public void setBodyHrac2(Integer bodyHrac2) {
        this.bodyHrac2 = bodyHrac2;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
