#include <M5Stack.h>

#include "WiFi.h"
#include "AsyncUDP.h"
#include <TimeLib.h>

#include <ArduinoJson.h>
#include "time.h"

#include <SPI.h>
#include <MFRC522.h>

#define RST_PIN 22 //Pin 9 para el reset del RC522 no es necesario conctarlo
#define SS_PIN 21 //Pin 10 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN); ///Creamos el objeto para el RC522
MFRC522::StatusCode status; //variable to get card status


#define BLANCO 0XFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x07E0
#define AZUL 0x001F
const char * ssid = "Team_2"; //Red Wifi
const char * password = "12345678"; //Contraseña
char texto[200];
boolean rec = 0;

byte ActualUID[7]; //almacenará el código del Tag leído
byte Usuario1[7] = {0x04, 0x19, 0xC1, 0x5A, 0x51, 0x59, 0x80} ;
byte Usuario2[7] = {0x04, 0x21, 0xC1, 0x5A, 0x51, 0x59, 0x80} ;
byte Usuario3[7] = {0x04, 0x2A, 0xC1, 0x5A, 0x51, 0x59, 0x80} ;
byte Usuario4[7] = {0x04, 0x33, 0xC1, 0x5A, 0x51, 0x59, 0x80} ;

const int pin = 25;


AsyncUDP udp;
void setup()
{
  M5.begin();
  M5.Lcd.setTextSize(2); //Tamaño del texto
  Serial.begin(9600);
  SPI.begin(); //Iniciamos el Bus SPI
  mfrc522.PCD_Init(); // Iniciamos el MFRC522
  
  pinMode(pin, OUTPUT);// connected to S terminal of Relay
  //digitalWrite(pin, HIGH);
  
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
      int i = 200;
      while (i--) {
        *(texto + i) = *(packet.data() + i);
      }
      rec = 1; //recepcion de un mensaje
    });
  }
}

void loop()
{
  //if (rec) {

    if (Serial.available() > 0) {

      rec = 0;
      //udp.broadcastTo("Recibido", 2222); //Confirmación
      //udp.broadcastTo(texto, 2222); //reenvía lo recibido
      StaticJsonDocument<200> jsonBufferRecv; //definición buffer para almacenar el objeto JSON, 200 máximo
      DeserializationError error = deserializeJson(jsonBufferRecv, texto); //paso de texto a formato JSON
      if (error)
        return;
      //serializeJson(jsonBufferRecv, Serial); //envío por el puerto serie el objeto "jsonBufferRecv"
      //Serial.println(); //nueva línea

      int distancia = jsonBufferRecv ["Distancia"];
      int comprobar = jsonBufferRecv ["Comprobar"];

      // -----------------------
      // ULTRASONICO
      // -----------------------
      char command = (char) Serial.read(); switch (command) {
        case 'D':
          Serial.println(distancia);
          M5.Lcd.println("----------Ult-----------");
          M5.Lcd.print("Distancia: ");
          M5.Lcd.println(distancia);
          break;
        // -----------------------
        // PIR
        // -----------------------
        case 'P':

          if (comprobar > 0) {
            Serial.println("La habitacion esta ocupada");
            M5.Lcd.println("-----------PIR-----------");
            M5.Lcd.println("La habitacion esta ocupada");
          } else {
            Serial.println("No hay gente en la habitacion");
            M5.Lcd.println("-----------PIR-----------");
            M5.Lcd.println("No hay gente en la habitacion");
          }
          break;

        // -----------------------
        // RFID
        // -----------------------
        case 'G':

          if ( mfrc522.PICC_IsNewCardPresent())
          {
            //Serial.println("Pasa primer IF");
            //Seleccionamos una tarjeta
            if ( mfrc522.PICC_ReadCardSerial())
            {
              //Serial.println("Pasa segundo IF");
              // Enviamos serialemente su UID
              //Serial.println();
              //Serial.print(F("USUARIO:"));
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
              }
              Serial.print(" ");/*
              M5.Lcd.print(" ");


              //comparamos los UID para determinar si es uno de nuestros usuarios
              if (compareArray(ActualUID, Usuario1, 7))
              { Serial.println("Usuario 1");
                //M5.Lcd.setCursor(0, 60);
                //M5.Lcd.setTextColor(VERDE);
                //M5.Lcd.println("Usuario 1");
                //lectura_datos();
                //si();
              }
              else if (compareArray(ActualUID, Usuario2, 7))
              { Serial.println("Usuario 2");
                M5.Lcd.setCursor(0, 60);
                M5.Lcd.setTextColor(VERDE);
                M5.Lcd.println("Usuario 2");
                //lectura_datos();
                //si();
              }
              else if (compareArray(ActualUID, Usuario3, 7))
              { Serial.println("Usuario 3");
                M5.Lcd.setCursor(0, 60);
                M5.Lcd.setTextColor(VERDE);
                M5.Lcd.println("Usuario 3");
                //lectura_datos();
                //si();
              }
              else if (compareArray(ActualUID, Usuario4, 7))
              { Serial.println("Usuario 4");
                M5.Lcd.setCursor(0, 60);
                M5.Lcd.setTextColor(VERDE);
                M5.Lcd.println("Usuario 4");
                //lectura_datos();
                //si();
              }
              else
              { Serial.println("?????");
                M5.Lcd.setCursor(0, 60);
                M5.Lcd.setTextColor(ROJO);
                M5.Lcd.println("?????");
                no();
              }*/
            }
            // Terminamos la lectura de la tarjeta tarjeta actual
            mfrc522.PICC_HaltA();
            M5.Lcd.setCursor(30, 140);
            M5.Lcd.setTextColor(BLANCO);
            M5.Lcd.println("Pase otra etiqueta de Usuario");
          }
          break;

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

      //M5.Lcd.fillScreen(NEGRO); //borrar pantalla
      /*
        M5.Lcd.setCursor(0, 10); //posicion inicial del cursor
        M5.Lcd.setTextColor(BLANCO); //color del texto
        M5.Lcd.print("Distancia ultr: ");
        M5.Lcd.println(distancia);
        M5.Lcd.print("Comprobacion pir: ");
        M5.Lcd.println(comprobar);
      */
    }
  //}
}

