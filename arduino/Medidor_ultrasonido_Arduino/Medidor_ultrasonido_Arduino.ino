#include <M5Stack.h>


const int EchoPin = 26;
const int TriggerPin = 25;

void setup() {
  M5.begin();
  M5.Lcd.setTextSize(2); //Tamaño del texto
  Serial.begin(9600);
  pinMode(TriggerPin, OUTPUT);
  pinMode(EchoPin, INPUT);
}
/*void loop() {
  Serial.print("Distancia: ");
  Serial.println(distancia(TriggerPin, EchoPin));
  delay(1000);
  }*/
void loop() {
  
  if (Serial.available() > 0) {
    
    char command = (char) Serial.read(); switch (command) {
      case 'H':
        Serial.println("Hola Mundo"); 
        M5.Lcd.print("Hola Mundo");
        break;
        
      case 'D':
        Serial.println(distancia(TriggerPin, EchoPin)); 
        M5.Lcd.print("Distancia: ");
        M5.Lcd.print(distancia(TriggerPin, EchoPin));
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
