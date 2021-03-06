package at.ffesternberg.sybos;

import at.ffesternberg.sybos.entity.SybosEntity;
import at.ffesternberg.sybos.exception.NoSybosTokenException;
import at.ffesternberg.sybos.exception.SybosClientException;
import at.ffesternberg.sybos.exception.WrongSybosTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An abstract base class for loading SybosEntities from a sybos api<br/>
 * Handles the session Token and the base URL
 *
 * @param <T>
 */
public abstract class SybosClient<T extends SybosEntity> {
    private static final Logger log = LoggerFactory
            .getLogger(SybosClient.class);
    private static final String NO_TOKEN = "Token fehlt";
    private static final Pattern WRONG_TOKEN = Pattern
            .compile("Token \\((.*\\)) unbekannt oder nicht aktiv");

    protected static final SimpleDateFormat SYBOS_DATE_FORMAT = new SimpleDateFormat(
            "dd.MM.yyyy HH:mm");

    private String baseUrl;
    private String token;
    private int count = 60;
    private int offset = 0;
    private int thumbHeight = -1;
    private int mediumHeight = -1;
    //private int id;
    private SybosOrder order=SybosOrder.DEFAULT;
    private Map<String, String> args = new HashMap<String, String>();

    /**
     * Creates a new SybosClient
     *
     * @param baseUrl the API base URL (without e.g. xmlMaterial.php)
     * @param token   the access token for the SyBos API
     */
    public SybosClient(String baseUrl, String token) {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        this.baseUrl = baseUrl;
        this.token = token;
    }

