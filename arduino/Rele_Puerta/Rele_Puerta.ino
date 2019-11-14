#include <M5Stack.h>
#define TFT_GREY 0x5AEB // New colour

const int pin = 21;

void setup() {
  Serial.begin(115200);
  M5.begin();
  M5.Lcd.setTextSize(2);
  pinMode(pin, OUTPUT);// connected to S terminal of Relay
  digitalWrite(pin, LOW);
}

void loop() {

  M5.Lcd.clear();
  M5.Lcd.setCursor(0, 0);

  if (Serial.available() > 0) {

    char command = (char) Serial.read();
    switch (command) {
      case 'A':
        digitalWrite(pin, LOW);// turn relay OFF
        Serial.println("Circuito abierto");
        M5.Lcd.println("A");
        delay(5000);
        digitalWrite(pin, HIGH);
        break;

      case 'C':
        digitalWrite(pin, HIGH);
        Serial.println("Circuito cerrado, pasa corriente");
        M5.Lcd.println("C");
        delay(5000);
        break;
    }
  }

}
