/*
Dimitrios Papageorgacopoulos
ptdimitrios@gmail.com
6 April 2018
Project A9
CS 17.11 Java

This file is a driver which compiles all of the data supplied
in the CSV files into tables in a sqlite database.

The program then utilizes sql commands to filter out all dates that don't exist
for all three tables and builds a table out of those remaining data points. A graph
is then constructed using that final  table.
 */

package edu.srjc.papageorgacopoulos.dimitrios.A9;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.sql.*;

import javax.management.InvalidAttributeValueException;
import java.io.File;
import java.util.*;

import static java.sql.DriverManager.*;


public class Main extends Application
{

    private static final File elec = new File("pge_electric_interval_data_2016-01-01_to_2016-02-28.csv");
    private static final File gas = new File("pge_gas_interval_data_2016-01-01_to_2016-03-01.csv");
    private static final File KCASONOM43 = new File("KCASONOM43.csv");

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        /*
        I got the code to display multiple series on a single line chart from this site
        https://docs.oracle.com/javafx/2/charts/line-chart.htm
        */
        setupSQLDatabases();

        TreeMap<String, HomeDataPoint> dataToGraph = getData();

        primaryStage.setTitle("HomeDataPoint Chart");

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        final LineChart<String,Number> lineChart =
                new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("Home Data");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("kWh");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Therms");

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Hottest Temp");

        XYChart.Series series4 = new XYChart.Series();
        series4.setName("Coldest Temp");

        for(String day: dataToGraph.keySet())
        {

            series1.getData().add(new XYChart.Data(day, dataToGraph.get(day).getkWh()));
            series2.getData().add(new XYChart.Data(day, dataToGraph.get(day).getTherms()));
            series3.getData().add(new XYChart.Data(day, dataToGraph.get(day).getHighestTemp()));
            series4.getData().add(new XYChart.Data(day, dataToGraph.get(day).getLowestTemp()));
        }


        Scene scene = new Scene(lineChart, 800, 600);

        lineChart.getData().addAll(series1, series2, series3, series4);

        primaryStage.setScene(scene);

