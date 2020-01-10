#include <SPI.h>

const int pin=25;

void setup() {
  Serial.begin(9600);
  SPI.begin();
  pinMode(pin, OUTPUT);

  digitalWrite(pin, HIGH);

}

void loop() {
 if (Serial.available() > 0) {

    char command = (char) Serial.read(); switch (command) {

      case 'H':
        Serial.println("Hola Mundo");
        break;

       case 'A':
        digitalWrite(pin, LOW);// turn relay OFF
        Serial.println("Puerta abierta");
        //delay(5000);
        //digitalWrite(pin, HIGH);
        break;

      case 'C':
        digitalWrite(pin, HIGH);// turn relay OFF
        Serial.println("Puerta cerrada");
        //delay(5000);
        //digitalWrite(pin, HIGH);
        break;

    }
  }

}
