#include <M5Stack.h>

#include "WiFi.h"
#include "AsyncUDP.h"
#include <TimeLib.h>

#include <ArduinoJson.h>
#include "time.h"


#define BLANCO 0XFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x07E0
#define AZUL 0x001F
const char * ssid = "Team_2"; //Red Wifi
const char * password = "12345678"; //Contraseña
char texto[200];
boolean rec = 0;
AsyncUDP udp;
void setup()
{
  M5.begin();
  M5.Lcd.setTextSize(2); //Tamaño del texto
  Serial.begin(9600);
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
  if (rec) {

    if (Serial.available() > 0) {

      rec = 0;
      //udp.broadcastTo("Recibido", 2222); //Confirmación
      //udp.broadcastTo(texto, 2222); //reenvía lo recibido
      StaticJsonDocument<200> jsonBufferRecv; //definición buffer para almacenar el objeto JSON, 200 máximo
      DeserializationError error = deserializeJson(jsonBufferRecv, texto); //paso de texto a formato JSON
      if (error)
        return;
      serializeJson(jsonBufferRecv, Serial); //envío por el puerto serie el objeto "jsonBufferRecv"
      Serial.println(); //nueva línea

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
            Serial.print("La habitacion esta ocupada");
            M5.Lcd.println("-----------PIR-----------");
            M5.Lcd.println("La habitacion esta ocupada");
          } else {
            Serial.print("No hay gente en la habitacion");
            M5.Lcd.println("-----------PIR-----------");
            M5.Lcd.println("No hay gente en la habitacion");
          }
          break;

          // -----------------------
          // Tiempo
          // -----------------------

      }

      M5.Lcd.fillScreen(NEGRO); //borrar pantalla
      /*
        M5.Lcd.setCursor(0, 10); //posicion inicial del cursor
        M5.Lcd.setTextColor(BLANCO); //color del texto
        M5.Lcd.print("Distancia ultr: ");
        M5.Lcd.println(distancia);
        M5.Lcd.print("Comprobacion pir: ");
        M5.Lcd.println(comprobar);
      */
    }
  }
}
