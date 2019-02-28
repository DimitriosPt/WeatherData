/*
Dimitrios Papageorgacopoulos
ptdimitrios@gmail.com
18 April 2018
Project A9
CS 17.11 Java

This Class creates a HomeDataPoint object which consists of a String which holds
the date and a pair of pairs.

The key will be date.
The first pair will be the highest and lowest temps for the key from gatherWeatherData.
The next pair will be data from gatherElectricityData() and gatherGasData().

The final form will look something like:

day = 100 degrees, 20 degrees, 4kWh, 2.3 Therms

*/

package edu.srjc.papageorgacopoulos.dimitrios.A9;

import javafx.util.Pair;

import javax.management.InvalidAttributeValueException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class HomeDataPoint
{
    private String day;
    private Double highestTemp;
    private Double lowestTemp;
    private Double therms;
    private Double kWh;
    private Pair<Double, Double> weatherDataPair;
    private Pair<Double, Double> gasAndElectrictyPair;
    private Pair<Pair<Double, Double>, Pair<Double,Double>> combinedData;

    @Override
    public String toString()
    {
        return "HomeDataPoint{" +
                "day='" + day + '\'' +
                ", highestTemp=" + highestTemp +
                ", lowestTemp=" + lowestTemp +
                ", therms=" + therms +
                ", kWh=" + kWh +
                '}';
    }

    public Double getHighestTemp()
    {
        return highestTemp;
    }

    public void setHighestTemp(Double highestTemp)
    {
        this.highestTemp = highestTemp;
    }

    public Double getLowestTemp()
    {
        return lowestTemp;
    }

    public void setLowestTemp(Double lowestTemp)
    {
        this.lowestTemp = lowestTemp;
    }

    public Double getTherms()
    {
        return therms;
    }

    public void setTherms(Double therms)
    {
        this.therms = therms;
    }

    public Double getkWh()
    {
        return kWh;
    }

    public void setkWh(Double kWh)
    {
        this.kWh = kWh;
    }

    public Pair<Double, Double> getWeatherDataPair()
    {
        return weatherDataPair;
    }

    public void setWeatherDataPair(Pair<Double, Double> weatherDataPair)
    {
        this.weatherDataPair = weatherDataPair;
    }

    public Pair<Double, Double> getGasAndElectrictyPair()
    {
        return gasAndElectrictyPair;
    }

    public void setGasAndElectrictyPair(Pair<Double, Double> gasAndElectrictyPair)
    {
        this.gasAndElectrictyPair = gasAndElectrictyPair;
    }

    public String getDay()
    {
        return day;
    }

    public void setDay(String day)
    {
        this.day = day;
    }

    public Pair<Pair<Double, Double>, Pair<Double, Double>> getCombinedData()
    {
        return combinedData;
    }

    public void setCombinedData(Pair<Pair<Double, Double>, Pair<Double, Double>> combinedData)
    {
        this.combinedData = combinedData;
    }


    private void validateTemps(Double temp) throws InvalidAttributeValueException
    {
        if(temp == null)
        {
            throw new InvalidAttributeValueException("Temperature cannot be null");
        }
    }

    public HomeDataPoint()
    {
        this.day = "";
        this.highestTemp = null;
        this.lowestTemp = null;
        this.therms = null;
        this.kWh = null;

        combinedData = null;
    }


    public HomeDataPoint(String day, Double highestTemp, Double lowestTemp, Double therms, Double kWh) throws InvalidAttributeValueException
    {
        this.day = day;
        this.highestTemp = highestTemp;
        this.lowestTemp=lowestTemp;

        validateTemps(highestTemp);
        validateTemps(lowestTemp);

        this.therms= therms;
        this.kWh=kWh;
    }

    public HomeDataPoint(String day, Pair<Pair<Double,Double>, Pair<Double,Double>> twoPair) throws InvalidAttributeValueException
    {
        this.day = day;
        this.combinedData = twoPair;
        this.highestTemp = twoPair.getKey().getKey();
        this.lowestTemp = twoPair.getKey().getValue();


        this.therms = twoPair.getValue().getKey();
        this.kWh = twoPair.getValue().getValue();
    }

    public HomeDataPoint(String day, Pair<Double, Double> weatherDataPair,
                         Pair<Double,Double> gasAndElectrictyPair) throws InvalidAttributeValueException
    {
        this.day = day;
        try
        {
            this.highestTemp = weatherDataPair.getKey();
            this.lowestTemp = weatherDataPair.getValue();
        }
        catch (Exception e)
        {
            throw new NullPointerException("WeatherData is Null");
        }


        this.therms = gasAndElectrictyPair.getKey();
        this.kWh = gasAndElectrictyPair.getValue();
    }


}
