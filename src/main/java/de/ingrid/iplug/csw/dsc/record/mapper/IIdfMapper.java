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

import org.w3c.dom.Document;

import de.ingrid.iplug.csw.dsc.om.SourceRecord;

/**
 * Must be implemented by all mapper classes that map a {@link SourceRecord} to
 * an InGrid Detail data Format (IDF).
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public interface IIdfMapper {

    /**
     * Map a {@link SourceRecord} to an InGrid Detail data Format (IDF). The
     * implementing class must take care that all required parameters are
     * present in the {@link SourceRecord}.
     * 
     * @param record
     * @param doc
     * @throws Exception
     */
    public void map(SourceRecord record, Document doc) throws Exception;

}
