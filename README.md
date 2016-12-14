# SmartSmokeDetector
Description:Smart Smoke detector which can sense and send mobile notification using particle photon

Power Point Presentation:https://drive.google.com/open?id=0B61JmW3QIOuQR296aXdLUzJ2aEE

Working Video: https://youtu.be/KR6w_hc3lz4

Code Explanation:
MQTT client code installed on mobile device:
https://github.com/MariyamB/SmartSmokeDetector/tree/master/MQTT-Patterns-Basic-MQTT-v3.0


A threshold value is set after which the buzzer goes off and the notification is recieved on mobile device.
int sensorThres = 400;

We are using MQTT library in particle bean to connect to MQTT server.
MQTT client("iot.eclipse.org", 1883, callback);

The analog sensor value is read by the MQ2 sensor and compared with the threshold set.When the sensor value is greater than the set threshold value,the client MQTT connection is checked.If the client is connected then the buzzer is triggered and a message is sent to the MQTT topic 'LED'.This inturn pushes a notification onto the mobile device.

/*void loop() {
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
       client.publish("led","FIRE!!! Voltage Level is"+String(analogSensor));
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
}*/


