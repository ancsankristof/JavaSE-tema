package com.example.javafxmaven;

import com.example.javafxmaven.Utils.DatabaseHandler;
import com.example.javafxmaven.Utils.PasswordHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
public class VendegLoginController {

    public static String currentuser;

    @FXML
    public Label welcomeText;
    @FXML
    public  Label usernameLabel;
    @FXML
    public  Label EmpTypeLabel;
    @FXML
    public  Label currentPasswordLabel;
    @FXML
    public Button saveChanged;
    @FXML
    public PasswordField currentPasswordInput;
    @FXML
    public RadioButton changeAddressRadio;
    @FXML
    public RadioButton changePhoneRadio;

    @FXML
    public RadioButton changePasswordRadio;

    @FXML
    public  PasswordField changePasswordInput;

    @FXML
    public TextField changeAddressInput;

    @FXML
    public  TextField changePhoneInput;

    @FXML
    public DatePicker newAppointmentDay;

    @FXML
    public ChoiceBox newAppointmentHour;

    @FXML
    public ChoiceBox toDeleteApn;

    ObservableList<String> st = FXCollections.observableArrayList("Ferfi hajvagas","Noi hajvagas","Hajfestes");

    ObservableList<String> freeapnhours = FXCollections.observableArrayList();

    ObservableList<String> userApns = FXCollections.observableArrayList();

    @FXML
    public ChoiceBox newAppointmentDesc;
    @FXML
    public  Button createApnBtn;

    @FXML
    public  Label newApnHourLabel;

    @FXML
    public Pane vendegloginPanel;


    @FXML
    private void initialize() throws SQLException {
        newAppointmentDesc.setItems(st);
        getUserApns(currentuser);

    }
    public  void getUserApns(String username) throws SQLException {
        Connection c = DatabaseHandler.connectToDatabase();

        ResultSet rs = DatabaseHandler.getUserAppointments(c,username);
        userApns.clear();
        toDeleteApn.setValue(null);
        while (rs.next()){
            String appointment = rs.getString("Appointment");

            userApns.add((String) appointment.subSequence(0,16));

        }

        toDeleteApn.setItems(userApns);

        c.close();
    }

    public void generalTabActive() throws SQLException {
        getUserApns(usernameLabel.getText());
    }

    public void closeButton() {
        Stage stage = (Stage)  vendegloginPanel.getScene().getWindow();
        stage.close();
    }

    public void deleteAppointmentUser() throws SQLException {
        Connection c = DatabaseHandler.connectToDatabase();
        DatabaseHandler.deleteSelectedApn(c,toDeleteApn.getValue().toString()+":00",usernameLabel.getText());
        c.close();
        getUserApns(currentuser);
    }

    public void createAppointment() throws SQLException {
        Connection c = DatabaseHandler.connectToDatabase();

        String date = newAppointmentDay.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String hour = newAppointmentHour.getValue().toString();

        //nem kész funkció
        String fodrasz = "fodrasz1";

        String desc = newAppointmentDesc.getValue().toString();

        if (hour.length()==1){
            hour = "0"+hour;
        }
        DatabaseHandler.registerAppointment(c,date+" "+hour+":00",usernameLabel.getText(),fodrasz,desc);

        c.close();

        newAppointmentDesc.setValue(null);
        newAppointmentHour.setValue(null);
        newAppointmentDay.setValue(null);
        newAppointmentHour.setVisible(false);
        createApnBtn.setVisible(false);
        newApnHourLabel.setVisible(false);

    }

