/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgudttojava;

import com.impossibl.postgres.jdbc.PGConnectionImpl;
import com.impossibl.postgres.types.CompositeType;
import com.impossibl.postgres.types.Registry;
import com.impossibl.postgres.types.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author neil
 */
public class PgSQLImpl implements PgSQLInterface {
    private static Connection connection; 
    private static final PgSQLImpl singleton = new PgSQLImpl();
    private static PGConnectionImpl pgConn;
    private static Registry registry;
    public static String server;
    public static String port; 
    public static String database;
    public static String userName;
    public static String password; 
    
    private PgSQLImpl()
    { 
        port="5432";//initialise this to the default value
        
    }
    public static PgSQLImpl getInstance( ) {
      return singleton;
    }
    @Override
    public  void ConnectToDatabase() throws Exception {
        if(server==null||server.equalsIgnoreCase("")){throw new Exception("No Host Name For Server Specified.");}
        if(port==null||port.equalsIgnoreCase(""))
        {
            port="5432";
            Logger.getLogger(PgSQLImpl.class.getName()).log(Level.INFO, "No port specified defaulting to 5432");
        }
        if(database==null||database.equalsIgnoreCase("")){throw new Exception("No database specified.");}
        if(userName==null||userName.equalsIgnoreCase("")){throw new Exception("No user id/user name specified.");}
        if(password==null||password.equalsIgnoreCase("")){throw new Exception("No password specified.");}
  
        String url ="jdbc:pgsql://" + server + ":" + port + "/" + database;
        
        if(connection!=null && !connection.isClosed()){
            return;
        }
        
        try 
        {
            connection  = DriverManager.getConnection(url, userName, password);
            pgConn = (PGConnectionImpl) connection;
            registry = pgConn.getRegistry();
            if (connection instanceof PGConnectionImpl == false) 
            {
                throw new IllegalStateException("Postgres driver must be PGJDBC-NG");
            }
        }
        catch(SQLException | IllegalStateException ex)
        {
            throw ex;
        }
    }



    @Override
    public Type GetTypeFromRegistry(String typeName) throws Exception {
        try
        {
            Type type = registry.loadType(typeName);
            if(type== null){ throw new Exception("Could not find the type: "+typeName+" in the registry");}
            return type;
            
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }

    @Override
    public void CloseConnection() throws SQLException {
        try {
            if(connection.isClosed()){return;}
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(PgSQLImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    @Override
    public CompositeType  GetCompositeTypeFromRegistry(String typeName) throws Exception
    {
          
        Type type = GetTypeFromRegistry(typeName);
        if( type instanceof CompositeType == false){throw new Exception("Type is not a composite type.");}
        return (CompositeType) type;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void SetDatabaseConnectionData(PgSQLConnectionData pgSQLConnectionData) throws Exception {
       server   = pgSQLConnectionData.getServer();
       port     = pgSQLConnectionData.getPort();
       database = pgSQLConnectionData.getDatabase();
       userName = pgSQLConnectionData.getUsername();
       password = pgSQLConnectionData.getPassword();
    }
   

}
