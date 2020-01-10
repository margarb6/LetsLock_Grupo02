/*
  ArduinoMqttClient - WiFi Simple Sender

  This example connects to a MQTT broker and publishes a message to
  a topic once a second.

  The circuit:
  - Arduino MKR 1000, MKR 1010 or Uno WiFi Rev.2 board

  This example code is in the public domain.
*/

#include <M5Stack.h>
#include <ArduinoMqttClient.h>
#include <WiFi.h> // for MKR1000 change to: #include <WiFi101.h>
#include <SPI.h>
#include <MFRC522.h>

#define RST_PIN 22
#define SS_PIN 21
MFRC522 mfrc522(SS_PIN, RST_PIN);
MFRC522::StatusCode status;

#define BLANCO 0xFFFF
#define NEGRO 0
#define AZUL 0x001F

///////please enter your sensitive data in the Secret tab/arduino_secrets.h
char ssid[] = "Team_2";        // your network SSID (name)
char pass[] = "12345678";    // your network password (use for WPA, or use as key for WEP)

// To connect with SSL/TLS:
// 1) Change WiFiClient to WiFiSSLClient.
// 2) Change port value from 1883 to 8883.
// 3) Change broker value to a server with a known SSL/TLS root certificate
//    flashed in the WiFi module.

WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

const char broker[] = "broker.hivemq.com";
int        port     = 1883;
const char topic[]  = "david/team_2/presencia";
const char topicDist[] = "david/team_2/distancia";
const char topicRFID[] = "david/team_2/rfid";

const long interval = 1000;
const long intervalDist = 60000;
unsigned long previousMillis = 0;
unsigned long previousMillisDist = 0;
unsigned long tiempoTranscurrido;

const int pinPir = 2;
const int EchoPin = 26;
const int TriggerPin = 25;
const int pin = 25;

byte ActualUID[7];

int count = 0;
bool entra = true;

void setup() {
  M5.begin();
  M5.Lcd.setTextSize(2);
  SPI.begin();
  mfrc522.PCD_Init();

  //Initialize serial and wait for port to open:
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  // attempt to connect to Wifi network:
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }

  pinMode(pinPir, INPUT);
  pinMode(TriggerPin, OUTPUT);
  pinMode(EchoPin, INPUT);
  pinMode(pin, OUTPUT);

  Serial.println("You're connected to the network");
  Serial.println();

  // You can provide a unique client ID, if not set the library uses Arduino-millis()
  // Each client must have a unique client ID
  // mqttClient.setId("clientId");

  // You can provide a username and password for authentication
  // mqttClient.setUsernamePassword("username", "password");

  Serial.print("Attempting to connect to the MQTT broker: ");
  Serial.println(broker);

  Serial.println("You're connected to the MQTT broker!");
  Serial.println();
}

void loop() {
  // call poll() regularly to allow the library to send MQTT keep alives which
  // avoids being disconnected by the broker
  mqttClient.poll();

  // avoid having delays in loop, we'll use the strategy from BlinkWithoutDelay
  // see: File -> Examples -> 02.Digital -> BlinkWithoutDelay for more info
  unsigned long currentMillis = millis();
  unsigned long currentMillisDist = millis();

  //Distancia --> Ultrasonidos
  if (currentMillisDist - previousMillisDist >= intervalDist) {
    if (mqttClient.connect(broker, port)) {

      int dist = distancia(TriggerPin, EchoPin);

      tiempoTranscurrido = millis();

      // send message, the Print interface can be used to set the message contents
      mqttClient.beginMessage(topicDist);
      mqttClient.print(dist);
      mqttClient.endMessage();

    }
    previousMillisDist = currentMillisDist;
  }

  if (currentMillis - previousMillis >= interval) {
    // save the last time a message was sent

    int value = digitalRead(pinPir);
    if (value == HIGH) {

      if (mqttClient.connect(broker, port)) {

        tiempoTranscurrido = millis();

        // send message, the Print interface can be used to set the message contents
        mqttClient.beginMessage(topic);
        mqttClient.print("presencia");
        mqttClient.endMessage();

        count++;
        entra = true;
      }
    }
    previousMillis = currentMillis;
  }

  if (tiempoTranscurrido + 10000 < currentMillis && count >= 1 && entra == true) {

    mqttClient.stop();
    entra = false;
  }

  // -----------------------
  // RFID
  // -----------------------

  if ( mfrc522.PICC_IsNewCardPresent())
  {
    //Seleccionamos una tarjeta
    if ( mfrc522.PICC_ReadCardSerial())
    {
      String uid = "";
      M5.Lcd.setCursor(0, 30);
      M5.Lcd.fillScreen(NEGRO);
      M5.Lcd.setTextColor(AZUL);
      M5.Lcd.print(F("USUARIO:"));
      for (byte i = 0; i < mfrc522.uid.size; i++) {
        Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
        M5.Lcd.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
        Serial.print(mfrc522.uid.uidByte[i], HEX);
        M5.Lcd.print(mfrc522.uid.uidByte[i], HEX);
        ActualUID[i] = mfrc522.uid.uidByte[i];
        //uid = uid + valor;
        if(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "){
          uid = uid + mfrc522.uid.uidByte[i];
        }
      }
      if (mqttClient.connect(broker, port)) {
        mqttClient.beginMessage(topicRFID);
        mqttClient.print(uid);
        mqttClient.endMessage();
      }

      Serial.print(" ");
      Serial.print("UID: ");
      Serial.println(uid);

    }
    // Terminamos la lectura de la tarjeta tarjeta actual
    mfrc522.PICC_HaltA();
    M5.Lcd.setCursor(30, 140);
    M5.Lcd.setTextColor(BLANCO);
    M5.Lcd.println("Pase otra etiqueta de Usuario");
  }

  //SWITCH ABRIR PUERTA y RFID

  if (Serial.available() > 0) {

    char command = (char) Serial.read(); switch (command) {

      case 'H':
        Serial.println("Hola Mundo");
        break;

      case 'A':
        digitalWrite(pin, LOW);// turn relay OFF
        Serial.println("Circuito abierto");
        M5.Lcd.println("A");
        delay(5000);
        digitalWrite(pin, HIGH);
        break;

    }
  }


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
