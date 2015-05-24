package com.namphibian.pgudttojava;



import com.impossibl.postgres.types.CompositeType;
import com.impossibl.postgres.types.Type;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class FXMLController implements Initializable {
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
    //private Connection connection; 
    @FXML
    private Button btnConnect;
    @FXML
    private TextField txtServer;
    @FXML
    private TextField txtPort;
    @FXML
    private TextField txtDB;
    @FXML
    private TextField txtUID;
    @FXML
    private TextField txtPWD;
    @FXML
    private TableView<PgUserDefinedType> tblMain;
    @FXML
    TableColumn<PgUserDefinedType,Boolean> itemConvertCol;
    @FXML
    TableColumn<PgUserDefinedType,String> itemTypeNameCol;
    @FXML
    TableColumn<PgUserDefinedType,String> itemSchemaCol;
    @FXML
    TableColumn<PgUserDefinedType,String> itemSizeCol;

    private ObservableList data;
    private PgUDTModelInterface pgSQLUDT;
    @FXML
    private void handleButtonAction(ActionEvent event) {

        try{
            
            PgSQLConnectionData pgSQLConnectionData = new PgSQLConnectionData(txtServer.getText(), txtPort.getText(), txtDB.getText(), txtUID.getText(), txtPWD.getText());
            pgSQLUDT = new PgUDTModel(pgSQLConnectionData);
            
            itemConvertCol.setCellValueFactory(new PropertyValueFactory("canConvert"));
            itemTypeNameCol.setCellValueFactory(new PropertyValueFactory("typeName"));
            itemSchemaCol.setCellValueFactory(new PropertyValueFactory("schemaName"));
            itemSizeCol.setCellValueFactory(new PropertyValueFactory("size"));
           
            this.data = FXCollections.observableList(pgSQLUDT.GetUDTList());
            tblMain.setItems(data);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    private void handleGenerateButtonAction(ActionEvent event) {

        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Directory For UDT Classes");
        File selectedDirectory = directoryChooser.showDialog(null);

        if(selectedDirectory == null)
        {
            final Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Button okButton = new Button( "OK" );
            okButton.setAlignment( Pos.CENTER );
            okButton.setOnAction( 
                new EventHandler<ActionEvent>() 
                    {
                        @Override 
                        public void handle( ActionEvent e ) 
                        {
                            dialogStage.close();
                        }
                    } 
            );
            dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text("No folder selected generation will not proceed."), new Button("Ok")).
                alignment(Pos.CENTER).padding(new Insets(5)).build()));
            dialogStage.showAndWait();
            return;
        }
              
       
        for (Object o : tblMain.getItems()) 
        {
                
                try{
                        System.out.println("Hiya");
                    /* Type type = registry.loadType((String) itemTypeNameCol.getCellData(o));   */ 
                        PgUserDefinedType pgUserDefinedType =(PgUserDefinedType)o;
                        GenerateSQLDataTypeClass(selectedDirectory,pgUserDefinedType.getSqlCompositeType(), new TypeInfo(pgUserDefinedType.getSqlCompositeType()));
                        GenerateHibernateUDTType(selectedDirectory,pgUserDefinedType.getSqlCompositeType(), new TypeInfo(pgUserDefinedType.getSqlCompositeType()));
                        GenerateHibernateArrayUDTType(selectedDirectory,pgUserDefinedType.getSqlCompositeType(), new TypeInfo(pgUserDefinedType.getSqlCompositeType()));
                        
                }
        
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void GenerateHibernateArrayUDTType(File selectedDirectory, Type type, TypeInfo typeInfo) throws IOException {
        File outFile = new File(selectedDirectory.getAbsolutePath() + File.separator + Names.getFullCamelCase(type.getName()) + "PgArrayUserType.java");
        try (FileWriter out = new FileWriter(outFile))
        {
            
            STGroup udtTemplates = new STGroupFile("templates/udtArrayUserType.stg");
            ST mainTemplate = udtTemplates.getInstanceOf("main");
            
            mainTemplate.add("type", type);
            mainTemplate.add("typeInfo", typeInfo);
            mainTemplate.write(new AutoIndentWriter(out));
        }
    }

    private void GenerateHibernateUDTType(File selectedDirectory, Type type , TypeInfo typeInfo) throws IOException {
        File outFile = new File(selectedDirectory.getAbsolutePath() + File.separator + Names.getFullCamelCase(type.getName()) + "UserType.java");
        try (FileWriter out = new FileWriter(outFile))
        {
            
            STGroup udtTemplates = new STGroupFile("templates/udtUserType.stg");
            ST mainTemplate = udtTemplates.getInstanceOf("main");
            
            mainTemplate.add("type", type);
            mainTemplate.add("typeInfo", new TypeInfo((CompositeType) type));
            mainTemplate.write(new AutoIndentWriter(out));
        }
    }

    private void GenerateSQLDataTypeClass(File selectedDirectory, Type type, TypeInfo typeInfo) throws IOException {
        File outFile = new File(selectedDirectory.getAbsolutePath() + File.separator + Names.getFullCamelCase(type.getName()) + "Type.java");
        try (FileWriter out = new FileWriter(outFile))
        {
            
            STGroup udtTemplates = new STGroupFile("templates/udt.stg");
            ST mainTemplate = udtTemplates.getInstanceOf("main");
            mainTemplate.add("isInPackage",false);// targetPackage != null && !targetPackage.isEmpty());
            mainTemplate.add("package", "com.package");
            mainTemplate.add("type", type);
            mainTemplate.add("typeInfo", new TypeInfo((CompositeType) type));
            mainTemplate.write(new AutoIndentWriter(out));
        }
    }
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       //pgSQL = PgSQLImpl.getInstance();
        
    }    
    
   
 /*   public ObservableList GetUDTList()
    {
        ResultSet rs = null;
        PreparedStatement ps =null;
        List<PgUserDefinedType> pgList = new ArrayList<>();
        PGConnectionImpl pgConn = (PGConnectionImpl) this.connection;

        Registry registry = pgConn.getRegistry();
        try
        {
            ps = this.connection.prepareStatement(SQL_UDT);
            rs=ps.executeQuery();
            
            if(rs.isBeforeFirst())
            {
                
                while(rs.next())
                {
                    PgUserDefinedType pgUserDefineType = new PgUserDefinedType();
                    
                    Type type = registry.loadType(rs.getString("name"));
                    
                  
                    if (type != null && type instanceof CompositeType != false) 
                    {
                        
                        TypeInfo typeInfo = new TypeInfo((CompositeType) type);
                        System.out.print(typeInfo.toString());
                        
                        pgUserDefineType.setCanConvert(Boolean.FALSE);
                        pgUserDefineType.setSchemaName(rs.getString("schema"));
                        pgUserDefineType.setTypeName(rs.getString("name"));
                        pgUserDefineType.setSize(rs.getString("size")); 
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
        }
        return FXCollections.observableList(pgList);
        
    }*/
    
 
}