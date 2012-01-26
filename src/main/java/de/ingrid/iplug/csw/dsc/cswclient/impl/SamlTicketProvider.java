package de.ingrid.iplug.csw.dsc.cswclient.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SamlTicketProvider {

    final static Log log = LogFactory.getLog(SamlTicketSoapRequestPreprocessor.class);
    
    final static String SAML_TICKET_CACHE_NAME = "samlTicketCache";

    final static String SAML_TICKET_CACHE_KEY = "samlTicket";

    private String samlTicketRequestUrl = null;

    private Cache samlTicketCache = null;

    private Integer cacheForSeconds = 30;

    public SamlTicketProvider() {
        // Create a CacheManager using defaults
        CacheManager manager = CacheManager.create();
        if (!manager.cacheExists(SAML_TICKET_CACHE_NAME)) {
            manager.addCache(SAML_TICKET_CACHE_NAME);
        }

        samlTicketCache = manager.getCache(SAML_TICKET_CACHE_NAME);
        CacheConfiguration config = samlTicketCache.getCacheConfiguration();
        config.setTimeToIdleSeconds(cacheForSeconds);
        config.setTimeToLiveSeconds(cacheForSeconds);
        config.setMaxElementsInMemory(1);
        config.setOverflowToDisk(false);

    }

    public String get() {
        Element cacheElement = samlTicketCache.get(SAML_TICKET_CACHE_KEY);
        String samlTicket;
        if (cacheElement == null || cacheElement.toString() == null) {
            if (log.isDebugEnabled()) {
                log.debug("SAML not in cache, requery from: " + samlTicketRequestUrl);
            }
            samlTicket = getSamlTicket();
            samlTicketCache.put(new Element(SAML_TICKET_CACHE_KEY, samlTicket));
        } else {
            samlTicket = cacheElement.toString();
        }
        if (log.isDebugEnabled()) {
            log.debug("Got SAML Ticket: " + samlTicket);
        }

        return samlTicket;
    }

    public void setSamlTicketRequestUrl(String samlTicketRequestUrl) {
        this.samlTicketRequestUrl = samlTicketRequestUrl;
    }

    public void setCacheForSeconds(Integer cacheForSeconds) {
        this.cacheForSeconds = cacheForSeconds;
    }

    private String getSamlTicket() {
        StringBuilder result = new StringBuilder();
        try {
            // Create a URL for the desired page
            URL url = new URL(samlTicketRequestUrl);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                result.append(str);
            }
            in.close();
        } catch (Exception e) {
            log.error("Error getting SAML Ticket from: " + samlTicketRequestUrl, e);
        }
        return result.toString();
    }

}