package com.example.javafxmaven;

import com.example.javafxmaven.Utils.DatabaseHandler;
import com.example.javafxmaven.Utils.PasswordHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class AlkalmazottLoginController {
    @FXML
    public  Label welcomeText;
    @FXML
    public  Label usernameLabel;
    @FXML
    public  Label EmpTypeLabel;
    @FXML
    public  Label currentPasswordLabel;
    @FXML
    public  Button saveChanged;
    @FXML
    public  PasswordField currentPasswordInput;
    @FXML
    public RadioButton changeAddressRadio;
    @FXML
    public RadioButton changePhoneRadio;
    @FXML
    public RadioButton changePasswordRadio;
    @FXML
    public  PasswordField changePasswordInput;
    @FXML
    public  TextField changeAddressInput;
    @FXML
    public  TextField changePhoneInput;
    @FXML
    public ChoiceBox toDeleteApn;
    @FXML
    public CheckBox showDeleteApnBox;
    @FXML
    public CheckBox showSettingsBox;
    @FXML
    public Button deleteAppointmentButton;
    @FXML
    public TabPane afterlogintabpanel;
    @FXML
    public GridPane calendarPanel;
    @FXML
    public  Label currentYearName;
    @FXML
    public  Label settingsAddressLabel;
    @FXML
    public  Label settingsPhoneLabel;
    @FXML
    public  Label settingsnewPasswordLabel;

    LocalDate currentdate = LocalDate.now();
    String startmonth ;
    String endmonth;
    String startday;
    String endday;
    HashMap<String,Label> calendarMap = new HashMap<>();
    HashMap<String,String> calendarAppointments = new HashMap<>();
    ObservableList<String> userApns = FXCollections.observableArrayList();
    ArrayList<Label> calendarLabels = new ArrayList<>();

    public void showDeleteApn() {
        if (showDeleteApnBox.isSelected()){
            toDeleteApn.setVisible(true);
            deleteAppointmentButton.setVisible(true);
        }
        else{
            toDeleteApn.setVisible(false);
            deleteAppointmentButton.setVisible(false);
            showDeleteApnBox.setSelected(false);
        }
    }

    public void showSettings() {
        if (showSettingsBox.isSelected()){

            settingsAddressLabel.setVisible(true);
            settingsPhoneLabel.setVisible(true);
            settingsnewPasswordLabel.setVisible(true);

            changeAddressRadio.setVisible(true);
            changePhoneRadio.setVisible(true);
            changePasswordRadio.setVisible(true);

        }
        else{
            settingsAddressLabel.setVisible(false);
            settingsPhoneLabel.setVisible(false);
            settingsnewPasswordLabel.setVisible(false);

            showSettingsBox.setSelected(false);

            changeAddressRadio.setVisible(false);
            changeAddressRadio.setSelected(false);

            changePhoneRadio.setVisible(false);
            changePhoneRadio.setSelected(false);

            changePasswordRadio.setVisible(false);
            changePasswordRadio.setSelected(false);

            changeAddressInput.setVisible(false);
            changePhoneInput.setVisible(false);
            changePasswordInput.setVisible(false);
            currentPasswordInput.setVisible(false);
            currentPasswordLabel.setVisible(false);
            saveChanged.setVisible(false);

        }
    }

    class AppointmentCompare implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            String appointment1 = o1.split("/")[1];
            String appointment2 = o2.split("/")[1];

            return appointment1.compareTo(appointment2);
        }
    }

    public void calendarTabActive() throws ParseException, SQLException {

        int currentyear = currentdate.getYear();

        if (calendarLabels.size()==0){
            createCalendar();
        }

        updateCalendar();
        currentYearName.setText(Integer.toString(currentyear));

        updateStrDates();
        updateFromDatabase(currentYearName.getText()+"-"+startmonth+"-"+startday, currentYearName.getText()+"-"+endmonth+"-"+endday);

    }
    public void createCalendar(){

        String[] daynames = {"Hetfo", "Kedd", "Szerda", "Csutortok","Pentek","Szombat","Vasarnap"};



        for (int i=0;i<6;i++){
            Label newDay = new Label("calendarLabel"+Integer.toString(i)+"0");
            calendarLabels.add(newDay);
            newDay.setText(daynames[i]);

            newDay.setStyle("-fx-border-color:black;");
            newDay.setStyle("-fx-font-size:14;");


            newDay.setPrefHeight(29.55);
            newDay.setMinHeight(29.55);
            newDay.setMaxHeight(29.55);
            newDay.setPrefWidth(150.0);

            calendarPanel.add(newDay,i,0);
        }

        String[] days = {"H", "K", "Sz", "Cs","P","Szo","V"};
        for (int i=0;i<6;i++){
            //het:napok
            String day = days[i];
            int j = 1;

            for (int c = 1;c<23;c++){
                //nap:orak
                String labelhour = Integer.toString(j+7);

                if (labelhour.length()==1){
                    labelhour = "0"+labelhour;
                }

                if (c%2==0){
                    Label newDay = new Label("calendarLabel-"+day+"-"+labelhour+":30");

                    newDay.setText("");

                    newDay.setStyle("-fx-border-color:black;");

                    newDay.setPrefHeight(29.55);
                    newDay.setMinHeight(29.55);
                    newDay.setMaxHeight(29.55);
                    newDay.setPrefWidth(150.0);


                    calendarMap.put("calendarLabel-"+day+"-"+labelhour+":30",newDay);
                    calendarPanel.add(newDay,i,c);

                    j++;
                }
                else{
                    Label newDay = new Label("calendarLabel-"+day+"-"+labelhour+":00");

                    newDay.setText("");
                    newDay.setStyle("-fx-border-color:black;");

                    newDay.setPrefHeight(29.55);
                    newDay.setMinHeight(29.55);
                    newDay.setMaxHeight(29.55);
                    newDay.setPrefWidth(150.0);


                    calendarMap.put("calendarLabel-"+day+"-"+labelhour+":00",newDay);
                    calendarPanel.add(newDay,i,c);


                }


            }


        }
        //currentYearName.setText(Integer.toString(currentyear));

    }
    public void updateCalendar(){
        String[] daynames = {"Hetfo", "Kedd", "Szerda", "Csutortok","Pentek","Szombat","Vasarnap"};

        int currentyear = currentdate.getYear();
        int currentday = currentdate.getDayOfMonth();
        int currentmonth = currentdate.getMonthValue();

        LocalDate localDate = LocalDate.of(currentyear, currentmonth, currentday);

        int DayWeek = DayOfWeek.from(localDate).getValue();

        ArrayList<String> dates  = new ArrayList<>();

        int daycount = 0;

        for (int i = DayWeek-1;i!=0;i--){
            String tolabel= "  "+localDate.minusDays(i).getMonthValue()+"/"+localDate.minusDays(i).getDayOfMonth()+"  "+daynames[daycount];
            dates.add(tolabel);
            daycount++;
        }

        for (int i = 0;DayWeek+i < 8;i++){
            String out2= "  "+ localDate.plusDays(i).getMonthValue()+"/"+localDate.plusDays(i).getDayOfMonth()+"  "+daynames[daycount];
            dates.add(out2);
            daycount++;
        }


        for (int i=0;i<6;i++){

            Label dayLabel = calendarLabels.get(i);
            dayLabel.setText(dates.get(i));

            dayLabel.setStyle("-fx-border-color:black;");
            dayLabel.setStyle("-fx-font-size:14;");
        }

        currentYearName.setText(Integer.toString(currentyear));
    }

    public void nextWeek() throws ParseException, SQLException {
        currentdate = currentdate.plusDays(7);

        updateCalendar();
        updateStrDates();
        updateFromDatabase(currentYearName.getText()+"-"+startmonth+"-"+startday, currentYearName.getText()+"-"+endmonth+"-"+endday);
    }

    public void prevWeek() throws ParseException, SQLException {
        currentdate = currentdate.plusDays(-7);

        updateCalendar();
        updateStrDates();
        updateFromDatabase(currentYearName.getText()+"-"+startmonth+"-"+startday, currentYearName.getText()+"-"+endmonth+"-"+endday);
    }

    public void updateFromDatabase(String startdate,String enddate) throws SQLException, ParseException {
        Connection c = DatabaseHandler.connectToDatabase();
        ResultSet set = DatabaseHandler.getAppointmentWeek(c,startdate,enddate);

        if (calendarAppointments.size() != 0){
            for (var cl:calendarMap.values()) {

                cl.setText("");
                cl.setStyle("-fx-background-color: transparent");
                cl.setStyle("-fx-border-color:black;");
                cl.setMinHeight(29.55);
                cl.setMinWidth(150.0);
                cl.setPrefHeight(29.55);
                cl.setPrefWidth(150.0);

            }
        }

        calendarAppointments.clear();

        while (set.next()){
            calendarAppointments.put(set.getString("Appointment"),set.getString("Description"));

            String date = set.getString("Appointment");
            String desc = set.getString("Description");

            SimpleDateFormat databaseDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dbDate = databaseDateFormat.parse(date);

            int ora = dbDate.getHours();
            int perc = dbDate.getMinutes();
            String fullname = desc.split("/")[1];
            String worktype = desc.split("/")[0];

            String[] days = {"H", "K", "Sz", "Cs","P","Szo","V"};
            String naplabel = days[dbDate.getDay()-1];

            String oralabel = String.valueOf(ora);

            String oranext = Integer.toString(ora+1);
            String oranextnext = Integer.toString(ora+2);


            if (oralabel.length()==1){
                oralabel="0"+oralabel;
            }
            if (oranextnext.length()==1){
                oranextnext="0"+oranextnext;
            }
            if (oranext.length()==1){
                oranext="0"+oranext;
            }


            switch(worktype) {
                case "Ferfi hajvagas":
                    if (perc==0){
                        String colortomod = "green";
                        changeLabel("calendarLabel-"+naplabel+"-"+oralabel+":00",fullname+": "+oralabel+":00"+"-"+ora+":30",colortomod);
                    }
                    else {
                        String colortomod = "green";
                        changeLabel("calendarLabel-"+naplabel+"-"+oralabel+":30",fullname+": "+oralabel+":30"+"-"+oranext+":00",colortomod);
                    }
                    break;
                case "Noi hajvagas":

                    if (perc==0){
                        String colortomod = "lightblue";

                        //1. 15:00
                        changeLabel("calendarLabel-"+naplabel+"-"+oralabel+":00",fullname+": "+oralabel+":00"+"-"+oranext+":30",colortomod);

                        //2. 15:30
                        changeLabel("calendarLabel-"+naplabel+"-"+oralabel+":30","\t",colortomod);

                        //3. 16:00
                        changeLabel("calendarLabel-"+naplabel+"-"+oranext+":00","\t",colortomod);


                    }
                    else {
                        String colortomod = "lightblue";

                        //1. 15:30
                        changeLabel("calendarLabel-"+naplabel+"-"+oralabel+":30",fullname+": "+oralabel+":30"+"-"+oranextnext+":00",colortomod);

                        //2. 16:00
                        changeLabel("calendarLabel-"+naplabel+"-"+oranext+":00","\t",colortomod);

                        //3. 16:30
                        changeLabel("calendarLabel-"+naplabel+"-"+oranext+":30","\t",colortomod);

                    }
                    break;
                case "Hajfestes":

                    if (perc==0){
                        String colortomod = "orange";

                        //1. 15:00
                        changeLabel("calendarLabel-"+naplabel+"-"+oralabel+":00",fullname+": "+oralabel+":00"+"-"+oranext+":00",colortomod);

                        //2. 16:00
                        changeLabel("calendarLabel-"+naplabel+"-"+oralabel+":30","\t",colortomod);

                    }
                    else {
                        String colortomod = "orange";

                        //1. 15:30
                        changeLabel("calendarLabel-"+naplabel+"-"+oralabel+":30",fullname+": "+oralabel+":30"+"-"+oranext+":30",colortomod);

                        //2. 16:00
                        changeLabel("calendarLabel-"+naplabel+"-"+oranext+":00","\t",colortomod);

                        //2. 16:30
                        changeLabel("calendarLabel-"+naplabel+"-"+oranext+":30","\t",colortomod);

                    }

                    break;
            }

        }
        c.close();
    }

    public void updateStrDates(){
        String startlabeltext = calendarLabels.get(0).getText();
        String startdaymonthstr[] = startlabeltext.split("  ");
        String startdaymonth[] = startdaymonthstr[1].split("/");

        String endlabeltext = calendarLabels.get(5).getText();
        String enddaymonthstr[] = endlabeltext.split("  ");
        String enddaymonth[] = enddaymonthstr[1].split("/");

        startmonth = startdaymonth[0];
        endmonth = enddaymonth[0];

        startday = startdaymonth[1];
        endday = enddaymonth[1];

        if (startmonth.length() == 1){
            startmonth = "0" + startdaymonth[0];
        }
        if (endmonth.length() == 1){
            endmonth = "0" + enddaymonth[0];
        }

        if (startday.length() == 1){
            startday = "0" + startdaymonth[1];
        }
        if (endday.length() == 1){
            endday = "0" + enddaymonth[1];
        }
    }

    public void closeButton() {
        Stage stage = (Stage) calendarPanel.getScene().getWindow();
        stage.close();
    }

    public void changeLabel(String labelname,String labeltext,String color){
        Label labeltomofidy = calendarMap.get(labelname);
        labeltomofidy.setText(labeltext);
        labeltomofidy.setStyle("-fx-background-color: "+color+"; ");
        labeltomofidy.setPrefHeight(29.55);
        labeltomofidy.setMinHeight(29.55);
        labeltomofidy.setMaxHeight(29.55);
        labeltomofidy.setPrefWidth(149.5);
    }

    public void deleteAppointmentUser() throws SQLException {
        Connection c = DatabaseHandler.connectToDatabase();
        String appointment = toDeleteApn.getValue().toString().split("/")[1];
        String fullname = toDeleteApn.getValue().toString().split("/")[0];
        String username = DatabaseHandler.getUsernameByFullname(c,fullname).getString("Username");

        DatabaseHandler.deleteSelectedApn(c,appointment+":00",username);
        c.close();
        getUserApns();
    }

    public void getUserApns() throws SQLException {
        Connection c = DatabaseHandler.connectToDatabase();
        ResultSet rs = DatabaseHandler.getAllUserAppointments(c);
        userApns.clear();
        toDeleteApn.setValue(null);
        while (true){
            assert rs != null;
            if (!rs.next()) break;

            String appointment = rs.getString("Appointment");
            String apnowner = rs.getString("Description").split("/")[1];

            userApns.add( (apnowner+ "/" +(String) appointment.subSequence(0,16)) );



        }
        userApns.sort(new AppointmentCompare());

        toDeleteApn.setItems(userApns);

        c.close();

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
    };

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
