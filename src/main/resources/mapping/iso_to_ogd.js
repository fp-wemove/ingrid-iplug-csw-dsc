/*
 * **************************************************-
 * ingrid-iplug-csw-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
 * CSW 2.0.2 AP ISO 1.0 Record (full) to OGD to Lucene Document mapping
 *
 * The following global variable are passed from the application:
 *
 * @param cswRecord A CSWRecord instance, that defines the input
 * @param document A lucene Document instance, that defines the output
 * @param log A Log instance
 * @param XPathUtils utility extracting data from CSW
 * @param IDX utility for adding data to index
 *
 */
if (javaVersion.indexOf("1.8") === 0) {
    load("nashorn:mozilla_compat.js");
}

if (log.isDebugEnabled()) {
    log.debug("Mapping csw record " + cswRecord.getId() + " to ogd to lucene document");
}

// get the xml content of the record
var recordNode = cswRecord.getOriginalResponse();

var isService = true;
var identification = "srv:SV_ServiceIdentification";
if (XPathUtils.getNodeList(recordNode, "//gmd:MD_DataIdentification").length > 0) {
    identification = "gmd:MD_DataIdentification";
    isService = false;
}

//codelist Mapping
var dateRoleMap = {
    'creation': 'erstellt',
    'publication': 'veroeffentlicht',
    'revision': 'aktualisiert'
};
var languageMap = {
    'ger': 'de'
};
var contactRoleMap = {
    'custodian': 'autor',
    'pointOfContact': 'vertrieb',
    'principalInvestigator': 'autor',
    'processor': 'autor'
};

// consts, ADAPT TO PROVIDER
var author = "FIXED AUTHOR";
var author_email = "FIXED AUTHOR EMAIL";

// useful values
var languageCSW = getFirstValueFromXPath(recordNode, "//" + identification + "/gmd:language/gmd:LanguageCode/@codeListValue");
var languageOGD = mapKeyValue(languageCSW, languageMap);

// xpath selectors
var selectorMaintainer = "@codeListValue='custodian' or @codeListValue='pointOfContact' or @codeListValue='principalInvestigator' or @codeListValue='processor'"
  

var jsonTransformationDescriptions = [{
        "jsonPath": "name",
        "xpath": "//" + identification + "/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"
    }, {
        "jsonPath": "title",
        "xpath": "//" + identification + "/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"
    }, {
        "jsonPath": "author",
        "fixed": author
    }, {
        "jsonPath": "author_email",
        "fixed": author_email
    }, {
        "jsonPath": "maintainer",
        // use maintainer selector only selecting special roles
        "withParent": "//gmd:identificationInfo/" + identification + "/gmd:pointOfContact/idf:idfResponsibleParty/gmd:role/gmd:CI_RoleCode[" + selectorMaintainer + "]/../..",
        "xpath": "./gmd:organisationName/gco:CharacterString"
    }, {
        "jsonPath": "maintainer_email",
        // use maintainer selector only selecting special roles
        "withParent": "//gmd:identificationInfo/" + identification + "/gmd:pointOfContact/idf:idfResponsibleParty/gmd:role/gmd:CI_RoleCode[" + selectorMaintainer + "]/../..",
        "xpath": "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString"
    }, {
        "jsonPath": "notes",
        "xpath": "//gmd:identificationInfo/" + identification + "/gmd:abstract/gco:CharacterString"
    }, {
        "jsonPath": "groups",
        "multiples": true,
        // only keywords with thesaurus OGDD
        "withParent": "//gmd:identificationInfo/" + identification + "/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString[text()='OGDD-Kategorien']/../../../..",
        "xpath": ".//gmd:keyword/gco:CharacterString"
    }, {
        "jsonPath": "tags",
        "multiples": true,
        // all keywords
        "xpath": "//" + identification + "/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString"
    }, {
        "jsonPath": "url",
        "xpath": "//gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:linkage/gmd:URL"
    }, {
        "jsonPath": "type",
        "fixed": isService ? "app" : "datensatz"
    }, {
        "jsonPath": "resources",
        "isResourceField": true,
        "multiples": true,
        "xpath": "//gmd:distributionInfo/gmd:MD_Distribution"
    }, {
        "jsonPath": "license_id",
        "inJsonField": "id",
        "xpath": "//" + identification + "/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString[contains(text(),'\"id\": ')]"
    }, {
        "jsonPath": "extras/contacts",
        "isContactField": true,
        "multiples": true,
        "xpath": "//gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributorContact/idf:idfResponsibleParty"    	
    }, {
        "jsonPath": "extras/dates",
        "isDateField": true,
        "multiples": true,
        "xpath": "//gmd:identificationInfo/" + identification + "/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue"
    }, {
        "jsonPath": "extras/subgroups",
        "multiples": true,
        // all keywords and hierarchyLevelName (according to excel mapping instructions from codematix)
        "xpath": ["//gmd:hierarchyLevelName/gco:CharacterString", "//" + identification + "/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString"]
    }, {
        "jsonPath": "extras/metadata_original_id",
        "xpath": "//gmd:fileIdentifier/gco:CharacterString"
    }, {
        "jsonPath": "extras/spatial",
        "isSpatialField": true,
        "multiples": true,
        "xpath": "//gmd:identificationInfo/" + identification + "/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox"
    }, {
        "jsonPath": "extras/temporal_coverage_from",
        "isTemporalCoverage": true,
        "xpath": "//gmd:identificationInfo/" + identification + "/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:beginPosition"
    }, {
        "jsonPath": "extras/temporal_coverage_to",
        "isTemporalCoverage": true,
        "xpath": "//gmd:identificationInfo/" + identification + "/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:endPosition"
    }, {
        "jsonPath": "ogd_version",
        "fixed": "v1.1"
    }
];

