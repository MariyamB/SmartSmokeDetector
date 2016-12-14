# SmartSmokeDetector
Description:Smart Smoke detector which can sense and send mobile notification using particle photon

Power Point Presentation:https://drive.google.com/open?id=0B61JmW3QIOuQSnRkSHJVc0V2R28

Working Video: https://youtu.be/KR6w_hc3lz4

Code Explanation:<br />
MQTT client code installed on mobile device:<br />
MQTT client("iot.eclipse.org", 1883, callback);<br />


A threshold value is set after which the buzzer goes off and the notification is recieved on mobile device.<br />
int sensorThres = 400;<br />

We are using MQTT library in particle bean to connect to MQTT server.<br />
MQTT client("iot.eclipse.org", 1883, callback);<br />

The analog sensor value is read by the MQ2 sensor and compared with the threshold set.<br />When the sensor value is greater than the set threshold value,the client MQTT connection is checked.<br />If the client is connected then the buzzer is triggered and a message is sent to the MQTT topic 'LED'.<br />This inturn pushes a notification onto the mobile device.

void loop() <br />
{<br />
        if (client.isConnected())<br />
        client.loop();<br />
  int analogSensor = analogRead(smokeA0);<br />
  Serial.print("Pin A0: ");<br />
  Serial.println(analogSensor);<br />
  // Checks if it has reached the threshold value<br />
  if (analogSensor > sensorThres)<br />
  {<br />
      i++;<br />
      if(i == 1)<br />
       client.publish("led","FIRE!!! Voltage Level is"+String(analogSensor));<br />
    digitalWrite(redLed, HIGH);<br />
    digitalWrite(greenLed, LOW);<br />
    //delay(200);<br />
    digitalWrite(buzzer, HIGH);<br />
    //delay(200);<br />
    //digitalWrite(buzzer, LOW);<br />
  }<br />
  else<br />
  {<br />
    digitalWrite(redLed, LOW);<br />
    digitalWrite(greenLed, HIGH);<br />
    digitalWrite(buzzer, LOW);<br />
  }<br />
 delay(100);<br />
}<br />


