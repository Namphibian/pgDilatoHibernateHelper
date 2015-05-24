/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgudttojava;

import com.impossibl.postgres.types.CompositeType;
import com.impossibl.postgres.types.Type;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Neil Franken
 * @version 0.0.0.1
 */
public class PgUserDefinedType {
    
    private final SimpleBooleanProperty canConvert                      = new SimpleBooleanProperty(false);
    private final SimpleStringProperty  schemaName                      = new SimpleStringProperty();
    private final SimpleStringProperty  typeName                        = new SimpleStringProperty();
    private final SimpleStringProperty  size                            = new SimpleStringProperty();
   /* private final SimpleObjectProperty<Type> sqlType                    = new  ReadOnlyObjectWrapper<>();*/
 
    private CompositeType sqlCompositeType ;
    public PgUserDefinedType(){
        
        this.sqlCompositeType = new CompositeType();

    
    }
    
    
    /**
     * @return the canConvert
     */
    public Boolean getCanConvert() {
        return canConvert.get();
    }

    /**
     * @param canConvert the canConvert to set
     */
    public void setCanConvert(Boolean canConvert) {
        this.canConvert.set(canConvert);
    }

    /**
     * @return the schemaName
     */
    public String getSchemaName() {
        return schemaName.get();
    }

    /**
     * @param schemaName the schemaName to set
     */
    public void setSchemaName(String schemaName) {
        this.schemaName.set(schemaName);
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName.get();
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName.set(typeName);
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size.get();
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size.set(size);
    }

    /**
     * @return the sqlCompositeType
     */
    public CompositeType getSqlCompositeType() {
        return sqlCompositeType;
    }

    /**
     * @param sqlCompositeType the sqlCompositeType to set
     */
    public void setSqlCompositeType(CompositeType sqlCompositeType) {
        this.sqlCompositeType = sqlCompositeType;
    }


    
}
