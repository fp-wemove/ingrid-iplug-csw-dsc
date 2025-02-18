/*
 * **************************************************-
 * ingrid-iplug-csw-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/**
 * 
 */
package de.ingrid.iplug.csw.dsc.record.mapper;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ingrid.iplug.csw.dsc.om.SourceRecord;

/**
 * Creates a base InGrid Detail data Format (IDF) skeleton.
 * 
 * @author joachim@wemove.com
 * 
 */
public class CreateIdfMapper implements IIdfMapper {

    protected static final Logger log = Logger.getLogger(CreateIdfMapper.class);

    @Override
    public void map(SourceRecord record, Document doc) throws Exception {
        Element html = doc.createElementNS("http://www.portalu.de/IDF/1.0",
                "html");
        doc.appendChild(html);
        Element head = doc.createElementNS("http://www.portalu.de/IDF/1.0",
                "head");
        html.appendChild(head);
        Element body = doc.createElementNS("http://www.portalu.de/IDF/1.0",
                "body");
        html.appendChild(body);
    }

}
