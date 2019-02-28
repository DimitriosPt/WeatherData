/*
Dimitrios Papageorgacopoulos
ptdimitrios@gmail.com
18 April 2018
Project A9
CS 17.11 Java

This file is a class for an GasDataObject
which will consist of a String which will represent date
and a double which will represent Therms.
 */

package edu.srjc.papageorgacopoulos.dimitrios.A9;

public class GasDataObject
{

    private String date;
    private double therms;



    public GasDataObject(String line) throws Exception
    {
        if (line.length() == 0)
        {
            throw new IllegalStateException("Empty line");
        }

        String[] dataPoint = line.split(",");


        if (dataPoint.length < 5)
        {
            throw new IllegalArgumentException("\n There are an invalid number of data points");
        }

        this.date = dataPoint[1].replaceAll("-", "");
        this.therms = Double.parseDouble(dataPoint[2]);
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

    public double getTherms()
    {
        return therms;
    }

    public void setTherms(double therms)
    {
        if(therms < 0)
        {
            throw new IllegalStateException("\n Therms cannot be less than 0");
        }
        else
        {
            this.therms = therms;
        }
    }


}
