/**
 * CSW Record to Ingrid Record mapping
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 *
 * The following global variable are passed from the application:
 *
 * @param cswRecord A CSWRecord instance, that defines the input
 * @param document A ingrid Record instance, that defines the output
 * @param log A Log instance
 *
 */
importPackage(Packages.de.ingrid.iplug.csw.dsc.tools);

log.debug("Mapping csw record "+cswRecord.getId()+" to lucene document\n");

// get the xml content of the record
var recordNode = cswRecord.getOriginalResponse();

// attribute extraction example
var id = XPathUtils.getString(recordNode, "//fileIdentifier/CharacterString")
log.debug("Extracted id: "+id+"\n");