var jsonObject = {};
for (var i in jsonTransformationDescriptions) {
    var t = jsonTransformationDescriptions[i];

    var value = "";
    var valueArray = [];
    var err = false;

    if (t.fixed) {
        value = t.fixed;
    } else {
        if (typeof t.xpath === 'string') {
            t.xpath = [t.xpath];
        }
        var tempNode;
        if (t.withParent) {
            var nodeList = XPathUtils.getNodeList(tempNode, t.withParent);
            if (nodeList && nodeList.getLength() > 0) {
                if (nodeList.getLength() === 1) {
                    tempNode = nodeList.item(0);
                } else {
                    log.debug("Error! More than one Match for Parent xpath: " + t.jsonPath + " " + t.withParent);
                    continue;
                }
            } else {
                log.debug("Error! Parent Xpath not found: " + t.jsonPath + " " + t.withParent);
                continue;
            }
        } else {
            tempNode = recordNode;
        }

        for (var i in t.xpath) {
            var nodeList = XPathUtils.getNodeList(tempNode, t.xpath[i]);
            if (nodeList && nodeList.getLength() > 0) {

                for (var x = 0; x < nodeList.getLength(); x++) {
                    if (t.inJsonField) {
                        value += JSON.parse(nodeList.item(x).getTextContent())[t.inJsonField] + " ";
                    } else if (t.isDateField) {
                        var dateRole = nodeList.item(x).getTextContent();
                        dateRole = mapKeyValue(dateRole, dateRoleMap);
                        var date = XPathUtils.getNode(nodeList.item(x), "./../../../gmd:date/gco:Date").getTextContent();
                        date = date + "T00:00:00";
                        valueArray.push({
                            date: date,
                            role: dateRole
                        });

                    } else if (t.isResourceField) {
                        var url = getFirstValueFromXPath(nodeList.item(x), "./gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:linkage/gmd:URL");
                        var description = getFirstValueFromXPath(nodeList.item(x), "./gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:name/gco:CharacterString");
                        var format = getFirstValueFromXPath(nodeList.item(x), "./gmd:distributionFormat/gmd:MD_Format/gmd:name/gco:CharacterString");

                        if (url) {
                            var resourceObject = {};
                            if (url) resourceObject["url"] = url;
                            if (format) resourceObject["format"] = format;
                            if (description) resourceObject["description"] = description;
                            if (languageOGD) resourceObject["language"] = languageOGD;
                           
                            valueArray.push(resourceObject);                        	
                        }

                    } else if (t.isContactField) {
//                        var role = getFirstValueFromXPath(nodeList.item(x), "./gmd:role/gmd:CI_RoleCode//@codeListValue");
                        var name = getFirstValueFromXPath(nodeList.item(x), "./gmd:organisationName/gco:CharacterString");
                        var url = getFirstValueFromXPath(nodeList.item(x), "./gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL");
                        var email = getFirstValueFromXPath(nodeList.item(x), "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString");
                        var street = getFirstValueFromXPath(nodeList.item(x), "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString");
                        var plz = getFirstValueFromXPath(nodeList.item(x), "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString");
                        var city = getFirstValueFromXPath(nodeList.item(x), "./gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString");
                        
                        var address = null;
                        if (street) address = street;
                        if (address && plz) address += ", " + plz;
                        if (address && city) address += " " + city;
                        
                        var contactObject = {};
                        // this is distributionInfo so it's always 'vertrieb' !
//                        if (role) contactObject["role"] = mapKeyValue(role, contactRoleMap, 'vertrieb');
                        contactObject["role"] = 'vertrieb';
                        if (name) contactObject["name"] = name;
                        if (url) contactObject["url"] = url;
                        if (email) contactObject["email"] = email;
                        if (address) contactObject["address"] = address;

                        valueArray.push(contactObject);

                    } else if (t.isSpatialField) {
                        var west = getFirstValueFromXPath(nodeList.item(x), "./gmd:westBoundLongitude/gco:Decimal");
                        var east = getFirstValueFromXPath(nodeList.item(x), "./gmd:eastBoundLongitude/gco:Decimal");
                        var south = getFirstValueFromXPath(nodeList.item(x), "./gmd:southBoundLatitude/gco:Decimal");
                        var north = getFirstValueFromXPath(nodeList.item(x), "./gmd:northBoundLatitude/gco:Decimal");
                        
                        if (west && east && south && north) {
                        	west = Number(west);
                        	east = Number(east);
                        	south = Number(south);
                        	north = Number(north);
                        	// no array, but does not matter, this one is used when "multiples":true is set !
                        	valueArray = {
                                type: "polygon",
                                coordinates: [ [west,south], [west,north], [east,north], [east,south], [west,south] ]
                            };
                        }
                        
                    } else if (t.isTemporalCoverage) {
                    	value = nodeList.item(x).getTextContent();
                    	if (hasValue(value)) value = value + "T00:00:00";

                    } else if (t.multiples) {
                        valueArray.push(nodeList.item(x).getTextContent());
                    } else {
                        value += nodeList.item(x).getTextContent() + " ";
                    }
                }
                if (nodeList.getLength() !== 1 && !t.multiples) {
                    log.debug("Error! More than one Match for xpath: " + nodeList.getLength() + " " + t.xpath[i]);
                    err = true;
                    break;
                }
            } else {
                log.debug("Error! Xpath not found: " + t.jsonPath + " " + t.xpath[i]);
                err = true;
                break;
            }
        }
        value = value.trim();
        if (t.multiples) {
            value = valueArray;
        }
        if (err) continue;

    }
    
    if (!hasValue(value)) continue;

    var jPath = t.jsonPath.split("/");
    var current = jsonObject;
    for (var j in jPath) {
        var curVar = jPath[j];
        if (j == jPath.length - 1) {
            current[curVar] = value;
        } else {
            if (!current[curVar]) {
                current[curVar] = {};
            }
            current = current[curVar];
        }
    }
}
log.debug("\n\nJSON String:");
log.debug(JSON.stringify(jsonObject, null, 2));

IDX.addAllFromJSON(JSON.stringify(jsonObject));

function getFirstValueFromXPath(node, xpath) {
    var nodeList = XPathUtils.getNodeList(node, xpath);
    if (nodeList && nodeList.getLength() > 0) {
    	return nodeList.item(0).getTextContent();
    }
    return null;
}

function mapKeyValue(myKey, myMap, defaultValue) {
	var myValue = myMap[myKey];
    if (hasValue(myValue)) {
        return myValue;
    }
    if (defaultValue) {
    	return defaultValue;
    }
    return myKey;
}

function hasValue(val) {
	if (typeof val == "undefined") {
		return false; 
	} else if (val == null) {
		return false; 
	} else if (typeof val == "string" && val == "") {
		return false;
	} else if (typeof val == "object" && val.toString() == "") {
		return false;
	} else {
	  return true;
	}
}
