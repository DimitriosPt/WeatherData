/*
Dimitrios Papageorgacopoulos
ptdimitrios@gmail.com
16 February 2018
Project A9
Java Programming CS 17.11

This Class creates a WeatherDataPoint object which consists of a String which holds
the date and a pair of doubles which holds the highest and lowest temperatures.
 */
package edu.srjc.papageorgacopoulos.dimitrios.A9;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherDataPoint extends ArrayList<WeatherDataPoint>
{

    private String dateTime;
    private double outTemp;

    @Override

    public String toString()
    {
        return "WeatherDataPoint{" +
                "dateTime='" + dateTime + '\'' +
                ", outTemp='" + outTemp + '\'' +
                '}';
    }

    public WeatherDataPoint(String line) throws Exception
    {
        if (line.length() == 0 || line.charAt(0) == '#')
        {
            throw new IllegalStateException("Empty line");
        }


        String[] dataPoint = line.split(",");


        if (dataPoint.length < 8)
        {
            throw new IllegalArgumentException("\n There are an invalid number of data points");
        }


        String dateField = dataPoint[0].replaceAll("\"", "");

        /*
        Conversion of Epoch to Date format came from here:

        //https://stackoverflow.com/questions/535004/unix-epoch-time-to-java-date-object

        https://stackoverflow.com/questions/11046053/how-to-format-date-string-in-java //https://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html _
        */

        long epoch = Long.parseLong(dateField);
        Date d = new Date(epoch * 1000);

        dateTime = new SimpleDateFormat("yyyy/MM/dd").format(d);

        dateTime = dateTime.replaceAll("/", "");

        String temp = dataPoint[7];
        outTemp = Double.parseDouble(temp.replaceAll("\"", ""));

    }


    public WeatherDataPoint(String time, double temperature)
    {
        this.dateTime = time;
        this.outTemp = temperature;
    }


    public String getDateTime()
    {
        return dateTime;
    }


    public double getOutTemp()
    {
        return outTemp;
    }


    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }


    public void setOutTemp(double outTemp)
    {
        this.outTemp = outTemp;
    }

}


