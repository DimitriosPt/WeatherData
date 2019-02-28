/*
Dimitrios Papageorgacopoulos
ptdimitrios@gmail.com
18 April 2018
Project A9
CS 17.11 Java

This file is a class for an ElectricityDataObject
which will consist of a String which will represent date
and a double which will represent Kwh.
 */

package edu.srjc.papageorgacopoulos.dimitrios.A9;

public class ElectricityDataObject
{

    private String date;
    private double kWh;

    @Override
    public String toString()
    {
        return "ElectricityDataObject{" +
                "date='" + date + '\'' +
                ", kWh=" + kWh +
                '}';
    }

    public ElectricityDataObject(String line) throws Exception
    {
        if (line.length() == 0 || line.charAt(0) == '#')
        {
            throw new IllegalStateException("Empty line");
        }

        String[] dataPoint = line.split(",");


        if (dataPoint.length < 5)
        {
            throw new IllegalArgumentException("\n There are an invalid number of data points");
        }

        // 2016-01-28 ----> 20160128 ----> HomeDataObject
        this.date = dataPoint[1].replaceAll("-", "");
        this.kWh = Double.parseDouble(dataPoint[4]);
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        if (date.length()!= 10)
        {
            throw new IllegalStateException("\n The date here is invalid");
        }
        else
        {
            this.date = date;
        }
    }

    public double getkWh()
    {
        return kWh;
    }

    public void setkWh(double kWh)
    {
        this.kWh = kWh;
    }
}
