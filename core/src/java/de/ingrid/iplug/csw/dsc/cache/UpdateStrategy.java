/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.csw.dsc.cache;

import java.util.List;

import org.apache.commons.logging.Log;

public interface UpdateStrategy {

	/**
	 * Execute the update strategy.
	 * @param context The ExecutionContext
	 * @return The list of ids that exist on the server
	 * @throws Exception
	 */
	public abstract List<String> execute(ExecutionContext context) throws Exception;

	/**
	 * Get the ExecutionContext instance.
	 * @return The execution context
	 */
	public abstract ExecutionContext getExecutionContext();

	/**
	 * Get the Log instance.
	 * @return The log
	 */
	public abstract Log getLog();
}