/*  
 *  --[Ev_v30_01] - Reading the Temperature, humidity and pressure
 *  
 *  Explanation: Turn on the Events v30 board and read the 
 *  temperature sensor every second
 *  
 *  Copyright (C) 2018 Libelium Comunicaciones Distribuidas S.L. 
 *  http://www.libelium.com 
 *  
 *  This program is free software: you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by 
 *  the Free Software Foundation, either version 3 of the License, or 
 *  (at your option) any later version. 
 *  
 *  This program is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU General Public License for more details. 
 *  
 *  You should have received a copy of the GNU General Public License 
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *  
 *  Version:           3.2
 *  Design:            David Gascón 
 *  Implementation:    Carlos Bello
 */
 
#include <WaspSensorEvent_v30.h>

float temp;
float humd;
float pres;
float value;

void setup() 
{
  // Turn on the USB and print a start message
  USB.ON();
  USB.println(F("Start program"));
  
}



void loop() 
{
  
  ///////////////////////////////////////
  // 1. Read BME280 Values
  ///////////////////////////////////////
  // Turn on the sensor board
  Events.ON();
  //Temperature
  temp = Events.getTemperature();
  //Humidity
  humd = Events.getHumidity();
  //Pressure
  pres = Events.getPressure();

  
  ///////////////////////////////////////
  // 2. Print BME280 Values
  ///////////////////////////////////////
  USB.print("{");
  USB.print("\"temperature\":\"");
  USB.printFloat(temp, 2);
  USB.print(F("\","));
  USB.print("\"humidity\":\"");
  USB.printFloat(humd, 1); 
  USB.print(F("\",")); 
  USB.print("\"pressure\":\"");
  USB.printFloat(pres, 2); 
  USB.print(F("\"")); 
  USB.println("}");  

  
  ///////////////////////////////////////
  // 3. Go to deep sleep mode
  ///////////////////////////////////////
  PWR.deepSleep("00:00:00:10", RTC_OFFSET, RTC_ALM1_MODE1, ALL_OFF);
  USB.ON();
}



