package com.namphibian.pgudttojava;

import com.impossibl.postgres.jdbc.PGConnectionImpl;
import static com.impossibl.postgres.protocol.ResultField.Format.Text;
import com.impossibl.postgres.types.CompositeType;
import com.impossibl.postgres.types.Registry;
import com.impossibl.postgres.types.Type;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private Connection connection; 
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
    private TableView<PgUserDefineType> tblMain;
    @FXML
    TableColumn itemConvertCol;
    @FXML
    TableColumn itemTypeNameCol;
    @FXML
    TableColumn itemSchemaCol;
    @FXML
    TableColumn itemSizeCol;
    private ObservableList data;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        try{
            ConnectToDatabase(txtServer.getText(), txtPort.getText(), txtDB.getText(), txtUID.getText(), txtPWD.getText());
            itemConvertCol.setCellValueFactory(new PropertyValueFactory<PgUserDefineType,Boolean>("canConvert"));
            itemTypeNameCol.setCellValueFactory(new PropertyValueFactory<PgUserDefineType,String>("typeName"));
            itemSchemaCol.setCellValueFactory(new PropertyValueFactory<PgUserDefineType,String>("schemaName"));
            itemSizeCol.setCellValueFactory(new PropertyValueFactory<PgUserDefineType,String>("size"));
            this.data = GetUDTList();
            tblMain.setItems(data);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    private void handleGenerateButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        PGConnectionImpl pgConn = (PGConnectionImpl) this.connection;
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
              
        Registry registry = pgConn.getRegistry();
        for (Object o : tblMain.getItems()) 
        {
                
                try{
                        Type type = registry.loadType((String) itemTypeNameCol.getCellData(o));    
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
                        
                        outFile = new File(selectedDirectory.getAbsolutePath() + File.separator + Names.getFullCamelCase(type.getName()) + "UserType.java");
                        try (FileWriter out = new FileWriter(outFile)) 
                        {

                            STGroup udtTemplates = new STGroupFile("templates/udtUserType.stg");
                            ST mainTemplate = udtTemplates.getInstanceOf("main");

                            mainTemplate.add("type", type);
                            mainTemplate.add("typeInfo", new TypeInfo((CompositeType) type));
                            mainTemplate.write(new AutoIndentWriter(out));
                        }
                        outFile = new File(selectedDirectory.getAbsolutePath() + File.separator + Names.getFullCamelCase(type.getName()) + "PgArrayUserType.java");
                        try (FileWriter out = new FileWriter(outFile)) 
                        {

                            STGroup udtTemplates = new STGroupFile("templates/udtArrayUserType.stg");
                            ST mainTemplate = udtTemplates.getInstanceOf("main");

                            mainTemplate.add("type", type);
                            mainTemplate.add("typeInfo", new TypeInfo((CompositeType) type));
                            mainTemplate.write(new AutoIndentWriter(out));
                        }
                        
                }
        
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void  ConnectToDatabase(String server, String port, String database, String userName, String password) throws Exception
    {
        String url ="jdbc:pgsql://" + server + ":" + port + "/" + database;
        
        
        try 
        {
            this.connection  = DriverManager.getConnection(url, userName, password);
            if (connection instanceof PGConnectionImpl == false) 
            {
                throw new IllegalStateException("Postgres driver must be PGJDBC-NG");
            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
    }
    public ObservableList GetUDTList()
    {
        ResultSet rs = null;
        PreparedStatement ps =null;
        List<PgUserDefineType> pgList = new ArrayList<>();
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
                    PgUserDefineType pgUserDefineType = new PgUserDefineType();
                    
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
        
    }
    
 
}