/**
 * 
 */
package de.ingrid.iplug.csw.dsc.tools;

import junit.framework.TestCase;
import de.ingrid.iplug.csw.dsc.ConfigurationKeys;
import de.ingrid.iplug.csw.dsc.cache.Cache;
import de.ingrid.iplug.csw.dsc.cswclient.CSWFactory;
import de.ingrid.iplug.csw.dsc.cswclient.CSWQuery;

/**
 * @author Administrator
 *
 */
public class SimpleSpringBeanFactoryTestLocal extends TestCase {

	/**
	 * Test method for {@link de.ingrid.iplug.csw.dsc.tools.SimpleSpringBeanFactory#getBean(java.lang.String, java.lang.Class)}.
	 */
	public void testGetBean() {
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans_sdisuite.xml");
		assertNotNull(SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.CSW_FACTORY, CSWFactory.class));
		assertNotNull(SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.CSW_QUERY_TEMPLATE, CSWQuery.class));
		assertNotNull(SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.CSW_CACHE, Cache.class));
		assertNotNull(SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.CSW_HARVEST_FILTER, String.class));
	}

}
