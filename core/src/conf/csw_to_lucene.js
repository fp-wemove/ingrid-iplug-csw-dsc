/**
 * CSW Record to Lucene Document mapping
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 *
 * The following global variable are passed from the application:
 *
 * @param cswRecord A CSWRecord instance, that defines the input
 * @param document A lucene Document instance, that defines the output
 * @param log A Log instance
 *
 */
importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.iplug.csw.dsc.tools);

log.debug("Mapping csw record "+cswRecord.getId()+" to lucene document\n");

// get the xml content of the record
var recordNode = cswRecord.getOriginalResponse();

// attribute extraction example
var id = XPathUtils.getString(recordNode, "//fileIdentifier/CharacterString")
log.debug("Extracted id: "+id+"\n");

// code below is copied from DummyDocumentReader as demonstration 
// (satisfies IndexesTest.testIndexer())
var _counter = 1;
var _store = true;
var _index = true;
var _token = true;

document.add(new Field("datatype", "default", !_store, _index, !_token));
document.add(new Field("title", "title " + _counter, _store, _index, _token));
document.add(new Field("content", "content " + _counter, _store, _index, _token));
document.add(new Field("url", "url " + _counter, _store, _index, _token));
