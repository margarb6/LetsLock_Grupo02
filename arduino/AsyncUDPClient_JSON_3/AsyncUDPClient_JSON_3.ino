#include "WiFi.h"
#include "AsyncUDP.h"
#include <TimeLib.h>
#include <ArduinoJson.h>
#include <SPI.h>
#include <MFRC522.h>
#include <M5Stack.h>
#include "time.h"

#define RST_PIN 22 //Pin 9 para el reset del RC522 no es necesario conctarlo
#define SS_PIN 21 //Pin 10 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN); ///Creamos el objeto para el RC522
MFRC522::StatusCode status; //variable to get card status
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
  SPI.begin(); //Iniciamos el Bus SPI
  mfrc522.PCD_Init(); // Iniciamos el MFRC522
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
}

byte ActualUID[7]; //almacenará el código del Tag leído
byte Usuario1[7] = {0x04, 0x19, 0xC1, 0x5A, 0x51, 0x59, 0x80} ;
byte Usuario2[7] = {0x04, 0x21, 0xC1, 0x5A, 0x51, 0x59, 0x80} ;
byte Usuario3[7] = {0x04, 0x2A, 0xC1, 0x5A, 0x51, 0x59, 0x80} ;
byte Usuario4[7] = {0x04, 0x33, 0xC1, 0x5A, 0x51, 0x59, 0x80} ;

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

  //Sensor RFID
  if ( mfrc522.PICC_IsNewCardPresent())
  {
    //Seleccionamos una tarjeta
    if ( mfrc522.PICC_ReadCardSerial())
    {
      // Enviamos serialemente su UID
      Serial.println();
      Serial.print(F("USUARIO:"));
      for (byte i = 0; i < mfrc522.uid.size; i++) {
        Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
        Serial.print(mfrc522.uid.uidByte[i], HEX);
        ActualUID[i] = mfrc522.uid.uidByte[i];
      }
      Serial.print(" ");
    }
    mfrc522.PICC_HaltA();

  }


  //delay(1000);
  int dist = distancia(TriggerPin, EchoPin);
  jsonBuffer["Distancia"] = dist;
  jsonBuffer["Comprobar"] = comprobar;
  jsonBuffer["ActualUID"] = ActualUID;
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
