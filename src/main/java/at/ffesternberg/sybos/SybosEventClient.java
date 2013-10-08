package at.ffesternberg.sybos;

import at.ffesternberg.sybos.entity.Event;
import at.ffesternberg.sybos.exception.SybosClientException;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Sybos client for the endpoint xmlVeranstaltung.php
 */
public class SybosEventClient extends SybosClient<Event> {
    private boolean loadPast = false;
    private boolean loadFuture = true;

    public SybosEventClient(String baseUrl, String token) {
        super(baseUrl, token);
    }

    @Override
    protected String getServerResource() {
        return "xmlVeranstaltung.php";
    }

    @Override
    protected Event processEntity(Element el) throws SybosClientException,
            ParseException {
        Event ev = new Event();
        ev.setId(Integer.parseInt(getTextContent(el, "id")));
        ev.setFrom(parseSybosDate(getTextContent(el, "von"),
                getTextContent(el, "vont")));
        ev.setFrom(parseSybosDate(getTextContent(el, "bis"),
                getTextContent(el, "bist")));
        ev.setReferat(getTextContent(el, "referat"));
        ev.setBezeichnung1(getTextContent(el, "bezeichnung1"));
        ev.setBezeichnung2(getTextContent(el, "bezeichnung2"));
        ev.setOrt(getTextContent(el, "ort"));
        ev.setInhalt(getTextContent(el, "inhalt"));
        ev.setVoraussetzung(getTextContent(el, "voraussetzung"));
        ev.setKosten(getTextContent(el, "kosten"));
        ev.setAbteilung(getTextContent(el, "abteilung"));
        ev.setTitel(getTextContent(el, "veroeffentltitel"));
        ev.setBeschreibung(getTextContent(el, "veroeffentltxt"));
        return ev;
    }

    @Override
    public List<Event> loadEntites() throws SybosClientException {
        HashSet<Event> evSet = new HashSet<Event>();
        if (isLoadPast()) {
            getArgs().put("z", "past");
            setOrder(ORDER_DESC);
            evSet.addAll(super.loadEntites());
        }
        if (isLoadFuture()) {
            getArgs().put("z", "future");
            evSet.addAll(super.loadEntites());
        }
        List<Event> evList = new LinkedList<Event>();
        evList.addAll(evSet);
        Collections.sort(evList);
        return evList;
    }

    /**
     * Is loading past Events
     *
     * @return
     */
    public boolean isLoadPast() {
        return loadPast;
    }

    /**
     * Set loading past Events
     *
     * @param loadPast
     */
    public void setLoadPast(boolean loadPast) {
        this.loadPast = loadPast;
    }

    /**
     * Is loading future Events
     *
     * @return
     */
    public boolean isLoadFuture() {
        return loadFuture;
    }

    /**
     * Set loading future Events
     */
    public void setLoadFuture(boolean loadFuture) {
        this.loadFuture = loadFuture;
    }

}
