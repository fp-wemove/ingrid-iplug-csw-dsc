/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.csw.dsc.cswclient.constants;

public enum ConstraintLanguage {
	CQL_TEXT {
		public String toString() {
			return "CQL_TEXT";
		}
	},
	FILTER {
		public String toString() {
			return "FILTER";
		}
	}
}
