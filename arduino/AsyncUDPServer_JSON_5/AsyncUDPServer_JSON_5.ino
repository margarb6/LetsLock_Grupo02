#include <M5Stack.h>

#include "WiFi.h"
#include "AsyncUDP.h"
#include <ArduinoJson.h>

#define BLANCO 0XFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x07E0
#define AZUL 0x001F
const char * ssid = "Team_2"; //Red Wifi
const char * password = "12345678"; //Contraseña
char texto[200]; //array para recibir los datos como texto
int hora;
boolean rec = 0;
AsyncUDP udp;
void setup()
{
  M5.begin();
  M5.Lcd.setTextSize(2); //Tamaño del texto
  Serial.begin(115200);
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
    rec = 0;
    udp.broadcastTo("Recibido", 2222); //Confirmación
    udp.broadcastTo(texto, 2222); //reenvía lo recibido
    hora = atol(texto); //paso de texto a int
    StaticJsonDocument<200> jsonBufferRecv; //definición buffer para almacenar el objeto JSON, 200 máximo
    DeserializationError error = deserializeJson(jsonBufferRecv, texto); //paso de texto a formato JSON
    if (error)
      return;
    serializeJson(jsonBufferRecv, Serial); //envío por el puerto serie el objeto "jsonBufferRecv"
    Serial.println(); //nueva línea
    int segundo = jsonBufferRecv ["Segundo"]; //extraigo el dato "Segundo" del objeto " jsonBufferRecv " y lo
    //almaceno en la variable "segundo"
    Serial.println(segundo); //envío por el puerto serie la variable segundo

    M5.Lcd.fillScreen(NEGRO); //borrar pantalla
    M5.Lcd.setCursor(0, 10); //posicion inicial del cursor
    M5.Lcd.setTextColor(BLANCO); //color del texto
    M5.Lcd.print(segundo);
  }
}
