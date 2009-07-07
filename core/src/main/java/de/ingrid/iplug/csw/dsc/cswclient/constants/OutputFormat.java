/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.csw.dsc.cswclient.constants;

public enum OutputFormat {
	APPLICATION_XML {
		public String toString() {
			return "application/xml";
		}
	},
	TEXT_XML {
		public String toString() {
			return "text/xml";
		}
	}
}
