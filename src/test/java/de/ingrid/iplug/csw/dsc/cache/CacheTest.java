/*
 * **************************************************-
 * ingrid-iplug-csw-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

package de.ingrid.iplug.csw.dsc.cache;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import junit.framework.TestCase;
import de.ingrid.iplug.csw.dsc.TestUtil;
import de.ingrid.iplug.csw.dsc.cache.impl.DefaultFileCache;
import de.ingrid.iplug.csw.dsc.cswclient.CSWFactory;
import de.ingrid.iplug.csw.dsc.cswclient.CSWRecord;
import de.ingrid.iplug.csw.dsc.cswclient.constants.ElementSetName;
import de.ingrid.iplug.csw.dsc.cswclient.impl.GenericRecord;
import de.ingrid.iplug.csw.dsc.tools.StringUtils;

public class CacheTest extends TestCase {
	
	private final String cachePath = "./test_case_cache";
	private Cache cache = null;

	public void testPut() throws Exception {
		
		String id = "10453eff-59fa-42e9-a3e1-6e3cd99e2a05";
		ElementSetName elementSetName = ElementSetName.FULL;
		
		this.putRecord(id, elementSetName);
		
		DefaultFileCache cache = (DefaultFileCache)this.setupCache();
		File file = new File(cache.getAbsoluteFilename(id, elementSetName));
		assertTrue("The record exists in the filesystem.", file.exists());
	}

	public void testExists() throws Exception {

		String id = "10453eff-59fa-42e9-a3e1-6e3cd99e2a05";
		ElementSetName elementSetName = ElementSetName.FULL;
		
		this.putRecord(id, elementSetName);
		
		Cache cache = this.setupCache();
		assertTrue("The record exists in the cache.", cache.isCached(id, elementSetName));
		assertFalse("The record does not exist in the cache.", cache.isCached("12345", elementSetName));
	}

	public void testGet() throws Exception {

		String id = "10453eff-59fa-42e9-a3e1-6e3cd99e2a05";
		ElementSetName elementSetName = ElementSetName.FULL;
		
		this.putRecord(id, elementSetName);
		
		Cache cache = this.setupCache();
		CSWRecord record = cache.getRecord(id, elementSetName);
		assertTrue("The cached record has the requested id.", id.equals(record.getId()));
	}

	public void testGetIds() throws Exception {

		String[] ids = new String[] {
				"10453eff-59fa-42e9-a3e1-6e3cd99e2a05",
				"33462e89-e5ab-11c3-737d-b3a61366d028"
		};
		
		this.putRecord(ids[0], ElementSetName.FULL);
		this.putRecord(ids[1], ElementSetName.FULL);
		
		Cache cache = this.setupCache();
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue("The first record is cached.", cachedIds.contains(ids[0]));
		assertTrue("The second record is cached.", cachedIds.contains(ids[1]));
		assertFalse("The record is not cached.", cachedIds.contains("12345"));
	}

	public void testRemoveRecord() throws Exception {

		String[] ids = new String[]{
				"10453eff-59fa-42e9-a3e1-6e3cd99e2a05",
				"33462e89-e5ab-11c3-737d-b3a61366d028"
		};
		
		this.putRecord(ids[0], ElementSetName.FULL);
		this.putRecord(ids[1], ElementSetName.FULL);
		
		Cache cache = this.setupCache();
		cache.removeRecord(ids[1]);
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue("The first record is cached.", cachedIds.contains(ids[0]));
		assertTrue("The second record is removed.", !cachedIds.contains(ids[1]));
	}

	public void testEncoding() throws Exception {
		
		String id = "33462e89-e5ab-11c3-737d-b3a61366d028";
		ElementSetName elementSetName = ElementSetName.FULL;
		
		this.putRecord(id, elementSetName);
		
		DefaultFileCache cache = (DefaultFileCache)this.setupCache();
		File file = new File(cache.getAbsoluteFilename(id, elementSetName));
		assertTrue("The record exists in the filesystem.", file.exists());
		assertTrue("The record exists in the cache.", cache.isCached(id, elementSetName));
		
		// test get
		CSWRecord record = cache.getRecord(id, elementSetName);
		assertTrue("The cached record has the requested id.", id.equals(record.getId()));		

		// test get ids
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue("The record is cached.", cachedIds.contains(id));
		
		// test remove
		cache.removeRecord(id);
		assertTrue("The record is removed from the cache.", !cache.isCached(id, elementSetName));
	}

	public void testRemoveAllRecords() throws Exception {

		String[] ids = new String[]{
				"33462e89-e5ab-11c3-737d-b3a61366d028",
				"10453eff-59fa-42e9-a3e1-6e3cd99e2a05"
		};
		
		this.putRecord(ids[0], ElementSetName.FULL);
		this.putRecord(ids[1], ElementSetName.FULL);
		
		Cache cache = this.setupCache();
		cache.removeAllRecords();
		Set<String> cachedIds = cache.getCachedRecordIds();
		assertTrue("No files are cached.", cachedIds.size() == 0);
	}

	public void testTransactionModifyWithCommit() throws Exception {

		String id = "10453eff-59fa-42e9-a3e1-6e3cd99e2a05";
		ElementSetName elementSetName = ElementSetName.FULL;

		Cache cache = this.setupCache();
		
		// create original set
		CSWRecord originalRecord = TestUtil.getRecord(id, elementSetName, new GenericRecord());
		String originalTitle = TestUtil.getRecordTitle(originalRecord);
		cache.putRecord(originalRecord);
		
		// start transaction
		Cache tmpCache = cache.startTransaction();
		CSWRecord modifiedRecord = TestUtil.getRecord(id, elementSetName, new GenericRecord());
		String modifiedTitle = "Modified Title "+System.currentTimeMillis();
		TestUtil.setRecordTitle(modifiedRecord, modifiedTitle);
		tmpCache.putRecord(modifiedRecord);
		
		// get original record while transaction is open
		CSWRecord originalRecordInTransaction = cache.getRecord(id, elementSetName);
		assertTrue("The cached record title has not changed, since the transaction is not committet.", 
				originalTitle.equals(TestUtil.getRecordTitle(originalRecordInTransaction)));
		
		// commit the transaction
		tmpCache.commitTransaction();
		
		// get original record after transaction is committed
		CSWRecord originalRecordAfterTransaction = cache.getRecord(id, elementSetName);
		assertTrue("The cached record title is changed after the transaction is committet.", 
				modifiedTitle.equals(TestUtil.getRecordTitle(originalRecordAfterTransaction)));
		
		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertTrue("The temporary cache is deleted.", !tmpPath.exists());
	}

	public void testTransactionModifyWithRollback() throws Exception {

		String id = "10453eff-59fa-42e9-a3e1-6e3cd99e2a05";
		ElementSetName elementSetName = ElementSetName.FULL;

		Cache cache = this.setupCache();
		
		// create original set
		CSWRecord originalRecord = TestUtil.getRecord(id, elementSetName, new GenericRecord());
		String originalTitle = TestUtil.getRecordTitle(originalRecord);
		cache.putRecord(originalRecord);
		
		// start transaction
		Cache tmpCache = cache.startTransaction();
		CSWRecord modifiedRecord = TestUtil.getRecord(id, elementSetName, new GenericRecord());
		String modifiedTitle = "Modified Title "+System.currentTimeMillis();
		TestUtil.setRecordTitle(modifiedRecord, modifiedTitle);
		tmpCache.putRecord(modifiedRecord);
		
		// get original record while transaction is open
		CSWRecord originalRecordInTransaction = cache.getRecord(id, elementSetName);
		assertTrue("The cached record title has not changed, since the transaction is not committet.", 
				originalTitle.equals(TestUtil.getRecordTitle(originalRecordInTransaction)));
		
		// rollback the transaction
		tmpCache.rollbackTransaction();
		
		// get original record after transaction is committed
		CSWRecord originalRecordAfterTransaction = cache.getRecord(id, elementSetName);
		assertTrue("The cached record title is changed after the transaction is committet.", 
				originalTitle.equals(TestUtil.getRecordTitle(originalRecordAfterTransaction)));
		
		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertTrue("The temporary cache is deleted.", !tmpPath.exists());
	}

	public void testTransactionRemoveWithCommit() throws Exception {

		String id = "10453eff-59fa-42e9-a3e1-6e3cd99e2a05";
		ElementSetName elementSetName = ElementSetName.FULL;

		Cache cache = this.setupCache();
		
		// create original set
		CSWRecord originalRecord = TestUtil.getRecord(id, elementSetName, new GenericRecord());
		cache.putRecord(originalRecord);
		
		// start transaction
		Cache tmpCache = cache.startTransaction();
		tmpCache.removeRecord(id);

		// check if the record is removed from the tmp cache
		assertTrue("The record is deleted from temp cache.", !tmpCache.isCached(id, elementSetName));
		
		// check if the record is still in the original cache
		assertTrue("The cached record still exists, since the transaction is not committet.", cache.isCached(id, elementSetName));
		
		// commit the transaction
		tmpCache.commitTransaction();
		
		// check if the original record is deleted after transaction is committed
		assertTrue("The cached record deleted after the transaction is committet.", !cache.isCached(id, elementSetName));
		
		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertTrue("The temporary cache is deleted.", !tmpPath.exists());
	}

	public void testTransactionRemoveWithRollback() throws Exception {

		String id = "10453eff-59fa-42e9-a3e1-6e3cd99e2a05";
		ElementSetName elementSetName = ElementSetName.FULL;

		Cache cache = this.setupCache();
		
		// create original set
		CSWRecord originalRecord = TestUtil.getRecord(id, elementSetName, new GenericRecord());
		cache.putRecord(originalRecord);
		
		// start transaction
		Cache tmpCache = cache.startTransaction();
		tmpCache.removeRecord(id);
		
		// check if the record is removed from the tmp cache
		assertTrue("The record is deleted from temp cache.", !tmpCache.isCached(id, elementSetName));
		
		// check if the record is still in the original cache
		assertTrue("The cached record still exists, since the transaction is not committet.", cache.isCached(id, elementSetName));
		
		// rollback the transaction
		tmpCache.rollbackTransaction();
		
		// check if the original record is deleted after transaction is committed
		assertTrue("The cached record still exists after the transaction is committet.", cache.isCached(id, elementSetName));
		
		// check if the cache temporary cache is deleted
		File tmpPath = new File(((DefaultFileCache)tmpCache).getTempPath());
		assertTrue("The temporary cache is deleted.", !tmpPath.exists());
	}

	public void testLastCommitDate() throws Exception {

		Cache cache = this.setupCache();
		
		// start transaction
		Cache tmpCache = cache.startTransaction();
		
		// commit the transaction
		tmpCache.commitTransaction();
		
		// check if commit date is today
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		assertTrue("The comit date is today.", df.format(cache.getLastCommitDate()).equals(df.format(new Date())));
	}

	public void testInitialCache() throws Exception {

		Cache cache = this.setupCache();
		
		// start transaction
		Cache tmpCache = cache.startTransaction();
		
		// check if the tmp cache instance is not the initial cache instance
		assertTrue("The temp cache is not the initial cache.", tmpCache != cache);
		
		// check if the tmp cache's initial instance is the initial cache instance
		assertTrue("The temp cache's initial instance is the initial cache.", tmpCache.getInitialCache() == cache);
		
		// rollback the transaction
		tmpCache.rollbackTransaction();
	}
	
	public void testGetOriginalResponse() throws Exception {
		String id = "10453eff-59fa-42e9-a3e1-6e3cd99e2a05";
		ElementSetName elementSetName = ElementSetName.FULL;

		// create original set
		CSWRecord originalRecord = TestUtil.getRecord(id, elementSetName, new GenericRecord());
		String xml = StringUtils.nodeToString(originalRecord.getOriginalResponse());
		assertTrue("The String 'Hydrological basins in Europe' is in the transformed original response string.", xml.indexOf("Hydrological basins in Europe") > -1);
		
	}

	/**
	 * Helper methods
	 */

	protected void tearDown() {
		// delete cache
		TestUtil.deleteDirectory(new File(cachePath));
	}
	
	private Cache setupCache() {
		if (this.cache == null) {
			CSWFactory factory = new CSWFactory();
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
