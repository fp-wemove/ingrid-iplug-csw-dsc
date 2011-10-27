/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.csw.dsc.cswclient.impl;

import org.w3c.dom.Comment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.iplug.csw.dsc.cswclient.CSWRecord;
import de.ingrid.iplug.csw.dsc.cswclient.constants.ElementSetName;
import de.ingrid.iplug.csw.dsc.tools.NodeUtils;
import de.ingrid.iplug.csw.dsc.tools.StringUtils;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class GenericRecord implements CSWRecord {

    final private XPathUtils xPathUtils = new XPathUtils(new Csw202NamespaceContext());

    protected String id = null;
    protected ElementSetName elementSetName = null;
    protected Node node = null;

    /**
     * Initializes the record. The node will be detached (cloned) from it's
     * owner document.
     * 
     * @param elementSetName
     *            The {@link ElementSetName} of this record.
     * @param node
     *            The DOM Node describing the record. The node will be detached
     *            (cloned).
     * 
     * @see de.ingrid.iplug.csw.dsc.cswclient.CSWRecord#initialize(de.ingrid.iplug.csw.dsc.cswclient.constants.ElementSetName,
     *      org.w3c.dom.Node)
     */
    @Override
    public void initialize(ElementSetName elementSetName, Node node) throws Exception {
        // detach node from whole document inkl. all namespace definitions
        while (node instanceof Comment) {
            node = node.getNextSibling();
        }
        this.node = NodeUtils.detachWithNameSpaces(node);
        this.elementSetName = elementSetName;

        // get the record id
        NodeList idNodes = xPathUtils
                .getNodeList(this.node, "//gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString");
        if (idNodes == null || idNodes.item(0) == null)
            throw new RuntimeException(
                    "CSWRecord does not contain an id (looking for //gmd:fileIdentifier/gco:CharacterString):\n"
                            + StringUtils.nodeToString(this.node));
        if (idNodes.getLength() > 1)
            throw new RuntimeException(
                    "CSWRecord contains more than one id (looking for //gmd:fileIdentifier/gco:CharacterString):\n"
                            + StringUtils.nodeToString(this.node));

        this.id = idNodes.item(0).getTextContent();
    }

    @Override
    public String getId() {
        if (this.id != null) {
            return this.id;
        } else
            throw new RuntimeException("CSWRecord is not initialized properly. Make sure to call CSWRecord.initialize.");
    }

    @Override
    public ElementSetName getElementSetName() {
        if (this.elementSetName != null) {
            return this.elementSetName;
        } else
            throw new RuntimeException("CSWRecord is not initialized properly. Make sure to call CSWRecord.initialize.");
    }

    @Override
    public Node getOriginalResponse() {
        if (this.node != null) {
            return this.node;
        } else
            throw new RuntimeException("CSWRecord is not initialized properly. Make sure to call CSWRecord.initialize.");
    }
}
