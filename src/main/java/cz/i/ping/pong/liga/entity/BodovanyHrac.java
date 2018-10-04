package cz.i.ping.pong.liga.entity;

public class BodovanyHrac extends Hrac implements Comparable<BodovanyHrac> {
    private int body;

    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    @Override
    public int compareTo(BodovanyHrac other) {
        return Integer.compare(other.body, body);
    }
}
