/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgudttojava;

import com.google.common.reflect.TypeToken;
import com.impossibl.postgres.types.CompositeType;
import com.impossibl.postgres.types.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author neil
 */
public interface PgSQLInterface {
 
    public Connection getConnection();
  
    public  void  ConnectToDatabase() throws Exception;
 
    public void  CloseConnection() throws SQLException;
     /**
     *
     * @param typeName
     * @return
     * @throws Exception
     */
    public Type GetTypeFromRegistry(String typeName) throws Exception;

    /**
     *
     * @param typeName
     * @return
     * @throws Exception
     */
    public CompositeType GetCompositeTypeFromRegistry(String typeName) throws Exception;
    //public <T>List<TypeToken<T>> GetListFromRecordSet(String SQL,  TypeToken<T> typeToken) throws Exception;
    public void SetDatabaseConnectionData(PgSQLConnectionData pgSQLConnectionData) throws Exception;
}