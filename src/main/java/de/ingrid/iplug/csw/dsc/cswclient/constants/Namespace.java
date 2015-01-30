/*
 * **************************************************-
 * ingrid-iplug-csw-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.csw.dsc.cswclient.constants;

import javax.xml.namespace.QName;

public enum Namespace {
	
	CSW			{ public QName getQName() { return nsCSW; } },
	CSW_2_0_2	{ public QName getQName() { return nsCSW_2_0_2; } },
	OWS			{ public QName getQName() { return nsOWS; } },
	ISO			{ public QName getQName() { return nsISO; } },
	GML			{ public QName getQName() { return nsGML; } },
	GMD			{ public QName getQName() { return nsGMD; } },
	
	CSW_PROFILE	{ public QName getQName() { return nsCSW_PROFILE; } },
	CSW_RECORD		{ public QName getQName() { return nsCSW_RECORD; } },
	CSW_DATASET 	{ public QName getQName() { return nsCSW_DATASET; } },

	ISO_METADATA	{ public QName getQName() { return nsISO_METADATA; } };

	/**
	 * Get the QName of a namespace constant
	 * @return
	 */
	public abstract QName getQName();

	/**
	 * namespaces
	 */
	private static final QName nsCSW = new QName("http://www.opengis.net/cat/csw", "", "csw");
	private static final QName nsCSW_2_0_2 = new QName(nsCSW.getNamespaceURI()+"/2.0.2", "", nsCSW.getPrefix());
	private static final QName nsOWS = new QName("http://www.opengis.net/ows", "", "ows");
	private static final QName nsISO = new QName("http://www.opengis.net/cat/csw/apiso/1.0", "", "iso");
	private static final QName nsGML = new QName("http://www.opengis.net/gml", "", "gml");
	private static final QName nsGMD = new QName("http://www.isotc211.org/2005/gmd", "", "gmd");
	
	
	private static final QName nsCSW_PROFILE = new QName(nsCSW.getNamespaceURI(), "profile", nsCSW.getPrefix());
	private static final QName nsCSW_RECORD = new QName(nsCSW.getNamespaceURI(), "Record", nsCSW.getPrefix());
	private static final QName nsCSW_DATASET = new QName(nsCSW.getNamespaceURI(), "dataset", nsCSW.getPrefix());

	private static final QName nsISO_METADATA = new QName(nsGMD.getNamespaceURI(), "MD_Metadata", nsGMD.getPrefix());
}
