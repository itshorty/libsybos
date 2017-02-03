package at.ffesternberg.sybos;

import at.ffesternberg.sybos.entity.Event;
import at.ffesternberg.sybos.exception.SybosClientException;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.util.Map;

/**
 * Sybos client for the endpoint xmlVeranstaltung.php
 */
public class SybosEventClient extends SybosClient<Event> {
    private boolean loadPast = false;

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
    public Map<String, String> getArgs() {
        Map<String,String> args = super.getArgs();
        if(loadPast){
            args.put("z","past");
        }else{
            args.put("z","future");
        }
        return args;
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
}
