#include <M5Stack.h>

const int EchoPin = 26;
const int TriggerPin = 25;
const int sensor = 2;              // the pin that the sensor is atteched to
int state = LOW;             // by default, no motion detected
int val = 0;

void setup() {
  M5.begin();
  M5.Lcd.setTextSize(2); //Tamaño del texto
  Serial.begin(9600);
  pinMode(TriggerPin, OUTPUT);
  pinMode(EchoPin, INPUT);
  pinMode(sensor, INPUT);
}
/*void loop() {
  Serial.print("Distancia: ");
  Serial.println(distancia(TriggerPin, EchoPin));
  delay(1000);
  }*/
void loop() {
  M5.Lcd.fillScreen(BLACK);
  if (Serial.available() > 0) {

    char command = (char) Serial.read(); switch (command) {
      case 'H':
        Serial.println("Hola Mundo");
        M5.Lcd.print("Hola Mundo");
        break;

      case 'D':
        Serial.println(distancia(TriggerPin, EchoPin));
        M5.Lcd.println("----------Ult-----------");
        M5.Lcd.print("Distancia: ");
        M5.Lcd.println(distancia(TriggerPin, EchoPin));
        break;
        
      case 'P':
        int comprobar = 0;
        for (int i = 0; i < 300; i++) {
          val = digitalRead(sensor);   // read sensor value
          if (val == HIGH) {
            comprobar++;
          }
          delay(30);
        }
        
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
    }
  }
  //M5.update();
  //Serial.flush();
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
