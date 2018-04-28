package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable  {


    @FXML  Button addholebtn ;
    @FXML  TextField holeaddressinput ;
    @FXML  TextField holesizeinput ;
    @FXML  Button removeholebtn ;
    @FXML  TableView<Hole> holestable;
    @FXML  TableColumn<Hole,Integer> holesadresses;
    @FXML  TableColumn<Hole,Integer> holessizes;
    @FXML  Button btn1;

    static ObservableList<Hole> holesList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addholebtn.requestFocus();
        addholebtn.setFocusTraversable(true);
        holesadresses.setCellValueFactory(new PropertyValueFactory<>("begin"));
        holessizes.setCellValueFactory(new PropertyValueFactory<>("size"));

    }

    public void addholebtnclicked()
    {

        if( !isInt(holeaddressinput,holeaddressinput.getText()));
        else if( !isInt(holesizeinput,holesizeinput.getText()));
        else {
            Hole hole = new Hole();
            hole.setBegin(Integer.parseInt(holeaddressinput.getText()));
            hole.setSize(Integer.parseInt(holesizeinput.getText()));
            holestable.getItems().add(hole);
            holeaddressinput.clear();
            holesizeinput.clear();
        }
    }

    public void removeholebtnclicked()
    {
        ObservableList<Hole> selectedholeslist,allholeslist;
        allholeslist = holestable.getItems();
        selectedholeslist = holestable.getSelectionModel().getSelectedItems();
        if(holestable.getItems().size()==1){
            holestable.getItems().clear();
        }
        else {
            selectedholeslist.forEach(allholeslist::remove);
        }

        if(holestable.getItems().size()==0)
            removeholebtn.setDisable(true);

    }

    public void tableclicked() {
        ObservableList<Hole> selectedholes;
        selectedholes = holestable.getSelectionModel().getSelectedItems();
        if (holestable.getItems().size() > 0) {
            if (selectedholes.size() > 0)
                removeholebtn.setDisable(false);
            else
                removeholebtn.setDisable(true);
        }
        else
            removeholebtn.setDisable(true);
    }

    public void btn1clicked(ActionEvent event) throws IOException
    {
        holesList = holestable.getItems();
        if(holesList.size()==0)
        {
            Stage window = new Stage();

            //Block events to other windows
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("ERROR");
            window.setMinWidth(250);

            Label label = new Label();
            label.setText("Enter Holes before proceeding");
            Button closeButton = new Button("Ok");
            closeButton.setOnAction(e -> window.close());

            VBox layout = new VBox(10);
            layout.getChildren().addAll(label, closeButton);
            layout.setAlignment(Pos.CENTER);

            //Display window and wait for it to be closed before returning
            Scene scene = new Scene(layout , 300 , 100);
            window.setScene(scene);
            window.showAndWait();

        }
        else {
            Parent processScreen = FXMLLoader.load(getClass().getResource("process.fxml"));
            Scene processScene = new Scene(processScreen);
            Stage window = new Stage();
            window.setScene(processScene);
            window.initModality(Modality.APPLICATION_MODAL);
            window.showAndWait();
        }

    }

    public static boolean isInt(TextField input, String message){
        try{
            int age = Integer.parseInt(input.getText());
            //System.out.println("User is: " + age);
            return true;
        }catch(NumberFormatException z){
            Stage window = new Stage();

            //Block events to other windows
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("ERROR");
            window.setMinWidth(250);

            Label label = new Label();
            label.setText("ERROR: "+message+" is not a number");
            Button closeButton = new Button("Ok");
            closeButton.setOnAction(e -> window.close());

            VBox layout = new VBox(10);
            layout.getChildren().addAll(label, closeButton);
            layout.setAlignment(Pos.CENTER);

            //Display window and wait for it to be closed before returning
            Scene scene = new Scene(layout , 300 , 100);
            window.setScene(scene);
            window.showAndWait();
            return false;
        }
    }
}
