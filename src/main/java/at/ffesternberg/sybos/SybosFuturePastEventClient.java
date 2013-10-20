package at.ffesternberg.sybos;

import at.ffesternberg.sybos.entity.Event;
import at.ffesternberg.sybos.exception.SybosClientException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SybosFuturePastEventClient extends SybosEventClient{
    private boolean loadPast=false;
    private boolean loadFuture=true;

    public SybosFuturePastEventClient(String baseUrl, String token) {
        super(baseUrl, token);
    }

    @Override
    public List<Event> loadEntites() throws SybosClientException {
        HashSet<Event> events=new HashSet<Event>();
        if(isLoadPast()){
            super.setLoadPast(true);
            events.addAll(super.loadEntites());
        }
        if(isLoadFuture()){
            super.setLoadPast(false);
            events.addAll(super.loadEntites());
        }
        List<Event> eventList = new ArrayList<Event>();
        eventList.addAll(events);
        Collections.sort(eventList);
        return eventList;
     }

    public boolean isLoadPast() {
        return loadPast;
    }

    public void setLoadPast(boolean loadPast) {
        this.loadPast = loadPast;
    }

    public boolean isLoadFuture() {
        return loadFuture;
    }

    public void setLoadFuture(boolean loadFuture) {
        this.loadFuture = loadFuture;
    }
}