    /**
     * Loads the XML with the preset options, then parses each &lt;item&gt; XML Element to an Entity
     *
     * @return a list of returned entites
     * @throws SybosClientException if something went wrong
     */
    public List<T> loadEntites() throws SybosClientException {
        Document d = loadXML();
        List<T> list = new LinkedList<T>();
        Element root = d.getDocumentElement();
        NodeList items = root.getElementsByTagName("item");
        if (items.getLength() == 0) {
        	NodeList errorNode = root.getElementsByTagName("error");
        	if(errorNode.getLength()>0){
        		String error = getTextContent(root, "error");
        		if (error.equals(NO_TOKEN)) {
                    throw new NoSybosTokenException();
                }
                Matcher wtMatcher = WRONG_TOKEN.matcher(error);
                if (wtMatcher.matches()) {
                    throw new WrongSybosTokenException(wtMatcher.group(1));
                }
                throw new SybosClientException("Error response from server: "+error);
        	}
        }
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            try {
                list.add(processEntity(item));
            } catch (ParseException e) {
                throw new SybosClientException(
                        "Error while Parsing list element " + item, e);
            }
        }
        return list;
    }

    /**
     * Downloads the XML from the API with all preset options (parameters)
     *
     * @return a XML document
     * @throws SybosClientException
     */
    protected Document loadXML() throws SybosClientException {
        URL url = null;
        try {
            url = new URL(baseUrl + getServerResource() + "?token=" + token
                    + getArguments());
            log.debug("request: " + url.toExternalForm());
            DocumentBuilder db = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            if(log.isTraceEnabled()){
            	reader.mark(100000);
            	String line;
            	while((line=reader.readLine())!=null){
            		log.trace(line);
            	}
            	reader.reset();
            }
            Document d = db.parse(new InputSource(reader));
            return d;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            log.error("Malformed XML File: " + url, e);
            throw new SybosClientException("Malformed XML file! " + url, e);
        } catch (IOException e) {
            log.error("IO Exception while loading XML", e);
            throw new SybosClientException("IO Excption while loading XML", e);
        }
    }

    /**
     * Builds a string of all preset arguments
     *
     * @return url string of all arguments
     */
    private String getArguments() {
        StringBuilder sb = new StringBuilder();
        sb.append("&a=").append(getCount());
        sb.append("&o=").append(getOrder().getOrderString());
        sb.append("&f=").append(getOffset());
        if(thumbHeight>0)
            sb.append("&thumbHeight=").append(getThumbHeight());
        if(mediumHeight>0)
            sb.append("&mediumHeight").append(getMediumHeight());
        if (getArgs() != null) {
            for (Entry<String, String> argument : getArgs().entrySet()) {
                sb.append("&").append(argument.getKey()).append("=")
                        .append(argument.getValue());
            }
        }
        return sb.toString();
    }

    /**
     * get current entity limit (api parameter a)
     *
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Set entity limit (api parameter a)
     *
     * @param count
     */
    public void setCount(int count) {
        if (count < 0)
            throw new IllegalArgumentException("Count < 0: "+count);
        this.count = count;
    }


    /**
     * the order of the request (api parameter o)
     *
     * @return
     */
    public SybosOrder getOrder() {
        return order;
    }

    /**
     * Order of the request (api parameter o)
     *
     * @param order
     */
    public void setOrder(SybosOrder order) {
        if(order == null){
            this.order=SybosOrder.DEFAULT;
        }else{
            this.order=order;
        }

    }

    /**
     * Offest of the request (for pageing) (api parameter f)
     * @return
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Offest of the request (for pageing) (api parameter f)
     * @param offset
     */
    public void setOffset(int offset) {
        if(offset<0)
            throw new IllegalArgumentException("Offset < 0:"+ offset);
        this.offset = offset;
    }

    /**
     * Height of the returned pictures (resized by keeping aspect ratio) (api parameter thumbHeight)
     *
     * If negative (thumbHeight<0) the parameter is ignored and sybos default is used
     */
    public int getThumbHeight() {
        return thumbHeight;
    }
    /**
     * Height of the returned pictures (resized by keeping aspect ratio) (api parameter thumbHeight)
     *
     * If negative (thumbHeight<0) the parameter is ignored and sybos default is used
     */
    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }
    /**
     * Height of the returned pictures (resized by keeping aspect ratio) (api parameter mediumHeight)
     *
     * If negative (mediumHeight<0) the parameter is ignored and sybos default is used
     */
    public int getMediumHeight() {
        return mediumHeight;
    }
    /**
     * Height of the returned pictures (resized by keeping aspect ratio) (api parameter mediumHeight)
     *
     * If negative (mediumHeight<0) the parameter is ignored and sybos default is used
     */
    public void setMediumHeight(int mediumHeight) {
        this.mediumHeight = mediumHeight;
    }

    /**
     * The argument map
     *
     * @return
     */
    public Map<String, String> getArgs() {
        return args;
    }

    /**
     * Set the argument map
     *
     * @param args
     */
    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    /**
     * Utility Method - Combine a XML Date and Time to a java.util.Date
     *
     * @param date
     * @param time
     * @return
     * @throws ParseException
     */
    protected Date parseSybosDate(String date, String time)
            throws ParseException {
        String tmp = date + " " + time;
        return SYBOS_DATE_FORMAT.parse(tmp);
    }

    /**
     * Utility Method - Returns the text content of a child tag
     *
     * @param el  the parent tag
     * @param tag the name of the tag which content should be returnd
     * @return the text content of the tag
     * @throws SybosClientException if tag was not found
     */
    protected String getTextContent(Element el, String tag)
            throws SybosClientException {
        NodeList items = el.getElementsByTagName(tag);
        if (items.getLength() >= 1) {
            return items.item(0).getTextContent();
        } else {
            throw new SybosClientException("Malformed XML: Element " + tag + " not found!");
        }
    }

    /**
     * This should return the API endpoint
     *
     * @return the API endpoint (e.g. xmlMaterial.php)
     */
    protected abstract String getServerResource();

    /**
     * Parse an single XML element and convert it to the given entity
     *
     * @param el the type of the parsed entity
     * @return the parsed entity
     * @throws SybosClientException
     * @throws ParseException
     */
    protected abstract T processEntity(Element el) throws SybosClientException,
            ParseException;
}