        primaryStage.show();


    }


    public TreeMap<String, HomeDataPoint> getData() throws SQLException
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
        }

        catch (ClassNotFoundException e)
        {
            System.err.println("Cannot load SQLite-JDBC: Fatal Error");
            System.exit(0);
        }

        Connection connection = null;

        String dbName = "HomeDataDatabase.sqlite";

        try
        {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbName));
        }

        catch (SQLException e)
        {
            System.out.println("Connection failed");
            e.printStackTrace();
            System.exit(0);
        }

        TreeMap<String, HomeDataPoint> queryResultList = new TreeMap<>();

        Statement statement = connection.createStatement();

        ResultSet set = statement.executeQuery("Select * From HomeData");


        while(set.next())
        {
            String date = set.getString("Date");
            Double lowestTemp = set.getDouble("LowestTemp");
            Double highestTemp = set.getDouble("highestTemp");
            Double therms = set.getDouble("Therms");
            Double kWh = set.getDouble("kWh");
            HomeDataPoint pointToAdd = null;

            try
            {
                pointToAdd = new HomeDataPoint(date,highestTemp,lowestTemp,therms,kWh);

                queryResultList.put(pointToAdd.getDay(), pointToAdd);
            }
            catch (InvalidAttributeValueException e)
            {
                e.printStackTrace();
            }
        }
        return  queryResultList;
    }

    public static TreeMap<String, Pair<Double, Double>> gatherWeatherData()
    {

        Scanner dataFile = null;

        try
        {
            dataFile = new Scanner(new File(String.valueOf(KCASONOM43)));
        }

        catch (Exception e)
        {
            System.out.println("It would appear this file does not exist");

            System.exit(1);
        }



        ArrayList<String> errors = new ArrayList<>();

        ArrayList<WeatherDataPoint> dataPointArrayList = new ArrayList<>();

        HashMap<String, ArrayList<WeatherDataPoint>> dailyReadings = new HashMap<>();

        String line = "";

        int lineNumber = 1;


        while (dataFile.hasNext()){
            lineNumber += 1;

            line = dataFile.nextLine().trim();

            WeatherDataPoint singlePoint = null;

            try
            {
                singlePoint = new WeatherDataPoint(line);

                dataPointArrayList.add(singlePoint);
            }

            catch (Exception e)
            {
                if(e.getMessage().equals("That line appears to be empty"))
                {
                    errors.add(String.format("%s Line %d", e.getMessage(), lineNumber));
                    continue;
                }

                else
                {
                    errors.add(String.format("%s Line %d", e.getMessage(), lineNumber));
                    continue;
                }
            }
        }

        dataFile.close();


        for (WeatherDataPoint currentPoint: dataPointArrayList)
        {
            if(dailyReadings.get(currentPoint.getDateTime()) == null)
            {
                dailyReadings.put(currentPoint.getDateTime(),
                        new ArrayList<WeatherDataPoint>(Arrays.asList(currentPoint)));
            }

            else
            {
                dailyReadings.get(currentPoint.getDateTime()).add(currentPoint);
            }
        }


        TreeMap<String, ArrayList> sortedDays = new TreeMap<>(dailyReadings);

        HashMap<String, Pair<Double, Double>> dailyTemps = new HashMap<>();

        for (String date : sortedDays.keySet())
        {

            Double lowestTemp = 1000.0;
            Double highestTemp = -1000.0;

            for (WeatherDataPoint currentData : dailyReadings.get(date))
            {
                if (currentData.getOutTemp() > highestTemp)
                {
                    Long roundedSum = Math.round(currentData.getOutTemp() * 100.0);

                    double roundedHighestTemp = roundedSum / 100.0;

                    highestTemp = roundedHighestTemp;
                }

                if (currentData.getOutTemp() < lowestTemp)
                {
                    Long roundedSum = Math.round(currentData.getOutTemp() * 100.0);

                    double roundedLowestTemp = roundedSum / 100.0;

                    lowestTemp = roundedLowestTemp;
                }

                else
                {
                    continue;
                }

                Pair<Double, Double> temps = new Pair<>(highestTemp, lowestTemp);

                dailyTemps.put(date, temps);

            }
        }


        TreeMap<String, Pair<Double, Double>> sortedWeatherData = new TreeMap<>(dailyTemps);

/*
        for (String day:sortedWeatherData.keySet())
        {
            System.out.println(day + "temps: " + sortedWeatherData.get(day).getKey() + " " + sortedWeatherData.get(day).getValue());
        }
*/
        return sortedWeatherData;
    }


    public static TreeMap<String, Double> gatherGasData()
    {
        Scanner dataFile = null;

        try
        {
            dataFile = new Scanner(new File(String.valueOf(gas)));
        }

        catch (Exception e)
        {
            System.out.println("It would appear this file does not exist");

            System.exit(1);
        }

        ArrayList<String> gasErrors = new ArrayList<>();

        ArrayList<GasDataObject> gasDataPointList = new ArrayList<>();

        HashMap<String, ArrayList<GasDataObject>> dailyReadings =
                new HashMap<>();

        String line = "";

        int lineNumber = 0;

        while (dataFile.hasNext())
        {

            lineNumber += 1;

            line = dataFile.nextLine().trim();

            GasDataObject singlePoint = null;

            try
            {
                singlePoint = new GasDataObject(line);

                gasDataPointList.add(singlePoint);
            }

            catch (Exception e)
            {
                if (e.getMessage().equals("That line appears to be empty"))
                {
                    gasErrors.add(String.format("%s Line %d", e.getMessage(), lineNumber));
                    continue;
                }

                else
                {
                    gasErrors.add(String.format("%s Line %d", e.getMessage(), lineNumber));
                    continue;
                }
            }
        }

        dataFile.close();

        for (GasDataObject currentPoint : gasDataPointList)
        {
            if (dailyReadings.get(currentPoint.getDate()) == null)
            {
                dailyReadings.put(currentPoint.getDate(),
                        new ArrayList<GasDataObject>(Arrays.asList(currentPoint)));
            }

            else
            {
                dailyReadings.get(currentPoint.getDate()).add(currentPoint);
            }
        }




        HashMap<String, Double> summedTherms = new HashMap<>();

        for (String date : dailyReadings.keySet())
        {

            Double dailySum = 0.0;

            for (GasDataObject currentObject : dailyReadings.get(date))
            {
                dailySum += currentObject.getTherms();
            }

            Long roundedSum = Math.round(dailySum * 1000.0);

            double truncatedRoundedSum = roundedSum / 1000.0;

            summedTherms.put(date, truncatedRoundedSum);
        }

/*
        for (String date : summedTherms.keySet())
        {
            System.out.println(date + " : Therms are  " + summedTherms.get(date));
        }
*/
        TreeMap<String, Double> sortedThermalData = new TreeMap<>(summedTherms);

        return sortedThermalData;
    }


    public static TreeMap<String, Double> gatherElectricityData()
    {

        Scanner dataFile = null;

        try
        {
            dataFile = new Scanner(new File(String.valueOf(elec)));
        }

        catch (Exception e)
        {
            System.out.println("It would appear this file does not exist");

            System.exit(1);
        }


        ArrayList<String> electricityErrors = new ArrayList<>();

        ArrayList<ElectricityDataObject> electricityDataPointList = new ArrayList<>();

        HashMap<String, ArrayList<ElectricityDataObject>> dailyReadings =
                new HashMap<>();

        String line = "";

        int lineNumber = 1;


        while (dataFile.hasNext())
        {

            lineNumber += 1;

            line = dataFile.nextLine().trim();

            ElectricityDataObject singlePoint = null;

            try
            {
                singlePoint = new ElectricityDataObject(line);

                electricityDataPointList.add(singlePoint);
            }

            catch (Exception e)
            {
                if (e.getMessage().equals("That line appears to be empty"))
                {
                    electricityErrors.add(String.format("%s Line %d", e.getMessage(), lineNumber));
                    continue;
                }

                else
                {
                    electricityErrors.add(String.format("%s Line %d", e.getMessage(), lineNumber));
                    continue;
                }
            }
        }

        dataFile.close();


        for (ElectricityDataObject currentPoint : electricityDataPointList)
        {
            if (dailyReadings.get(currentPoint.getDate()) == null)
            {
                dailyReadings.put(currentPoint.getDate(),
                        new ArrayList<ElectricityDataObject>(Arrays.asList(currentPoint)));
            }

            else
            {
                dailyReadings.get(currentPoint.getDate()).add(currentPoint);
            }
        }

        TreeMap<String, ArrayList> sortedDays = new TreeMap<>(dailyReadings);


        HashMap<String, Double> summedkWh = new HashMap<>();

        for (String date : dailyReadings.keySet())
        {

            Double dailySum = 0.0;

            for (ElectricityDataObject currentObject : dailyReadings.get(date))
            {
                dailySum += currentObject.getkWh();
            }

            Long roundedSum = Math.round(dailySum * 1000.0);

            double truncatedRoundedSum = roundedSum / 1000.0;

            summedkWh.put(date, truncatedRoundedSum);
        }
/*
            for (String date : summedkWh.keySet())
            {
                System.out.println(date + " :Electricty " + summedkWh.get(date));
            }
*/
        TreeMap<String, Double> sortedElectricalData = new TreeMap<>(summedkWh);

        return sortedElectricalData;
    }


    public static TreeMap<String, HomeDataPoint> synthesizeData()
    {


        TreeMap<String, Double> electricityData = new TreeMap<>(gatherElectricityData());
        TreeMap<String, Double> gasData = new TreeMap<>(gatherGasData());
        TreeMap<String, Pair> weatherData = new TreeMap<>(gatherWeatherData());

        TreeMap<String, HomeDataPoint> synthesizedTreeMap = new TreeMap<>();

        HashSet<String> sharedDays = new HashSet<>();


        for(String date: gatherElectricityData().keySet())
        {
            if(gasData.containsKey(date) && weatherData.containsKey(date))
            {
                sharedDays.add(date);
            }

            else
            {
                continue;
            }
        }

/*
        System.out.println("Here are the shared dates: ");

        for(String day: sharedDays)
        {
            System.out.println(day);
        }
*/


        for(String date: sharedDays)
        {

            Pair<Double,Double> weatherDataPair = weatherData.get(date);

            Pair<Double, Double> gasAndElectricityPair =
                    new Pair<>(gasData.get(date), electricityData.get(date));

            try
            {
                HomeDataPoint allData = new HomeDataPoint(date, weatherDataPair, gasAndElectricityPair);

                synthesizedTreeMap.put(date, allData);
            }

            catch(InvalidAttributeValueException e)
            {
                continue;
            }


        }


/*
        for(String day: synthesizedTreeMap.keySet())
        {
            System.out.println(synthesizedTreeMap.get(day).toString());
        }

*/
        return synthesizedTreeMap;
    }

    public static void setupSQLDatabases() throws SQLException
    {

        try
        {
            Class.forName("org.sqlite.JDBC");
        }

        catch (ClassNotFoundException e)
        {
            System.err.println("Cannot load SQLite-JDBC: Fatal Error");
            System.exit(0);
        }

        Connection connection = null;

        String dbName = "HomeDataDatabase.sqlite";

        try
        {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbName));
        }

        catch (SQLException e)
        {
            System.out.println("Connection failed");
            e.printStackTrace();
            System.exit(0);
        }



        TreeMap<String, Double> electricityDataToInsert = new TreeMap<>(gatherElectricityData());
        TreeMap<String, Double> gasDataToInsert = new TreeMap<>(gatherGasData());
        TreeMap<String, Pair<Double, Double>> weatherDataToInsert = new TreeMap<>(gatherWeatherData());



        try
        {
            Statement statement = connection.createStatement();

            statement.setQueryTimeout(5);

            statement.executeUpdate("DROP TABLE IF EXISTS ElectricityData");

            statement.executeUpdate("CREATE TABLE ElectricityData (Date TEXT, kWh Numeric)");

            for (String date : electricityDataToInsert.keySet())
            {
                Double kWh = electricityDataToInsert.get(date);

                statement.executeUpdate(String.format("Insert into ElectricityData(date, kWh) values (%s, %f)",
                        date, kWh));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }


        try
        {
            Statement statement = connection.createStatement();

            statement.setQueryTimeout(5);

            statement.executeUpdate("DROP TABLE IF EXISTS GasData");

            statement.executeUpdate("CREATE TABLE GasData (Date TEXT, Therms Numeric)");

            for (String date : gasDataToInsert.keySet())
            {
                Double therms = gasDataToInsert.get(date);

                statement.executeUpdate(String.format("Insert into GasData(date, Therms) values (%s, %f)",
                        date, therms));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }


        try
        {
            Statement statement = connection.createStatement();

            statement.setQueryTimeout(5);

            statement.executeUpdate("DROP TABLE IF EXISTS WeatherData");

            statement.executeUpdate("CREATE TABLE WeatherData (Date TEXT, LowestTemp Numeric, HighestTemp Numeric)");

            for (String date : weatherDataToInsert.keySet())
            {
                Double lowestTemp = weatherDataToInsert.get(date).getValue();
                Double highestTemp = weatherDataToInsert.get(date).getKey();

                statement.executeUpdate(String.format("Insert into WeatherData(date, LowestTemp, HighestTemp) values (%s, %f, %f)",
                        date, lowestTemp, highestTemp));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }


       try
       {
           Statement statement = connection.createStatement();

           statement.setQueryTimeout(5);

           statement.executeUpdate("DROP TABLE IF EXISTS HomeData");

           statement.executeUpdate("CREATE TABLE HomeData (Date TEXT, LowestTemp Numeric, HighestTemp Numeric, " +
                   "kWh Numeric, Therms Numeric)");

            statement.executeUpdate("Insert into Homedata Select * From WeatherData " +
                    "Natural Join ElectricityData Natural Join GasData");
       }

        catch (Exception e)
        {
            e.printStackTrace();
        }


       return;

    }

}


