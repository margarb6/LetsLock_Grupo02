#include "WiFi.h"
#include "AsyncUDP.h"
#include <TimeLib.h>
#include <ArduinoJson.h>
#include "time.h"


const int EchoPin = 26;
const int TriggerPin = 25;
const int sensor = 2;              // the pin that the sensor is atteched to
int state = LOW;             // by default, no motion detected
int val = 0;
StaticJsonDocument<200> jsonBuffer;
const char * ssid = "Team_2";
const char * password = "12345678"; //contraseña
AsyncUDP udp;
void setup()
{
  Serial.begin(9600);
  pinMode(TriggerPin, OUTPUT);
  pinMode(EchoPin, INPUT);
  pinMode(sensor, INPUT);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    Serial.println("WiFi Failed");
    while (1) {
      delay(1000);
    }
  }
  if (udp.listen(2222)) {
    Serial.print("UDP Listening on IP: ");
    Serial.println(WiFi.localIP());
    udp.onPacket([](AsyncUDPPacket packet) {
      Serial.write(packet.data(), packet.length());
      Serial.println();
    });
  }
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
}
void loop()
{ //enviar datos
  // SENSOR ULTRASÓNICO
  delay(1000);
  char texto[200];
  
  // SENSOR PIR
  int comprobar = 0;
  for (int i = 0; i < 300; i++) {
    val = digitalRead(sensor);   // read sensor value
    if (val == HIGH) {
      comprobar++;
    }
    //delay(10);
  }
  
  
  //delay(1000);
  int dist = distancia(TriggerPin, EchoPin);
  jsonBuffer["Distancia"] = dist;
  jsonBuffer["Comprobar"] = comprobar;
  serializeJson(jsonBuffer, texto); //paso del objeto “jsonbuffer" a texto para
  udp.broadcastTo(texto, 2222);
  /*delay(1000);
    char texto[200];
    jsonBuffer["Hora"] = hour(); //Datos introducidos en el objeto “jsonbuffer"
    jsonBuffer["Minuto"] = minute(); //3 campos
    jsonBuffer["Segundo"] = second(); //
    serializeJson(jsonBuffer, texto); //paso del objeto “jsonbuffer" a texto para
    //transmitirlo
    udp.broadcastTo(texto, 2222); //se envía por el puerto 1234 el JSON
    //como texto
  */
}


int distancia(int TriggerPin, int EchoPin) {
  long duracion, distanciaCm;
  digitalWrite(TriggerPin, LOW); //nos aseguramos señal baja al principio
  delayMicroseconds(4);
  digitalWrite(TriggerPin, HIGH); //generamos pulso de 10us
  delayMicroseconds(10);
  digitalWrite(TriggerPin, LOW);
  duracion = pulseIn(EchoPin, HIGH); //medimos el tiempo del pulso
  distanciaCm = duracion * 10 / 292 / 2; //convertimos a distancia
  return distanciaCm;
}
