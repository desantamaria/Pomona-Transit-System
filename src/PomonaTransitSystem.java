/*
MySQL and Visual Studio Code was used to create and test this program.
Daniel Santamaria
*/

import java.sql.*;
import java.util.Scanner;

public class PomonaTransitSystem {
   static final String DB_URL = "jdbc:mysql://127.0.0.1/transit";
   static final String USER = "root";
   static final String PASS = "password"; 
   public static void main(String[] args) {
	// Open a connection
   try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
	Statement stmt = conn.createStatement();){

      //For repeated runs...
      stmt.executeUpdate("DROP DATABASE TRANSIT");
      System.out.println("Database deleted successfully...");
        
      stmt.executeUpdate("CREATE DATABASE TRANSIT");
      System.out.println("Database created successfully...");

      stmt.executeUpdate("USE TRANSIT");
      System.out.println("Database used successfully...");

      //Create the Tables
      CreateTable("CREATE TABLE TRIP " +
         "(TripNumber INTEGER not NULL, " +
         " StartLocationName VARCHAR(255), " + 
         " DestinationName VARCHAR(255), " +  
         " PRIMARY KEY ( TripNumber ))", "TRIP" );
   
      CreateTable("CREATE TABLE TRIPOFFERING " +
         "(TripNumber INTEGER not NULL, " +
         " Date DATE, " + 
         " ScheduledStartTime VARCHAR(255), " +
         " ScheduledArrivalTime VARCHAR(255), " +  
         " DriverName VARCHAR(255), " +
         " BusID INTEGER, " +    
         " PRIMARY KEY ( TripNumber, Date, ScheduledStartTime ))", "TRIPOFFERING"); 

      CreateTable ("CREATE TABLE BUS " +
         "(BusID INTEGER not NULL, " +
         " Model VARCHAR(255), " + 
         " Year VARCHAR(255)," +    
         " PRIMARY KEY ( BusID ))", "BUS"); 

      CreateTable("CREATE TABLE DRIVER " +
         "(DriverName VARCHAR(255) not NULL, " +
         " DriverTelephoneNumber VARCHAR(255), " +     
         " PRIMARY KEY ( DriverName ))", "DRIVER"); 

      CreateTable("CREATE TABLE STOP " +
         "(StopNumber INTEGER not NULL, " +
         " StopAddress VARCHAR(255), " + 
         " PRIMARY KEY ( StopNumber ))", "STOP"); 

      CreateTable("CREATE TABLE ACTUALTRIPSTOPINFO " +
         "(TripNumber INTEGER not NULL, " +
         " Date DATE, " + 
         " ScheduledStartTime VARCHAR(255), " +
         " StopNumber INTEGER, " +
         " ScheduledArrivalTime VARCHAR(255), " +
         " ActualStartTime VARCHAR(255), " +
         " ActualArrivalTime VARCHAR(255), " +
         " NumberOfPassengerIn INTEGER, " +
         " NumberOfPassengerOut INTEGER, " +  
         " PRIMARY KEY ( TripNumber, Date, ScheduledStartTime, StopNumber ))", "ACTUALTRIPSTOPINFO"); 

      CreateTable("CREATE TABLE TRIPSTOPINFO " +
        "(TripNumber INTEGER not NULL, " +
        " StopNumber INTEGER, " +
        " SequenceNumber INTEGER, " +
        " DrivingTime VARCHAR(255), " +
        " PRIMARY KEY ( TripNumber, StopNumber ))", "TRIPSTOPINFO"); 

      System.out.println("inserting test data into tables...");
         
      InsertTestData(); // insert example data
      promptEnterKey();     
      
      //Task 1 Displaying the schedule of all trips for a given key
      Task1();
      promptEnterKey();

      //Task 2 TripOffering
      Task2();
      promptEnterKey();

      //Task 3 Displaying stops of a given trip
      Task3();
      promptEnterKey();

      //Task 4 Displaying the weekly schedule of a given driver and date
      Task4();
      promptEnterKey();

      //Task 5 Adding a drive
      Task5();
      promptEnterKey();

      //Task 6 Adding a Bus
      Task6();
      promptEnterKey();

      //Task 7 Deleting a Bus
      Task7();
      promptEnterKey();

      //Task 8: Recording actual data of given trip offering
      Task8();

      } catch (SQLException e) {
         e.printStackTrace();
      } 
   }
   public static void CreateTable(String Query, String TableName) {
         try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         Statement stmt = conn.createStatement();) {
            stmt.executeUpdate(Query);
            System.out.println("Created "+ TableName +" table in given database...");
         }
         catch (SQLException e){
            e.printStackTrace();
         }
      }

   public static void InsertDataTable(String TableName, String Data) {
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {   
         stmt.executeUpdate("INSERT INTO " + TableName + " VALUES (" + Data + ")");
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }

   public static void InsertTestData() {
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
         
         InsertDataTable("TRIP", "1, \"LocationA\", \"DestinA\"");         
         InsertDataTable("TRIP", "2, \"LocationB\", \"DestinB\"");
         InsertDataTable("TRIP", "3, \"LocationC\", \"DestinC\"");
         InsertDataTable("TRIP", "4, \"LocationD\", \"DestinD\"");
         InsertDataTable("TRIP", "5, \"LocationE\", \"DestinE\"");
         
         //DATE: YYYY-MM-DD
         InsertDataTable("TRIPOFFERING", "1, \"2021-12-02\", \"10:00AM\", \"11:00AM\", \"Daniel San\", 1234");
         InsertDataTable("TRIPOFFERING", "2, \"2021-11-30\", \"2:15PM\", \"3:15PM\", \"John Dee\", 2222");
         InsertDataTable("TRIPOFFERING", "3, \"2021-11-30\", \"11:30AM\", \"12:30PM\", \"Eddie Reed\", 3333");
         InsertDataTable("TRIPOFFERING", "4, \"2021-11-05\", \"6:00PM\", \"7:00PM\", \"Maria Lopez\", 1111");
         InsertDataTable("TRIPOFFERING", "5, \"2021-12-13\", \"5:45PM\", \"6:45PM\", \"Daniel San\", 1234");
         
         InsertDataTable("BUS", "1234, \"2XMinibus\", \"2015\"");
         InsertDataTable("BUS", "1111, \"3TrSingleDeck\", \"2013\"");
         InsertDataTable("BUS", "2222, \"4XMinibus\", \"2018\"");
         InsertDataTable("BUS", "3333, \"12BNCoach\", \"2017\"");

         InsertDataTable("DRIVER", "\"Daniel San\", \"909-123-4567\"");
         InsertDataTable("DRIVER", "\"John Dee\", \"909-111-2222\"");
         InsertDataTable("DRIVER", "\"Eddie Reed\", \"909-333-4444\"");
         InsertDataTable("DRIVER", "\"Maria Lopez\", \"909-555-6666\"");

         InsertDataTable("STOP", "01, \"301 S Garey Ave\"");
         InsertDataTable("STOP", "02, \"200 W. Second Street\"");
         InsertDataTable("STOP", "03, \"3801 W Temple Ave\"");
         InsertDataTable("STOP", "04, \"1101 W McKinley Ave\"");
         InsertDataTable("STOP", "05, \"2640 Pomona Blvd\"");
         InsertDataTable("STOP", "06, \"281 S Thomas St\"");

         InsertDataTable("ACTUALTRIPSTOPINFO", "1, \"2021-12-02\", \"10:00AM\", 01, \"11:00AM\"" + 
            ", \"10:05AM\", \"11:10AM\", 5, 4");
         InsertDataTable("ACTUALTRIPSTOPINFO", "2, \"2021-11-30\", \"2:15PM\", 02, \"3:15PM\"" + 
            ", \"2:16PM\", \"3:20APM\", 7, 6");
         InsertDataTable("ACTUALTRIPSTOPINFO", "3, \"2021-11-30\", \"11:30AM\", 03, \"12:30PM\"" + 
            ", \"11:35PM\", \"12:29AM\", 8, 4");
         InsertDataTable("ACTUALTRIPSTOPINFO", "4, \"2021-11-05\", \"6:00PM\", 04, \"7:00PM\"" + 
            ", \"6:03PM\", \"7:01PM\", 6, 1");
         InsertDataTable("ACTUALTRIPSTOPINFO", "5, \"2021-12-13\", \"5:45PM\", 05, \"6:45PM\"" + 
            ", \"5:45PM\", \"6:47PM\", 7, 2");
         
         InsertDataTable("TRIPSTOPINFO", "1, 01, 1234, \"0.5hr\"");
         InsertDataTable("TRIPSTOPINFO", "2, 02, 4321, \"0.4hr\"");
         InsertDataTable("TRIPSTOPINFO", "3, 03, 1122, \"0.5hr\"");
         InsertDataTable("TRIPSTOPINFO", "4, 04, 2211, \"0.3hr\"");
         InsertDataTable("TRIPSTOPINFO", "5, 05, 3311, \"0.5hr\"");
         
         System.out.println("Test Data successfully inserted...");
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }

   public static void Task1(){
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
   
         String TASK1 = "SELECT T.StartLocationName, T.DestinationName, O.Date, O.ScheduledStartTime, " + 
                        "O.ScheduledArrivalTime, O.DriverName, O.BusID " +
                        "FROM Trip AS T INNER JOIN TripOffering as O ON T.TripNumber = O.TripNumber";

         System.out.println("Task 1 SELECT Query:\n");
         ResultSet rs = stmt.executeQuery(TASK1);

         //Extract data from result set
         while (rs.next()) {
            // Retrieve by column name
            System.out.print("Starting Location: " + rs.getString("StartLocationName"));
            System.out.print(", Destination: " + rs.getString("DestinationName"));
            System.out.print(", Date: " + rs.getDate("Date"));
            System.out.print(", Scheduled Start Time: " + rs.getString("ScheduledStartTime"));
            System.out.print(", Scheduled Arrival Time: " + rs.getString("ScheduledArrivalTime"));
            System.out.print(", Driver Name: " + rs.getString("DriverName"));
            System.out.println(", Bus ID: " + rs.getInt("BusID"));
         }
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }
   public static void Task2(){
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
         
         //Task 2a
         String TASK2a = "DELETE " +
                        "FROM TripOffering " +
                        "WHERE TripNumber = 3 AND Date = \"2021-11-30\" AND ScheduledStartTime = \"11:30AM\"";
         System.out.println("\nTask 2a: Deletion on TRIPOFFERING Based on TripNumber=3, Date=2021-11-30, 11:30AM...");
         stmt.executeUpdate(TASK2a);
         
         System.out.println("Deletion successful!");
         promptEnterKey();
         
         //Task 2b
         System.out.println("\nTask 2b: Add a set of trip offerings");
         InsertDataTable("TRIPOFFERING", "3, \"2021-10-28\", \"9:00AM\", \"9:50AM\", \"John Dee\", 3333");

         System.out.println("Would you like to enter another Trip?");
         Scanner yesOrNo = new Scanner(System.in);

         while(true)
         {
            String line = yesOrNo.nextLine();
            if(line.equalsIgnoreCase("Y")){
                  
               while(line.equalsIgnoreCase("Y")) {
                  
                  System.out.println("Enter Trip Number: ");
                  Scanner TripNumberInput = new Scanner(System.in);
                  String TripNumber = TripNumberInput.nextLine();
                  System.out.println("Enter Name of Starting Location: ");
                  Scanner StartingLocationInput = new Scanner(System.in);
                  String StartingLocationName = StartingLocationInput.nextLine();
                  System.out.println("Enter Name of Destination: ");
                  Scanner DestinationInput = new Scanner(System.in);
                  String DestinationName = DestinationInput.nextLine();
                  
                  String UserData = "\"" + TripNumber + "\", \"" + StartingLocationName + "\", \"" + DestinationName + "\"";
                  
                  InsertDataTable("Trip", UserData);

                  System.out.println("\nWould you like to enter another row of Data?");
                  Scanner yesOrNo2 = new Scanner(System.in);
                  line = yesOrNo2.nextLine();
               }   
               break;
            }else if(line.equalsIgnoreCase("N") || line.equalsIgnoreCase("n")){
               break;
            }else{
               System.out.println("Please enter Y or N ");
            }
         }
         promptEnterKey();
         
         //Task 2c
         System.out.println("\nTask 2c: Changing the driver for a given Trip offering ");
         stmt.executeUpdate("UPDATE TripOffering " +
            "SET DriverName = \"Maria Lopez\" " + 
            "WHERE TripNumber = 1 AND Date = \"2021-12-02\" AND ScheduledStartTime = \"10:00AM\";");
         promptEnterKey();

         //Task2d
         System.out.println("\nTask 2d: Changing the bus for a given Trip offering ");
         stmt.executeUpdate("UPDATE TripOffering " +
         "SET BusID = 1111 " + 
         "WHERE TripNumber = 1 AND Date = \"2021-12-02\" AND ScheduledStartTime = \"10:00AM\";");

         System.out.println("The Results are...");
         ResultSet rs =stmt.executeQuery("SELECT * FROM TRIPOFFERING");
         
         while (rs.next()) {
            System.out.print("Trip Number: " + rs.getInt("TripNumber"));
            System.out.print(", Date: " + rs.getDate("Date"));
            System.out.print(", Scheduled Start Time: " + rs.getString("ScheduledStartTime"));
            System.out.print(", Scheduled Arrival Time: " + rs.getString("ScheduledArrivalTime"));
            System.out.print(", Driver Name: " + rs.getString("DriverName"));
            System.out.println(", Bus ID: " + rs.getString("BusID"));
         }
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }
   public static void Task3(){
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
         System.out.println("\n Task 3: Displaying Stops given by the TRIPSTOPINFO Table...");
         ResultSet rs =stmt.executeQuery("SELECT * FROM TRIPSTOPINFO");
         
         while (rs.next()) {
            // Retrieve by column name
            System.out.print("Trip Number: " + rs.getInt("TripNumber"));
            System.out.print(", Stop Number: " + rs.getInt("StopNumber"));
            System.out.print(", Sequence Number: " + rs.getInt("SequenceNumber"));
            System.out.println(", Driving Time: " + rs.getString("DrivingTime"));
         }
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }
   public static void Task4(){
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
         System.out.println("\n Task 4: Displaying Schedule of Daniel San in December...");
         ResultSet rs =stmt.executeQuery("SELECT * FROM TripOffering AS T " +
                                       "WHERE T.DriverName=\"Daniel San\" AND T.Date LIKE \"%-12-%\"");
         
         while (rs.next()) {
            // Retrieve by column name
            System.out.print("Trip Number: " + rs.getInt("TripNumber"));
            System.out.print(", Date: " + rs.getDate("Date"));
            System.out.print(", Scheduled Start Time: " + rs.getString("ScheduledStartTime"));
            System.out.print(", Scheduled Arrival Time: " + rs.getString("ScheduledArrivalTime"));
            System.out.println(", Driver Name: " + rs.getString("DriverName"));
         }
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }
   public static void Task5(){
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
         System.out.println("\n Task 5: Adding a new Driver...");
         InsertDataTable("DRIVER", "\"Gloria Guevarra\", \"909-999-9999\"");
         System.out.println("\n Added a new driver...");
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }
   public static void Task6(){
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
         System.out.println("\n Task 6: Adding new Buses...");
         InsertDataTable("BUS", "9999, \"7XMinibus\", \"2012\"");
         InsertDataTable("BUS", "8888, \"3XRCoach\", \"2014\"");
         System.out.println("\n Added the new buses...");   
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }
   public static void Task7(){
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
         
         String TASK7 = "DELETE " +
                        "FROM BUS " +
                        "WHERE BUSID = 9999";
         System.out.println("\nTask 7: Deleting a Bus...");
         stmt.executeUpdate(TASK7);
         
         System.out.println("The Results are...");
         ResultSet rs =stmt.executeQuery("SELECT * FROM BUS");
         while (rs.next()) {
            System.out.print("BusID: " + rs.getInt("BusID"));
            System.out.print(", Model: " + rs.getString("Model"));
            System.out.println(", Year: " + rs.getString("Year"));      
         }
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }
   public static void Task8(){
      try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
      Statement stmt = conn.createStatement();) {
         stmt.executeUpdate("UPDATE ActualTripStopInfo " +
            "SET ActualStartTime = \"6:04PM\" " + 
            "WHERE TripNumber = 4 AND Date = \"2021-11-05\" AND ScheduledStartTime = \"6:00PM\";");
         
            stmt.executeUpdate("UPDATE ActualTripStopInfo " +
            "SET ActualArrivalTime = \"7:03PM\" " + 
            "WHERE TripNumber = 4 AND Date = \"2021-11-05\" AND ScheduledStartTime = \"6:00PM\";");
         
         System.out.println("\nTask 8: Inserting data into Actual Start/Arrival in ActualTripStopInfo ");

         System.out.println("The results are...");
         ResultSet rs = stmt.executeQuery("SELECT * FROM ACTUALTRIPSTOPINFO");

         //Extract data from result set
         while (rs.next()) {
            // Retrieve by column name
            System.out.print("Trip Number: " + rs.getInt("TripNumber"));
            System.out.print(", Date: " + rs.getDate("Date"));
            System.out.print(", Scheduled Start Time: " + rs.getString("ScheduledStartTime"));
            System.out.print(", Scheduled Arrival Time: " + rs.getString("ScheduledArrivalTime"));
            System.out.print(", Actual Start Time: " + rs.getString("ActualStartTime"));
            System.out.println(", Actual Arrival Time: " + rs.getString("ActualArrivalTime"));
         }
      }
      catch (SQLException e){
         e.printStackTrace();
      }
   }
   
   public static void promptEnterKey(){
      System.out.println("Press \"ENTER\" to continue...");
      Scanner scanner = new Scanner(System.in);
      scanner.nextLine();
   }
   
   
   



}