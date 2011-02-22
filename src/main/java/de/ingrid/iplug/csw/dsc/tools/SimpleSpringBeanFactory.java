/**
 * 
 */
package de.ingrid.iplug.csw.dsc.tools;

import de.ingrid.utils.tool.SpringUtil;

/**
 * @author joachim@wemove.com
 *
 */
public enum SimpleSpringBeanFactory {

	// Guaranteed to be the single instance  
	INSTANCE;
	
	SpringUtil util = null;
	
	public <T> T getBean(String beanName, Class<T> clazz) {
		if (util == null) {
			util = new SpringUtil("spring/spring.xml");
		}
		return util.getBean(beanName, clazz);
    }
	
	public void setBeanConfig(String beanConfigFile) {
		util = new SpringUtil(beanConfigFile);
	}
	
}
