package com.namphibian.pgudttojava;

/**
 *
 * @author Neil Franken
 * 
 */
public class PgSQLConnectionData {
    private String server;
    private String port;
    private String database;
    private String username;
    private String password;

    public PgSQLConnectionData(){}
    public PgSQLConnectionData(String server, String port,String database, String username , String password)
    {
        this.server   = server;
        this.port     = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }
    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
