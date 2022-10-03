package com.example.javafxmaven.Utils;

import java.sql.*;


public class DatabaseHandler {



    public static void createDatabaseTable() {


        try {
            Connection c = connectToDatabase();
            c.setAutoCommit(true);
            Statement st = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS main.LoginInfo (Name TEXT PRIMARY KEY NOT NULL,Password TEXT NOT NULL);";
            st.executeUpdate(sql);


            sql = "CREATE TABLE IF NOT EXISTS main.Person (Username TEXT PRIMARY KEY NOT NULL,FullName TEXT NOT NULL,Phone TEXT NOT NULL,Address TEXT NOT NULL,EmpType TEXT NOT NULL);";
            st.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS main.Calendar (Appointment DATE PRIMARY KEY NOT NULL,Username TEXT NOT NULL,FodraszName TEXT NOT NULL,Description TEXT NOT NULL,NextFreeHour TEXT NOT NULL);";
            st.executeUpdate(sql);

            st.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public static Connection connectToDatabase() {

        Connection c = null;
            try {
                c = DriverManager.getConnection("jdbc:sqlite:maindatabase.sqlite");

            } catch ( Exception e ) {
                System.out.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }


        return c;
    }

    public static void registerUser(Connection c,String username, String password,String FullName, String Phone, String Address, String EmpType){
        try{

            Statement st = c.createStatement();
            String tablename = "LoginInfo";
            //String sql = "INSERT INTO "+tablename+" (Name,Password) VALUES ("+username+","+password+");";
            String sql = String.format("INSERT INTO %s (Name,Password) VALUES (\'%s\',\'%s\');",tablename,username,password);

            tablename = "Person";
            String sqlperson = String.format("INSERT INTO %s (Username,FullName,Phone,Address,EmpType) VALUES (\'%s\',\'%s\',\'%s\',\'%s\',\'%s\');",tablename,username,FullName,Phone,Address,EmpType);

            c.setAutoCommit(false);
            st.executeUpdate(sql);
            st.executeUpdate(sqlperson);
            c.commit();
            st.close();
            c.close();
        }
        catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public static ResultSet getUserInfo(Connection c,String username) {

        try {
            Statement st = c.createStatement();
            c.setAutoCommit(false);
            String loggedinsql = "select * from LoginInfo as l inner join Person p on l.Name = p.Username where l.Name = " + " \'" + username + "\'" + " ;";
            ResultSet loggedinresult = st.executeQuery(loggedinsql);

            return loggedinresult;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }



    }

    public static Boolean checkLogin(Connection c,String username,String password){

        try {
            String tablename = "LoginInfo";
            c.setAutoCommit(false);
            Statement st = c.createStatement();
            password = PasswordHandler.createSHAHash(password);
            String sql = "select * from "+ tablename+" where Name = "+"\'"+username+"\'  AND Password = "+"\'"+password+"\';";

            ResultSet rs  = st.executeQuery(sql);



            if (rs.getString("Name") != null){

                return true;
            }
            else{
                return false;
            }

        } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        return null;
        }



    }

    public static void registerAppointment(Connection c,String appnmentDate,String username,String fodraszName,String description){

        try {
            String tablename = "Calendar";
            c.setAutoCommit(false);
            Statement st = c.createStatement();

            ResultSet loggedinresult = getUserInfo(c,username);
            String fullname = loggedinresult.getString("FullName");

            String nextfree = "";
            String apntype = description.split("/")[0];

            switch (apntype){
                case "Ferfi hajvagas" -> {
                    nextfree= (appnmentDate.split(" ")[1]).substring(0,5);
                }
                case "Noi hajvagas" -> {
                    String hourfull = (appnmentDate.split(" ")[1]).substring(0,5);
                    String hour = hourfull.split(":")[0];
                    String mins = hourfull.split(":")[1];

                    String nexthour = Integer.toString(Integer.parseInt(hour)+1);

                    if (mins.equals("30")){
                        nextfree=hourfull + "," + nexthour +":00" + "," + nexthour + ":30";
                    }
                    else if (mins.equals("00")) {
                        nextfree=hourfull + "," + hour +":30" + "," + nexthour + ":00";
                    }


                }
                case "Hajfestes" -> {

                    String hourfull = (appnmentDate.split(" ")[1]).substring(0,5);
                    String hour = hourfull.split(":")[0];
                    String mins = hourfull.split(":")[1];

                    String nexthour = Integer.toString(Integer.parseInt(hour)+1);

                    if (mins.equals("30")){
                        nextfree=hourfull + "," + nexthour +":00";
                    }
                    else if (mins.equals("00")) {
                        nextfree=hourfull + "," + hour +":30";
                    }

                }

            }

            String tmp = description+"/"+fullname;

            String sql = String.format("INSERT INTO %s (Appointment,Username,FodraszName,Description,NextFreeHour) VALUES (\'%s\',\'%s\',\'%s\',\'%s\',\'%s\');",tablename,appnmentDate,username,fodraszName,tmp,nextfree);

            st.executeUpdate(sql);
            c.commit();
            st.close();
            c.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }



    }

    public static ResultSet getAppointmentWeek(Connection c,String startdate, String enddate) {

        try {


            Statement st = c.createStatement();
            c.setAutoCommit(false);

            String sql = "select * from Calendar where Appointment >= " + " \'" + startdate + "\'" + " AND Appointment <= " + "\'" +enddate +"\'"+";";
            ResultSet calendarresult = st.executeQuery(sql);

            return calendarresult;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }



    }

    public static ResultSet getAppointmentDay(Connection c,String day) {

        try {
            String daystart = day + " 01:01:01";
            String dayend = day + " 24:01:01";

            Statement st = c.createStatement();
            c.setAutoCommit(false);
            String sql = "select * from Calendar where Appointment >= " + " \'" + daystart + "\'" + " AND Appointment <= " + "\'" + dayend  +"\'"+";";
            ResultSet calendarresult = st.executeQuery(sql);

            return calendarresult;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }



    }

    public static ResultSet getUserAppointments(Connection c,String username) {

        try {
            Statement st = c.createStatement();
            c.setAutoCommit(false);
            String sql = "select Appointment from Calendar where Username = " + " \'" + username + "\'"+";";

            return st.executeQuery(sql);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }



    }

    public static void deleteSelectedApn(Connection c,String apn,String username) {

        try {

            Statement st = c.createStatement();

            String sql = "DELETE FROM Calendar WHERE Appointment = "+"\'"+apn+"\'" + "AND Username = "+ "\'"+ username +"\'" +";";


            c.setAutoCommit(false);
            st.executeUpdate(sql);
            c.commit();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

        }



    }

    public static void updateUserFields(Connection c,String field,String value,String username) throws SQLException {
        Statement st = c.createStatement();
        c.setAutoCommit(false);

        if (field.equals("password")){
            String sql = "UPDATE LoginInfo SET Password = \'"+value+"\'"+" WHERE Name = "+"\'"+username+"\';";

            st.executeUpdate(sql);
            c.commit();

        }
        if(field.equals("address")){
            String sql = "UPDATE Person SET Address = \'"+value+"\' WHERE Username = "+"\'"+username+"\';";

            st.executeUpdate(sql);
            c.commit();

        }
        if(field.equals("phone")){
            String sql = "UPDATE Person SET Phone = \'"+value+"\' WHERE Username = "+"\'"+username+"\';";

            st.executeUpdate(sql);
            c.commit();

        }

        st.close();


    }

    public static ResultSet getAllUserAppointments(Connection c) {

        try {
            Statement st = c.createStatement();
            c.setAutoCommit(false);
            String sql = "select Appointment,Description from Calendar;";

            return st.executeQuery(sql);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }



    }

    public static ResultSet getUsernameByFullname(Connection c,String fullname) {

        try {
            Statement st = c.createStatement();
            c.setAutoCommit(false);
            String loggedinsql = "select Username from Person where FullName = " + " \'" + fullname + "\'" + " ;";
            ResultSet loggedinresult = st.executeQuery(loggedinsql);

            return loggedinresult;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }



    }



}
