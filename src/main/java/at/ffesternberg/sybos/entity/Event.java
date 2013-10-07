package at.ffesternberg.sybos.entity;

import java.util.Date;

/**
 * A Event Entity retuned by the SyBos API
 */
public class Event implements SybosEntity, Comparable<Event> {
    private int id;
    private Date from;
    private Date to;
    private String referat;
    private String bezeichnung1;
    private String bezeichnung2;
    private String ort;
    private String inhalt;
    private String voraussetzung;
    private String kosten;
    private String abteilung;
    private String titel;
    private String beschreibung;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getReferat() {
        return referat;
    }

    public void setReferat(String referat) {
        this.referat = referat;
    }

    public String getBezeichnung1() {
        return bezeichnung1;
    }

    public void setBezeichnung1(String bezeichnung1) {
        this.bezeichnung1 = bezeichnung1;
    }

    public String getBezeichnung2() {
        return bezeichnung2;
    }

    public void setBezeichnung2(String bezeichnung2) {
        this.bezeichnung2 = bezeichnung2;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getInhalt() {
        return inhalt;
    }

    public void setInhalt(String inhalt) {
        this.inhalt = inhalt;
    }

    public String getVoraussetzung() {
        return voraussetzung;
    }

    public void setVoraussetzung(String voraussetzung) {
        this.voraussetzung = voraussetzung;
    }

    public String getKosten() {
        return kosten;
    }

    public void setKosten(String kosten) {
        this.kosten = kosten;
    }

    public String getAbteilung() {
        return abteilung;
    }

    public void setAbteilung(String abteilung) {
        this.abteilung = abteilung;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public int compareTo(Event other) {
        return this.getFrom().compareTo(other.getFrom());
    }

}