    public void showAvbDates() throws SQLException {

        ArrayList<String> avbHours = new ArrayList<>();
        ArrayList<String> festesFree= new ArrayList<>();
        ArrayList<String> noiFree= new ArrayList<>();

        freeapnhours.clear();
        newAppointmentHour.setItems(freeapnhours);

        int n = 8;
        for(int i=0;i<22;i++) {

            if (i % 2 == 0) {
                String hour = Integer.toString(n);
                if (hour.length() == 1) {
                    hour = "0"+hour;
                }
                avbHours.add(hour +":00");
            } else {
                String hour = Integer.toString(n);
                if (hour.length() == 1) {
                    hour = "0"+hour;
                }
                avbHours.add(hour +":30");
                n++;
            }
        }

        if (newAppointmentDay.getValue() != null && newAppointmentDesc.getValue() != null){


            Connection c = DatabaseHandler.connectToDatabase();

            String date = newAppointmentDay.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            ResultSet rs = DatabaseHandler.getAppointmentDay(c,date);

            String appointmenttype  = newAppointmentDesc.getValue().toString();

            while (true){
                assert rs != null;
                if (!rs.next()) break;

                String[] splithours = rs.getString("NextFreeHour").split(",");



                for (var hour:splithours) {
                    avbHours.remove(hour);

                }

            }

            switch (appointmenttype) {
                case "Ferfi hajvagas" -> {
                    freeapnhours.addAll(avbHours);
                    newAppointmentHour.setItems(freeapnhours);
                    break;
                }
                case "Noi hajvagas" -> {
                    int m = 1;
                    int h = 2;
                    for (int i = 0; i < avbHours.size() - 2; i++) {
                        String currentszam = avbHours.get(i);
                        String nextszam = avbHours.get(m);
                        String nextnextszam = avbHours.get(h);

                        String currentsplit = currentszam.split(":")[0];
                        String currentsplitmin = currentszam.split(":")[1];

                        String nextsplit = nextszam.split(":")[0];
                        String nextsplitmin = nextszam.split(":")[1];

                        String nextnextsplit = nextnextszam.split(":")[0];
                        String nextnextsplitmin = nextnextszam.split(":")[1];


                        if ((currentsplit.equals(nextsplit)) && (Integer.parseInt(nextsplit) + 1 == Integer.parseInt(nextnextsplit))) {
                            if (nextnextsplitmin.equals("00")) {
                                noiFree.add(currentszam);

                            }


                        } else if ((currentsplitmin.equals("30")) && (Integer.parseInt(currentsplit) + 1 == Integer.parseInt(nextsplit))) {

                            if (avbHours.contains(nextsplit + ":00") && nextsplit.equals(nextnextsplit)) {
                                noiFree.add(currentszam);

                            }

                        }

                        m++;
                        h++;
                    }
                    freeapnhours.addAll(noiFree);
                    newAppointmentHour.setItems(freeapnhours);
                }
                case "Hajfestes" -> {
                    int g = 1;
                    for (int i = 0; i < avbHours.size() - 1; i++) {
                        String currentszam = avbHours.get(i);
                        String nextszam = avbHours.get(g);

                        String currentsplit = currentszam.split(":")[0];
                        String currentsplitmin = currentszam.split(":")[1];
                        String nextsplit = nextszam.split(":")[0];
                        String nextsplitmin = nextszam.split(":")[1];


                        if (currentsplit.equals(nextsplit)) {
                            festesFree.add(currentszam);
                        } else if ((currentsplitmin.equals("30")) && (Integer.parseInt(currentsplit) + 1 == Integer.parseInt(nextsplit))) {

                            if (avbHours.contains(nextsplit + ":00")) {
                                festesFree.add(currentszam);
                            }

                        }

                        g++;
                    }


                    freeapnhours.addAll(festesFree);
                    newAppointmentHour.setItems(freeapnhours);
                }
            }

            c.close();

            newAppointmentHour.setVisible(true);
            newApnHourLabel.setVisible(true);
            createApnBtn.setVisible(true);
        }







    }

    public void changeAdressActive() {
        if (changeAddressRadio.isSelected()){
            changeAddressInput.setVisible(true);
            saveChanged.setVisible(true);
            currentPasswordLabel.setVisible(true);
            currentPasswordInput.setVisible(true);
        }
        else {
            hideFields();
            changeAddressInput.setVisible(false);
        }

    }
    public void  hideFields(){
        if( !(changeAddressRadio.isSelected() || changePhoneRadio.isSelected() || changePasswordRadio.isSelected())){
            saveChanged.setVisible(false);
            currentPasswordLabel.setVisible(false);
            currentPasswordInput.setVisible(false);
        }
    }

    public void  changePhoneActive(){
        if (changePhoneRadio.isSelected()){
            changePhoneInput.setVisible(true);
            saveChanged.setVisible(true);
            currentPasswordLabel.setVisible(true);
            currentPasswordInput.setVisible(true);
        }
        else {
            hideFields();
            changePhoneInput.setVisible(false);
        }
    }

    public void  changePasswordActive(){
        if (changePasswordRadio.isSelected()){
            changePasswordInput.setVisible(true);
            saveChanged.setVisible(true);
            currentPasswordLabel.setVisible(true);
            currentPasswordInput.setVisible(true);
        }
        else {
            hideFields();
            changePasswordInput.setVisible(false);
        }
    };

    public void changeSave() throws SQLException, NoSuchAlgorithmException {
        if (currentPasswordInput.getText() !=null){
            Connection c = DatabaseHandler.connectToDatabase();
            if (Boolean.TRUE.equals(DatabaseHandler.checkLogin(c, usernameLabel.getText(), currentPasswordInput.getText()))){

                if (changeAddressInput.isVisible() && !changeAddressInput.getText().equals("")){

                    DatabaseHandler.updateUserFields(c,"address",changeAddressInput.getText(),usernameLabel.getText());

                    changeAddressInput.setText(null);
                }

                if (changePhoneInput.isVisible() && !changePhoneInput.getText().equals("")){

                    DatabaseHandler.updateUserFields(c,"phone",changePhoneInput.getText(),usernameLabel.getText());

                    changePhoneInput.setText(null);
                }
                if (currentPasswordLabel.isVisible() && !changePasswordInput.getText().equals("")){
                    String password = changePasswordInput.getText();

                    password = PasswordHandler.createSHAHash(password);

                    DatabaseHandler.updateUserFields(c,"password",password,usernameLabel.getText());

                    currentPasswordLabel.setText(null);
                }
            }
            currentPasswordInput.setText("");
            changePhoneInput.setText("");
            changePasswordInput.setText("");
            changeAddressInput.setText("");

            changeAddressRadio.setSelected(false);
            changePhoneRadio.setSelected(false);
            changePasswordRadio.setSelected(false);

            saveChanged.setVisible(false);
            currentPasswordLabel.setVisible(false);
            currentPasswordInput.setVisible(false);
            changePasswordInput.setVisible(false);
            changeAddressInput.setVisible(false);
            changePhoneInput.setVisible(false);


            c.close();
        }


    }
}
