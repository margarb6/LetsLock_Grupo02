#include "WiFi.h"
#include "AsyncUDP.h"
#define BLANCO 0XFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x07E0
#define AZUL 0x001F
#include <M5Stack.h>
const char * ssid = "Team_2";
const char * password = "12345678";
char texto[20];
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
  if (udp.listen(3333)) {
    Serial.print("UDP Listening on IP: ");
    Serial.println(WiFi.localIP());
    udp.onPacket([](AsyncUDPPacket packet) {
      int i = 20;
      while (i--) {
        *(texto + i) = *(packet.data() + i);
      }
      rec = 1; //indica mensaje recibido
    });
  }
}
void loop()
{
  if (rec) {
    //Send broadcast
    rec = 0; //mensaje procesado
    udp.broadcastTo("Recibido", 3333); //envia confirmacion
    udp.broadcastTo(texto, 3333); //y dato recibido
    hora = atol(texto); //paso de texto a entero
    Serial.println (texto);
    Serial.println (hora);
    //mandar a M%Stack
    M5.Lcd.fillScreen(NEGRO); //borrar pantalla
    M5.Lcd.setCursor(0, 10); //posicion inicial del cursor
    M5.Lcd.setTextColor(BLANCO); //color del texto
    M5.Lcd.print(texto);
  }
}
