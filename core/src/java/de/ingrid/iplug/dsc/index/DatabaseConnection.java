/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.iplug.dsc.index;

import de.ingrid.utils.IDataSourceConnection;

/**
 * Class for creating a database connection.
 */
public class DatabaseConnection implements IDataSourceConnection{

    private static final long serialVersionUID = DatabaseConnection.class.getName().hashCode();

    private String fDriver;

    private String fConnectionurl;

    private String fUser;

    private String fPassword;

	private String fSchema;
    
    /**
     * @param driver
     * @param connectionurl
     * @param user
     * @param password
     * @param schema
     */
    public DatabaseConnection(String driver, String connectionurl, String user, String password, String schema) {
        this.fDriver = driver;
        this.fConnectionurl = connectionurl;
        this.fUser = user;
        this.fPassword = password;
        this.fSchema = schema;
    }

    /**
     * Returns the database driver.
     * @return The driver.
     */
    public String getDataBaseDriver() {
        return this.fDriver;
    }

    /**
     * Returns the database connection url.
     * @return The connection URL.
     */
    public String getConnectionURL() {
        return this.fConnectionurl;
    }

    /**
     * Returns the database user.
     * @return The user.
     */
    public String getUser() {
        return this.fUser;
    }

    /**
     * Returns the database password for the given user.
     * @return The password.
     */
    public String getPassword() {
        return this.fPassword;
    }

    /**
     * Returns the database schema.
     * @return The schema.
     */
    public String getSchema() {
		return this.fSchema;
	}
}
