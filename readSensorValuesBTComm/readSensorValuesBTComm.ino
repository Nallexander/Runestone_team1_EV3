#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <ctype.h>

#include <Adafruit_SI1145.h>
#include <ArduinoJson.h>
#include <SoftwareSerial.h>

#define DHTPIN  7 //Defines which pin the temperature sensor is on

#define DHTTYPE DHT22 //Defines which type of temperature sensor it is.

DHT_Unified dht(DHTPIN, DHTTYPE);

SoftwareSerial BTserial(10, 11);
StaticJsonBuffer<200> jsonBuffer;
JsonObject& root = jsonBuffer.createObject();
Adafruit_SI1145 uv = Adafruit_SI1145();

//Start up all serial interfaces
void setup() {
  Serial.begin(9600);
  BTserial.begin(9600);
  
  BTserial.write("AT+NAMEteam1ARDUINO"); //Set name for bluetooth module
  
  dht.begin();
  sensor_t sensor;
  dht.temperature().getSensor(&sensor);
  
  if (! uv.begin()) {
    Serial.println("Didn't find Si1145");
    while (1);
  }
  Serial.setTimeout(50);
}

/*
 * Reads the light from the light sensor and returns the value.
 */
int readLight() {
    int UVvalue = uv.readVisible();
    int IRvalue = uv.readIR();
    return UVvalue;
}

/*
 * Reads the temperature from the temperature sensor and returns the value.
 */
int readTemp() {
  sensors_event_t event;  
  dht.temperature().getEvent(&event);
  int temp = event.temperature;
  return temp;  
}

void loop()
{
 //Format the values to a JSON string and send them on Serial and bluetooth serial
 root["Light"] = readLight();
 root["Temperature"] = readTemp(); 
 root.printTo(BTserial);
 //root.printTo(Serial);
  
 delay(1000); //Read values once every second
}


