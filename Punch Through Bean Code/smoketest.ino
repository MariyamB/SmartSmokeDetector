// This #include statement was automatically added by the Particle IDE.
#include "MQTT/MQTT.h"

/*******

Author: Mariyam

*******/


int redLed = D5;
int greenLed = D4;
int buzzer = D0;
int smokeA0 = A0;
// Your threshold value
int sensorThres = 400;
int i = 0;

void callback(char* topic, byte* payload, unsigned int length);
MQTT client("iot.eclipse.org", 1883, callback);

// recieve message
void callback(char* topic, byte* payload, unsigned int length) {
    char p[length + 1];
    memcpy(p, payload, length);
    p[length] = NULL;
    String message(p);

    if (message.equals("RED"))
        RGB.color(255, 0, 0);
    else if (message.equals("GREEN"))
        RGB.color(0, 255, 0);
    else if (message.equals("BLUE"))
        RGB.color(0, 0, 255);
    else
        RGB.color(255, 255, 255);
    delay(1000);
}

void setup() {
  pinMode(redLed, OUTPUT);
  pinMode(greenLed, OUTPUT);
  pinMode(buzzer, OUTPUT);
  pinMode(smokeA0, INPUT);
  Serial.begin(9600);

   RGB.control(true);

    // connect to the server
    client.connect("alligator_client");

    // publish/subscribe
    if (client.isConnected()) {
        //client.publish("led","hello world");
        client.subscribe("led");
    }
    client.publish("led","GREEN");

      //Particle.subscribe("hook-response/SmokySensor", myHandler, MY_DEVICES);


}

// Loop for sensing the CO level
void loop() {
        if (client.isConnected())
        client.loop();
  int analogSensor = analogRead(smokeA0);
  Serial.print("Pin A0: ");
  Serial.println(analogSensor);
  // Checks if it has reached the threshold value
  if (analogSensor > sensorThres)
  {
      i++;
      if(i == 1)
       client.publish("led","FIRE!!! CO Level is"+String(analogSensor));
    digitalWrite(redLed, HIGH);
    digitalWrite(greenLed, LOW);
    //delay(200);
    digitalWrite(buzzer, HIGH);
    //delay(200);
    //digitalWrite(buzzer, LOW);
  }
  else
  {
    digitalWrite(redLed, LOW);
    digitalWrite(greenLed, HIGH);
    digitalWrite(buzzer, LOW);
  }
 delay(100);
}
