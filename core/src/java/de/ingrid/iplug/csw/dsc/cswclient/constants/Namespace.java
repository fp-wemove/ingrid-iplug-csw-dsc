/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.csw.dsc.cswclient.constants;

import javax.xml.namespace.QName;

public enum Namespace {
	
	CSW			{ public QName getQName() { return nsCSW; } },
	CSW_2_0_2	{ public QName getQName() { return nsCSW_2_0_2; } },
	OWS			{ public QName getQName() { return nsOWS; } },
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
	private static final QName nsCSW_2_0_2 = new QName(nsCSW+"/2.0.2", "", nsCSW.getPrefix());
	private static final QName nsOWS = new QName("http://www.opengis.net/ows", "", "ows");
	private static final QName nsGMD = new QName("http://www.isotc211.org/2005/gmd", "", "gmd");
	
	
	private static final QName nsCSW_PROFILE = new QName(nsCSW.getNamespaceURI(), "profile", nsCSW.getPrefix());
	private static final QName nsCSW_RECORD = new QName(nsCSW.getNamespaceURI(), "Record", nsCSW.getPrefix());
	private static final QName nsCSW_DATASET = new QName(nsCSW.getNamespaceURI(), "dataset", nsCSW.getPrefix());

	private static final QName nsISO_METADATA = new QName(nsGMD.getNamespaceURI(), "MD_Metadata", nsGMD.getPrefix());
}
