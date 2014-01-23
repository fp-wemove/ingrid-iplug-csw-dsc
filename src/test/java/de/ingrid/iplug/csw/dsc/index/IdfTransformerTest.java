package de.ingrid.iplug.csw.dsc.index;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.core.io.FileSystemResource;
import org.w3c.dom.Node;

import de.ingrid.iplug.csw.dsc.TestUtil;
import de.ingrid.iplug.csw.dsc.analyze.IsoCacheAnalyzer;
import de.ingrid.iplug.csw.dsc.cache.Cache;
import de.ingrid.iplug.csw.dsc.cache.IdfTransformer;
import de.ingrid.iplug.csw.dsc.cache.impl.DefaultFileCache;
import de.ingrid.iplug.csw.dsc.cswclient.CSWFactory;
import de.ingrid.iplug.csw.dsc.cswclient.CSWRecord;
import de.ingrid.iplug.csw.dsc.cswclient.constants.ElementSetName;
import de.ingrid.iplug.csw.dsc.cswclient.impl.GenericRecord;
import de.ingrid.iplug.csw.dsc.record.mapper.CouplingResourcesMapper;
import de.ingrid.iplug.csw.dsc.record.mapper.CreateIdfMapper;
import de.ingrid.iplug.csw.dsc.record.mapper.CswIdfMapper;
import de.ingrid.iplug.csw.dsc.record.mapper.IIdfMapper;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class IdfTransformerTest extends TestCase {

    final private XPathUtils xPathUtils = new XPathUtils(new IDFNamespaceContext());

    private final String cachePath = "./test_case_cache";
    private Cache cache = null;
    private CSWFactory factory = null;

    @Override
    protected void tearDown() throws Exception {
        // delete cache
        TestUtil.deleteDirectory(new File(cachePath));
    }

    /**
     * @throws Exception
     */
    public void testTransform() throws Exception {

        IsoCacheAnalyzer isoCacheAnalyzer = new IsoCacheAnalyzer();

        List<IIdfMapper> record2IdfMapperList = new ArrayList<IIdfMapper>();
        record2IdfMapperList.add(new CreateIdfMapper());
        CswIdfMapper cswIdfMapper = new CswIdfMapper();
        cswIdfMapper.setStyleSheetResource(new FileSystemResource("src/main/resources/mapping/iso_to_idf.xsl"));
        record2IdfMapperList.add(cswIdfMapper);
        record2IdfMapperList.add(new CouplingResourcesMapper());

        IdfTransformer idfTransformer = new IdfTransformer();
        idfTransformer.setIsoCacheAnalyzer(isoCacheAnalyzer);
        idfTransformer.setRecord2IdfMapperList(record2IdfMapperList);

        String[] ids = new String[] { "33462e89-e5ab-11c3-737d-b3a61366d028", "0C12204F-5626-4A2E-94F4-514424F093A1", "486d9622-c29d-44e5-b878-44389740011", "77793F43-707A-4346-9A24-9F4E22213F54", "CF902C59-D50B-42F6-ADE4-F3CEC39A3259",
                "CFA384AB-028F-476B-AC95-EB75CCEFB296" };

        for (String id : ids) {
            this.putRecord(id, ElementSetName.FULL);
        }

        Cache cache = this.setupCache();

        idfTransformer.transform(cache);

        CSWRecord record = cache.getRecord("CF902C59-D50B-42F6-ADE4-F3CEC39A3259", ElementSetName.IDF);
        Node n = record.getOriginalResponse();
        assertNotNull("Dataset IDF record CF902C59-D50B-42F6-ADE4-F3CEC39A3259 exists in cache.", record);
        assertEquals("Dataset IDF record CF902C59-D50B-42F6-ADE4-F3CEC39A3259 has reference to service CFA384AB-028F-476B-AC95-EB75CCEFB296.", "CFA384AB-028F-476B-AC95-EB75CCEFB296",
                xPathUtils.getString(n, "//idf:idfMdMetadata/idf:crossReference/@uuid"));

        record = cache.getRecord("0C12204F-5626-4A2E-94F4-514424F093A1", ElementSetName.IDF);
        n = record.getOriginalResponse();
        assertNotNull("Dataset IDF record 0C12204F-5626-4A2E-94F4-514424F093A1 exists in cache.", record);
        assertEquals("Dataset IDF record 0C12204F-5626-4A2E-94F4-514424F093A1 has reference to service 77793F43-707A-4346-9A24-9F4E22213F54.", "77793F43-707A-4346-9A24-9F4E22213F54",
                xPathUtils.getString(n, "//idf:idfMdMetadata/idf:crossReference/@uuid"));

        record = cache.getRecord("486d9622-c29d-44e5-b878-44389740011", ElementSetName.IDF);
        n = record.getOriginalResponse();
        assertNotNull("Dataset IDF record 486d9622-c29d-44e5-b878-44389740011 exists in cache.", record);
        assertEquals("Dataset IDF record 486d9622-c29d-44e5-b878-44389740011 has reference to service 77793F43-707A-4346-9A24-9F4E22213F54.", "77793F43-707A-4346-9A24-9F4E22213F54",
                xPathUtils.getString(n, "//idf:idfMdMetadata/idf:crossReference/@uuid"));
        
        
    }

    private Cache setupCache() {
        if (this.cache == null) {
            factory = new CSWFactory();
            factory.setRecordImpl("de.ingrid.iplug.csw.dsc.cswclient.impl.GenericRecord");
            DefaultFileCache cache = new DefaultFileCache();
            cache.configure(factory);
            cache.setCachePath(cachePath);
            this.cache = cache;
        }
        return this.cache;
    }

    private void putRecord(String id, ElementSetName elementSetName) throws Exception {
        Cache cache = this.setupCache();
        CSWRecord record = TestUtil.getRecord(id, elementSetName, new GenericRecord());
        cache.putRecord(record);
    }

}
