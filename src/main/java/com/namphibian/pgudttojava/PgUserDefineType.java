/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgudttojava;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author adm_neil
 */
public class PgUserDefineType {
    
    private SimpleBooleanProperty canConvert = new SimpleBooleanProperty(false);
    private SimpleStringProperty  schemaName = new SimpleStringProperty();
    private SimpleStringProperty  typeName   = new SimpleStringProperty();
    private SimpleStringProperty  size       = new SimpleStringProperty();

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
    
}