//Función para comparar dos vectores
boolean compareArray(byte array1[], byte array2[], int n_byte)
{
  for (int i = 0; i < n_byte; i++)
  {
    if (array1[i] != array2[i])return (false);
  }
  return (true);
}
void si ()
{ M5.Lcd.setTextSize(4);
  M5.Lcd.setCursor(150, 90);
  M5.Lcd.setTextColor(VERDE);
  M5.Lcd.println("SI");
  M5.Lcd.setTextSize(2);
}
void no ()
{ M5.Lcd.setTextSize(4);
  M5.Lcd.setCursor(150, 90);
  M5.Lcd.setTextColor(ROJO);
  M5.Lcd.println("NO");
  M5.Lcd.setTextSize(2);
}
void lectura_datos()
{
  byte buffer_1[18]; //buffer intermedio para leer 16 bytes
  byte buffer[66]; //data transfer buffer (64+2 bytes data+CRC)
  byte tam = sizeof(buffer);
  byte tam1 = sizeof(buffer_1);
  uint8_t pageAddr = 0x06; //In this example we will write/read 64 bytes (page 6,7,8 hasta la 21).
  //Ultraligth mem = 16 pages. 4 bytes per page.
  //Pages 0 to 4 are for special functions.
  // Read data ***************************************************
  //En esta función los datos se leen de 16 bytes en 16 y se almacenan en buffer_1 (de 16+2 bytes)
  //para despues transferirlos a buffer que tiene un tamaño mayor
  //Serial.println(F("Reading data ... "));
  for (int i = 0; i < (tam - 2) / 16; i++)
  {
    //data in 4 block is readed at once 4 bloques de 4 bytes total 16 bytes en cada lectura.
    status = (MFRC522::StatusCode) mfrc522.MIFARE_Read(pageAddr + i * 4, buffer_1, &tam1);
    // if (status != MFRC522::STATUS_OK) {
    // Serial.print(F("MIFARE_Read() failed: "));
    // Serial.println(mfrc522.GetStatusCodeName(status));
    // return;
    // }
    //copio los datos leidos en buffer_1 a la posición correspondiente del buffer
    for (int j = 0; j < 16; j++)
    {
      buffer[j + i * 16] = buffer_1[j];
    }
  }
  //Presentacion de los datos ledidos por el puerto serie y por el M5Stack
  Serial.print(F("Readed data: "));
  //Dump a byte array to Serial
  for (byte i = 0; i < (tam - 2); i++) {
    Serial.write(buffer[i]);
  }
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(0, 160);
  M5.Lcd.setTextColor(VERDE);
  for (byte i = 0; i < (tam - 2); i++) {
    M5.Lcd.print((char)buffer[i]);
  }
}
