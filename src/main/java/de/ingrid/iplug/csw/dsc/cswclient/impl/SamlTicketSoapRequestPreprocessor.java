package de.ingrid.iplug.csw.dsc.cswclient.impl;

import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.csw.dsc.cswclient.CSWRequestPreprocessor;

public class SamlTicketSoapRequestPreprocessor implements CSWRequestPreprocessor<ServiceClient> {

    final static Log log = LogFactory.getLog(SamlTicketSoapRequestPreprocessor.class);

    private String soapHeaderTemplate = null;

    private SamlTicketProvider samlTicketProvider = null;

    @Override
    public ServiceClient process(ServiceClient param) {
        String samlTicket = samlTicketProvider.get();
        if (log.isDebugEnabled()) {
            log.debug("Got SAML Ticket: " + samlTicket);
        }

        try {

            soapHeaderTemplate = soapHeaderTemplate.replaceAll("\\$\\{SAML_TICKET\\}", samlTicket);

            param.addHeader(AXIOMUtil.stringToOM(soapHeaderTemplate));
        } catch (Exception e) {
            log.error("Error adding SOAP header.", e);
        }

        return param;
    }

    public void setSoapHeaderTemplate(String soapHeaderTemplate) {
        this.soapHeaderTemplate = soapHeaderTemplate;
    }

    public void setSamlTicketProvider(SamlTicketProvider samlTicketProvider) {
        this.samlTicketProvider = samlTicketProvider;
    }

}
