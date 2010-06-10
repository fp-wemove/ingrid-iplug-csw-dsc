/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.csw.dsc.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.iplug.csw.dsc.ConfigurationKeys;
import de.ingrid.iplug.csw.dsc.cswclient.CSWFactory;
import de.ingrid.iplug.csw.dsc.tools.SimpleSpringBeanFactory;
import de.ingrid.utils.PlugDescription;

/**
 * The update job.
 * @author ingo herwig <ingo@wemove.com>
 */
public class UpdateJob {

	final protected static Log log = LogFactory.getLog(UpdateJob.class);
	final private static String DATE_FILENAME = "updatejob.dat";
	final private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private CSWFactory factory;
	private Cache cache;
	private Set<String> filterStrSet;
	private UpdateStrategy updateStrategy;

	/**
	 * Constructor
	 * @param factory The CSWFactory instance
	 * @param cache The Cache instance (UpdateJob assumes that the Cache is an transaction already)
	 */
	@SuppressWarnings({"unchecked"})
	public UpdateJob(CSWFactory factory, Cache cache) {

		// get filter set from configuration 
		Set<String> filterStrSet = (Set<String>)SimpleSpringBeanFactory.INSTANCE.getBean(
				ConfigurationKeys.CSW_HARVEST_FILTER, Set.class);

		// get strategy set from configuration
		// NOTICE: PlugDescription WILL BE RELOADED (due to scope="prototype" in spring XML configuration file) !!!!!
		PlugDescription plugDescription = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.PLUGDESCRIPTION, 
				PlugDescription.class);
		// ALSO SET RELOADED PD IN FACTORY SO WE HAVE THE LATEST SETTINGS (e.g. when URL was changed and reindexing in admin gui)
		factory.setPlugDescription(plugDescription);

		Map strategies = SimpleSpringBeanFactory.INSTANCE.getBean(ConfigurationKeys.CSW_UPDATE_STRATEGIES, Map.class);
		String updateStrategy = plugDescription.getString("updateStrategy");
		String beanId = (String)strategies.get(updateStrategy);
		if (beanId == null)
			throw new RuntimeException("Unknown value for 'updateStrategy' in PlugDescription: "+updateStrategy);
		
		UpdateStrategy strategy = SimpleSpringBeanFactory.INSTANCE.getBean(beanId, UpdateStrategy.class);

		this.factory = factory;
		this.cache = cache;
		this.filterStrSet = filterStrSet;
		this.updateStrategy = strategy;
	}
	
	/**
	 * Execute the update job.
	 * @throws Exception
	 */
	public void execute() throws Exception {
		Date start = new Date();

		// get cached record ids (for later removal of records that do not exist anymore)
		Set<String> cachedRecordIds = this.cache.getCachedRecordIds();
		
		Date lastExecutionDate = this.getLastExecutionDate();
		log.info("Starting update job using strategy '"+this.updateStrategy.getClass().getSimpleName()+"'");
		log.info("Last execution was on "+DATEFORMAT.format(lastExecutionDate));
		
		// create the execution context
		ExecutionContext ctx = new ExecutionContext();
		ctx.setFactory(this.factory);
		ctx.setCache(this.cache);
		ctx.setFilterStrSet(this.filterStrSet);
		ctx.setLastExecutionDate(lastExecutionDate);
		
		// delegate execution to the strategy
		List<String> allRecordIds = updateStrategy.execute(ctx);

		// remove deprecated records
		for (String cachedRecordId : cachedRecordIds) {
			if (!allRecordIds.contains(cachedRecordId))
				this.cache.removeRecord(cachedRecordId);
		}

		// write the execution date as last operation
		// this is the start date, to make sure that the next execution will fetch
		// all modified records from the job execution start on
		this.writeLastExecutionDate(start);
		
		// duplicates are filtered out automatically by the cache, so there is no need for action here
		int duplicates = allRecordIds.size() - new HashSet<String>(allRecordIds).size();

		// summary
		Date end = new Date();
		long diff = end.getTime()-start.getTime();
		log.info("Fetched "+allRecordIds.size()+" records of "+allRecordIds.size()+". Duplicates: "+duplicates);
		log.info("Job executed within "+diff+" ms.");
	}
	
	/**
	 * Get the last exectution date of this job. Returns 1970-01-01 00:00:00 if an error occurs.
	 * @return Date
	 */
	public Date getLastExecutionDate() {
		File dateFile = new File(DATE_FILENAME);
		
		try {
			BufferedReader input = new BufferedReader(new FileReader(dateFile));
			// we expect only one line with the date string
			String line = input.readLine();
			if (line != null) {
				Date date = DATEFORMAT.parse(line.trim());
				return date;
			}
		}
		catch (Exception e) {
			log.warn("Could not read from "+DATE_FILENAME+". " +
					"The update job fetches all records.");
		}
		// return the minimum date if no date could be found
		return new Date(0);
	}

	/**
	 * Write the last exectution date of this job.
	 * @param Date
	 */
	public void writeLastExecutionDate(Date date) {
		File dateFile = new File(DATE_FILENAME);

		try {
			Writer output = new BufferedWriter(new FileWriter(dateFile));
		    output.write(DATEFORMAT.format(date));
		    output.close();
		}
		catch (Exception e) {
			// delete the date file to make sure we do not use a corrupted version
			if (dateFile.exists())
				dateFile.delete();
			log.warn("Could not write to "+DATE_FILENAME+". " +
					"The update job fetches all records next time.");
		}
	}
}
