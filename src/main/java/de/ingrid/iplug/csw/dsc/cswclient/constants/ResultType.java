/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.csw.dsc.cswclient.constants;

public enum ResultType {
	HITS {
		public String toString() {
			return "hits";
		}
	},
	RESULTS {
		public String toString() {
			return "results";
		}
	},
	VALIDATE {
		public String toString() {
			return "validate";
		}
	}
}
