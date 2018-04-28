package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import sample.Hole;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ProcessController implements Initializable {

    @FXML Button btn2;
    @FXML TextField processidinput;
    @FXML TextField processsizeinput;
    @FXML Button allocatebtn;
    @FXML Button deallocatebtn;
    @FXML TableView<Process> processtable;
    @FXML TableColumn<Process,String> processidCol;
    @FXML TableColumn<Process,Integer> processsizeCol;
    @FXML ChoiceBox<String> choicebox ;
    @FXML VBox vboxmemory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choicebox.getItems().add("First Fit");
        choicebox.getItems().add("Best Fit");
        choicebox.setValue("First Fit");
        processidCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        processsizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        vboxmemory.setAlignment(Pos.CENTER_RIGHT);
        displayholes(Controller.holesList);
        for(int i=0;i<Controller.holesList.size();i++)
        {
            Controller.holesList.get(i).setLeftSpace(Controller.holesList.get(i).getSize());
        }
    }

    public void tableclicked()
    {
        ObservableList<Process> selectedprocesses;
        selectedprocesses=processtable.getSelectionModel().getSelectedItems();
        if(processtable.getItems().size()>0)
        {
            if(selectedprocesses.size()>0)
                deallocatebtn.setDisable(false);
            else
                deallocatebtn.setDisable(true);
        }
        else
            deallocatebtn.setDisable(true);

    }

    public void allocatebtnclicked()
    {

        if(! Controller.isInt(processsizeinput , processsizeinput.getText()));
        if(processtable.getItems().size()>0)
        {
            boolean repeatedProcessName = false;
            ObservableList<Process> processes = processtable.getItems();
            String input = processidinput.getText();
            for(int i=0;i<processes.size();i++)
            {
                String processname = processes.get(i).getId();
                if(input.equals(processname))
                {
                    repeatedProcessName=true;
                    break;
                }
            }
            if(repeatedProcessName)
            {
                Stage window = new Stage();

                //Block events to other windows
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("ERROR");
                window.setMinWidth(250);

                Label label = new Label();
                label.setText("The Process id was assigned before\nPlease choose unique id");
                Button closeButton = new Button("Ok");
                closeButton.setOnAction(e -> window.close());

                VBox layout = new VBox(10);
                layout.getChildren().addAll(label, closeButton);
                layout.setAlignment(Pos.CENTER);

                //Display window and wait for it to be closed before returning
                Scene scene = new Scene(layout , 300 , 100);
                window.setScene(scene);
                window.showAndWait();
                processtable.getItems().remove(processtable.getItems().size()-1);
            }
        }
        Process process = new Process();
        process.setId(processidinput.getText());
        process.setSize(Integer.parseInt(processsizeinput.getText()));
        processtable.getItems().add(process);

        ObservableList<Process> processList = processtable.getItems();
        vboxmemory.getChildren().clear();
        if(choicebox.getValue()=="First Fit") {
            FirstFit();
            vboxmemory.getChildren().clear();
            displayholes(Controller.holesList);
        }
        else{
            BestFit();
            vboxmemory.getChildren().clear();
            displayholes(Controller.holesList);
        }
        processidinput.clear();
        processsizeinput.clear();

        // DRAW THE MEMORY
    }

    public void deallocatebtnclicked()
    {
        ObservableList<Process> selectedprocesslist,allprocesslist;
        allprocesslist = processtable.getItems();
        String deallocatedHoleName = processtable.getSelectionModel().getSelectedItem().getId();
        for(int i=0;i<Controller.holesList.size();i++)
        {
            if(deallocatedHoleName.equals(Controller.holesList.get(i).getName())){
                Controller.holesList.get(i).setName("");
                Controller.holesList.get(i).setLeftSpace(Controller.holesList.get(i).getSize());
            }
        }
        selectedprocesslist = processtable.getSelectionModel().getSelectedItems();
        if(allprocesslist.size()==1)
            allprocesslist.clear();
        else
        selectedprocesslist.forEach(allprocesslist::remove);

        for(int i=0;i<Controller.holesList.size()-1;i++)
        {
            if(Controller.holesList.get(i).getBegin()+Controller.holesList.get(i).getSize() == Controller.holesList.get(i+1).getBegin() && (Controller.holesList.get(i).getName()=="" && Controller.holesList.get(i+1).getName()=="")){
                Controller.holesList.get(i).setSize(Controller.holesList.get(i).getSize()+Controller.holesList.get(i+1).getSize());
                Controller.holesList.get(i).setLeftSpace(Controller.holesList.get(i).getSize());
                Controller.holesList.remove(i+1);
                i--;
            }
        }

        if(processtable.getItems().size()==0)
            deallocatebtn.setDisable(true);

            vboxmemory.getChildren().clear();
            displayholes(Controller.holesList);

    }

    public void btn2clicked(ActionEvent event)
    {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    private void displayholes(ObservableList<Hole> holes)
    {
        holes.sort(((o1, o2) -> o1.CompareToStartAddress(o2)));
        double maxlength = holes.get(holes.size()-1).getBegin() + holes.get(holes.size()-1).getSize();
        double resolution = vboxmemory.getPrefHeight()/maxlength ;


        for(int i=0;i<holes.size();i++){
            if(i==0 && holes.get(0).getBegin()!=0)
            {
                VBox addressContainer = new VBox();
                HBox cell = new HBox();
                Label address = new Label("0");
                address.setAlignment(Pos.TOP_CENTER);
                //Label endaddress = new Label(""+ holes.get(0).getBegin());
                VBox blackBox = new VBox();
                blackBox.setStyle("-fx-background-color: #000");
                cell.setPrefHeight(resolution*holes.get(0).getBegin());
                blackBox.setPrefHeight(resolution*holes.get(0).getBegin());
                address.setPrefHeight(blackBox.getPrefHeight()-5);
                addressContainer.setPrefHeight(resolution*holes.get(0).getBegin());
                addressContainer.setMaxHeight(blackBox.getPrefHeight());
                blackBox.setPrefWidth(100.0);
                //pid.setStyle("-fx-border-color: #000;-fx-border-style: solid;-fx-border-width: 2px");
                addressContainer.getChildren().addAll(address);
                cell.getChildren().addAll(addressContainer,blackBox);
                vboxmemory.getChildren().add(cell);
            }

            if(i!=0 && holes.get(i-1).getBegin()+holes.get(i-1).getSize() != holes.get(i).getBegin())
            {
                VBox addressContainer = new VBox();
                HBox cell = new HBox();
                Label address = new Label(""+(holes.get(i-1).getBegin()+holes.get(i-1).getSize()));
                address.setAlignment(Pos.TOP_CENTER);
                VBox blackBox = new VBox();
                blackBox.setStyle("-fx-background-color: #000");
                cell.setPrefHeight(resolution*(holes.get(i).getBegin() - (holes.get(i-1).getBegin()+holes.get(i-1).getSize())));
                blackBox.setPrefHeight(resolution*(holes.get(i).getBegin() - (holes.get(i-1).getBegin()+holes.get(i-1).getSize())));
                address.setPrefHeight(blackBox.getPrefHeight()-5);
                addressContainer.setPrefHeight(resolution*(holes.get(i).getBegin() - (holes.get(i-1).getBegin()+holes.get(i-1).getSize())));
                addressContainer.setMaxHeight(blackBox.getPrefHeight());
                blackBox.setPrefWidth(100.0);
                addressContainer.getChildren().addAll(address);
                cell.getChildren().addAll(addressContainer,blackBox);
                vboxmemory.getChildren().add(cell);

            }
            VBox addressContainer = new VBox();
            HBox cell = new HBox();
            Label address = new Label(""+holes.get(i).getBegin());
            address.setAlignment(Pos.TOP_CENTER);
            double endAddress = holes.get(i).getBegin()+holes.get(i).getSize();
            Label endaddress = new Label(""+ endAddress);
            Label pid = new Label(holes.get(i).getName());
            cell.setPrefHeight(resolution*holes.get(i).getSize());
            pid.setPrefHeight(resolution*holes.get(i).getSize());
            address.setPrefHeight(pid.getPrefHeight()-5);
            addressContainer.setPrefHeight(resolution*holes.get(i).getSize());
            pid.setPrefWidth(100.0);
            if(pid.getText().equals(""))
                pid.setStyle("-fx-border-color: #000;-fx-border-style: solid;-fx-border-width: 1px;-fx-padding:0 0 0 20");
            else
                pid.setStyle("-fx-border-color: #000;-fx-border-style: solid;-fx-border-width: 1px;-fx-padding:0 0 0 20;-fx-font-size: 15px;-fx-font-style: bold");
            pid.setWrapText(true);
            if(i==holes.size()-1) {
                addressContainer.getChildren().addAll(address, endaddress);
            }
            else{
                addressContainer.getChildren().addAll(address);
            }
            cell.getChildren().addAll(addressContainer,pid);
            vboxmemory.getChildren().add(cell);
        }
    }

    private void FirstFit()
    {
        for(int i=0;i<Controller.holesList.size();i++)
        {
            if(Integer.parseInt(processsizeinput.getText())< Controller.holesList.get(i).getLeftSpace())
            {
                double bigHoleSize = Controller.holesList.get(i).getSize();
                Controller.holesList.get(i).setName(processidinput.getText());
                Controller.holesList.get(i).setSize(Integer.parseInt(processsizeinput.getText()));
                Controller.holesList.get(i).setLeftSpace(0);
                Controller.holesList.add(i+1,new Hole("",Controller.holesList.get(i).getBegin()+(Integer.parseInt(processsizeinput.getText())),bigHoleSize-(Integer.parseInt(processsizeinput.getText()))));
                Controller.holesList.get(i+1).setLeftSpace(Controller.holesList.get(i+1).getSize());
                break;
            }
            else if(Integer.parseInt(processsizeinput.getText()) == Controller.holesList.get(i).getLeftSpace())
            {
                Controller.holesList.get(i).setName(processidinput.getText());
                Controller.holesList.get(i).setLeftSpace(0);
                break;
            }
            else if(Double.parseDouble(processsizeinput.getText()) > Controller.holesList.get(i).getLeftSpace())
            {
                if(i==Controller.holesList.size()-1)
                {
                    processtable.getItems().remove(processtable.getItems().size()-1);
                    Stage window = new Stage();

                    //Block events to other windows
                    window.initModality(Modality.APPLICATION_MODAL);
                    window.setTitle("ERROR");
                    window.setMinWidth(250);

                    Label label = new Label();
                    label.setText("Cannot be allocated");
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
                else{
                    continue;
                }
            }
        }
    }

    private void BestFit()
    {
       int maxHoleSizeIndex=0;
       for(int i=0;i<Controller.holesList.size();i++)
       {
           if(Controller.holesList.get(i).getLeftSpace() > Controller.holesList.get(maxHoleSizeIndex).getLeftSpace())
               maxHoleSizeIndex=i;
       }
       if(Double.parseDouble(processsizeinput.getText()) > Controller.holesList.get(maxHoleSizeIndex).getLeftSpace())
       {
           processtable.getItems().remove(processtable.getItems().size()-1);
           Stage window = new Stage();

           //Block events to other windows
           window.initModality(Modality.APPLICATION_MODAL);
           window.setTitle("ERROR");
           window.setMinWidth(250);

           Label label = new Label();
           label.setText("Cannot be allocated");
           Button closeButton = new Button("Ok");
           closeButton.setOnAction(e -> window.close());

           VBox layout = new VBox(10);
           layout.getChildren().addAll(label, closeButton);
           layout.setAlignment(Pos.CENTER);

           //Display window and wait for it to be closed before returning
           Scene scene = new Scene(layout , 300 , 100);
           window.setScene(scene);
           window.showAndWait();
           return;
       }
        for(int i=0;i<Controller.holesList.size();i++)
        {
            if(Double.parseDouble(processsizeinput.getText()) == Controller.holesList.get(i).getLeftSpace())
            {
                Controller.holesList.get(i).setName(processidinput.getText());
                Controller.holesList.get(i).setLeftSpace(0);

                //Controller.holesList.get(i).setName(Controller.holesList.get(i).getName()+" "+processidinput.getText());
                //Controller.holesList.get(i).setLeftSpace(Controller.holesList.get(i).getLeftSpace()- Double.parseDouble(processsizeinput.getText()));
                //processtable.getItems().get(processtable.getItems().size()-1).setHoleIndex(i);
                break;
            }
            else if(Double.parseDouble(processsizeinput.getText()) < Controller.holesList.get(i).getLeftSpace())
            {
                int minHoleSizeIndex=maxHoleSizeIndex;
                for(int j=0;j<Controller.holesList.size();j++)
                {
                    if( Double.parseDouble(processsizeinput.getText()) < Controller.holesList.get(j).getLeftSpace() && Controller.holesList.get(j).getLeftSpace() < Controller.holesList.get(minHoleSizeIndex).getLeftSpace())
                        minHoleSizeIndex=j;
                }

                double bigHoleSize = Controller.holesList.get(minHoleSizeIndex).getSize();
                Controller.holesList.get(minHoleSizeIndex).setName(processidinput.getText());
                Controller.holesList.get(minHoleSizeIndex).setSize(Integer.parseInt(processsizeinput.getText()));
                Controller.holesList.get(minHoleSizeIndex).setLeftSpace(0);
                Controller.holesList.add(minHoleSizeIndex+1,new Hole("",Controller.holesList.get(minHoleSizeIndex).getBegin()+(Integer.parseInt(processsizeinput.getText())),bigHoleSize-(Integer.parseInt(processsizeinput.getText()))));
                Controller.holesList.get(minHoleSizeIndex+1).setLeftSpace(Controller.holesList.get(minHoleSizeIndex+1).getSize());


                //Controller.holesList.get(minHoleSizeIndex).setName(Controller.holesList.get(minHoleSizeIndex).getName()+" "+processidinput.getText());
                //Controller.holesList.get(minHoleSizeIndex).setLeftSpace(Controller.holesList.get(minHoleSizeIndex).getLeftSpace()- Double.parseDouble(processsizeinput.getText()));
                //processtable.getItems().get(processtable.getItems().size()-1).setHoleIndex(minHoleSizeIndex);
                break;
            }
        }
    }




}
