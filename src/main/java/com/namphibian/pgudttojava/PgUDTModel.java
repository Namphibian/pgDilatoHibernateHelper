/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgudttojava;


import com.impossibl.postgres.types.CompositeType;
import com.impossibl.postgres.types.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author neil
 */
public class PgUDTModel implements PgUDTModelInterface  {
    private final PgSQLInterface pgSQLInterface;
    private final static String SQL_UDT="SELECT n.nspname AS schema,\n" +
            "        pg_catalog.format_type ( t.oid, NULL ) AS name,\n" +
            "        t.typname AS internal_name,\n" +
            "        CASE\n" +
            "            WHEN t.typrelid != 0\n" +
            "            THEN CAST ( 'Record/UDT' AS pg_catalog.text )\n" +
            "            WHEN t.typlen < 0\n" +
            "            THEN CAST ( 'Variable Length' AS pg_catalog.text )\n" +
            "            ELSE CAST ( t.typlen AS pg_catalog.text )\n" +
            "        END AS size,\n" +
            "        pg_catalog.array_to_string (\n" +
            "            ARRAY( SELECT e.enumlabel\n" +
            "                    FROM pg_catalog.pg_enum e\n" +
            "                    WHERE e.enumtypid = t.oid\n" +
            "                    ORDER BY e.oid ), E'\\n'\n" +
            "            ) AS elements,\n" +
            "        pg_catalog.obj_description ( t.oid, 'pg_type' ) AS description\n" +
            "        ,*\n" +
            "    FROM pg_catalog.pg_type t\n" +
            "    LEFT JOIN pg_catalog.pg_namespace n\n" +
            "        ON n.oid = t.typnamespace\n" +
            "    WHERE ( t.typrelid = 0\n" +
            "            OR ( SELECT c.relkind = 'c'\n" +
            "                    FROM pg_catalog.pg_class c\n" +
            "                    WHERE c.oid = t.typrelid\n" +
            "                )\n" +
            "        )\n" +
            "        AND NOT EXISTS\n" +
            "            ( SELECT 1\n" +
            "                FROM pg_catalog.pg_type el\n" +
            "                WHERE el.oid = t.typelem\n" +
            "                    AND el.typarray = t.oid\n" +
            "            )\n" +
            "        AND n.nspname <> 'pg_catalog'\n" +
            "        AND n.nspname <> 'information_schema'\n" +
            "        AND pg_catalog.pg_type_is_visible ( t.oid )\n" +
            "    ORDER BY 1, 2;";

    public PgUDTModel(PgSQLConnectionData connectionData) throws Exception
    {
        pgSQLInterface= PgSQLImpl.getInstance();
        pgSQLInterface.SetDatabaseConnectionData(connectionData);
    }
    @Override
    public List<PgUserDefinedType> GetUDTList() 
    {
        try {
            pgSQLInterface.ConnectToDatabase();
        } catch (Exception ex) {
            Logger.getLogger(PgUDTModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet rs = null;
        PreparedStatement ps =null;
        List<PgUserDefinedType> pgList = new ArrayList<>();

        try
        {
            ps = pgSQLInterface.getConnection().prepareStatement(SQL_UDT);
            rs=ps.executeQuery();
            
            if(rs.isBeforeFirst())
            {
                
                while(rs.next())
                {
                    PgUserDefinedType pgUserDefineType = new PgUserDefinedType();
                    CompositeType type=null;
                    try 
                    {
                        type = pgSQLInterface.GetCompositeTypeFromRegistry(rs.getString("internal_name"));
                        // = registry.loadType(rs.getString("name"));
                    } catch (Exception ex) {
                        Logger.getLogger(PgUDTModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                  
                    if (type != null) 
                    {
                        
                        TypeInfo typeInfo = new TypeInfo(type);
                        System.out.print(typeInfo.toString());
                        
                        pgUserDefineType.setCanConvert(Boolean.FALSE);
                        pgUserDefineType.setSchemaName(rs.getString("schema"));
                        pgUserDefineType.setTypeName(rs.getString("internal_name"));
                        pgUserDefineType.setSize(rs.getString("size")); 
                       
                      
                        pgUserDefineType.setSqlCompositeType(type);
                       // pgUserDefineType.getSqlType().set(type);
                        pgList.add(pgUserDefineType);
                      //return null;
                    }
    

                    
                    
                }
                
                
            }
            
        } 
        catch (SQLException e) 
        {
            throw new RuntimeException(e);
 
	} 
        finally 
        {
            if (ps != null) 
            {
                try 
                {
                    ps.close();
		} catch (SQLException e) {}
            }
            if (rs != null) 
            {
                try 
                {
                    rs.close();
		} catch (SQLException e) {}
            }   
            try {
                pgSQLInterface.CloseConnection();
            } catch (SQLException ex) {
                Logger.getLogger(PgUDTModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pgList;
        
    }
    


   
}
