#include <Arduino.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <Wire.h>
#include "time.h"

// Provide the token generation process info.
#include "addons/TokenHelper.h"
// Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Insert your network credentials
//DEPENDE DONDE PORAI EL CODIGO NO FUNCA
#define WIFI_SSID "ORT-IoT"
#define WIFI_PASSWORD "OrtIOTnew22$2"

// Insert Firebase project API Key
#define API_KEY "AIzaSyDsOuqyP9EvGvc25XL5H_guO2sKXDQ3_4Y"

// Insert Authorized Email and Corresponding Password
#define USER_EMAIL "barbas100@gmail.com"
#define USER_PASSWORD "barbas100"

// Insert RTDB URLefine the RTDB URL
#define DATABASE_URL "https://cognifyapp-default-rtdb.firebaseio.com/"

// Define Firebase objects
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// Variable to save USER UID
String uid;

// Database main path (to be updated in setup with the user UID)
String databasePath;
// Database child nodes
String pressPath = "/presiones";
String WpressPath = "/presiones erróneas";
String RpressPath = "/presiones correctas";
String promPath = "/presiones correctas por segundo";
String promPressPath = "/presiones erróneas entre cada presion correcta";
String timePath = "/tiempo";
String botonPath = "/usuarios/flagInicio";
// Parent Node (to be updated in every loop)
String parentPath;

int timestamp;
FirebaseJson json;

const char* ntpServer = "pool.ntp.org";
// Timer variables (send new readings every three minutes)
unsigned long sendDataPrevMillis = 0;
int timerDelay;

// Initialize WiFi
void initWiFi() {
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to WiFi ..");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print('.');
    delay(1000);
  }
  Serial.println(WiFi.localIP());
  Serial.println();
}

// Function that gets current epoch time
unsigned long getTime() {
  time_t now;
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    //Serial.println("Failed to obtain time");
    return (0);
  }
  time(&now);
  return now;
}

//TECLADO MATRICIAL
byte pinesFilas[] = {26 , 25, 33, 32, 4};
byte pinesColumnas[] = {12, 14, 27};
const byte numeroFilas = 5;
const byte numeroColumnas = 3;
char teclas[3][5] = {
  {'1', '2', '3', 'A', 'E'},
  {'4', '5', '6', 'B', 'E'},
  {'7', '8', '9', 'C', 'G'}
};
int botonPresionado;
float presiones, presionesBien, presionesMal;
float promedioTotal, promedioBien;

//NEOPIXEL Y RANDOM
long randNumber;

#include <Adafruit_NeoPixel.h>

#define PIN_NEO_PIXEL  13  // Arduino pin that connects to NeoPixel
#define NUM_PIXELS     15  // The number of LEDs (pixels) on NeoPixel

Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUM_PIXELS, PIN_NEO_PIXEL, NEO_GRB + NEO_KHZ800);

/*
  struct Color {
  char nombre[20];  // Nombre del color
  byte rojo;        // Componente rojo (0-255)
  byte verde;       // Componente verde (0-255)
  byte azul;        // Componente azul (0-255)
  };

  Color listaDeColores[] = {
  {"Rojo", 255, 0, 0},
  {"Verde", 0, 255, 0},
  {"Azul", 0, 0, 255},
  {"Amarillo", 255, 255, 0},
  // Puedes agregar más colores aquí
  };
*/

//colores
uint32_t rojo = pixels.Color(255, 0, 0);
uint32_t verde = pixels.Color(0, 255, 0);
uint32_t azul = pixels.Color(0, 0, 150);
uint32_t amarillo = pixels.Color(255, 255, 0);


uint32_t violeta = pixels.Color(255, 0, 255);
uint32_t celeste = pixels.Color(0, 255, 255);
uint32_t blanco = pixels.Color(255, 255, 255);
uint32_t naranja = pixels.Color(255, 125, 0);
uint32_t rosa = pixels.Color(255, 0, 255);
uint32_t marron = pixels.Color(165, 100, 0);
uint32_t gris = pixels.Color(165, 165, 165);
uint32_t salmon = pixels.Color(255, 125, 125);
uint32_t menta = pixels.Color(0, 200, 200);
uint32_t indigo = pixels.Color(90, 90, 210);
uint32_t tin = pixels.Color(145, 145, 145);
uint32_t apagado = pixels.Color(0, 0, 0);

//int numColores = sizeof(listaDeColores) / sizeof(listaDeColores[0]);

String nombreUsuario;
int intValue, estados, estadoFirebase;
float tiempoEjercicio;
bool estadoBoton;

void setup() {
  pixels.begin();
  pixels.setBrightness (50);
  for (int nF = 0; nF < numeroFilas; nF++) {
    pinMode(pinesFilas[nF], OUTPUT);
    digitalWrite(pinesFilas[nF], HIGH);
  }
  for (int nC = 0; nC < numeroColumnas; nC++) {
    pinMode(pinesColumnas[nC], INPUT_PULLUP);
  }

  Serial.begin(115200);
  Serial.println("Teclado 4X4");
  Serial.println();
  // Initialize BME280 sensor
  initWiFi();
  configTime(0, 0, ntpServer);

  // Assign the api key (required)
  config.api_key = API_KEY;

  // Assign the user sign in credentials
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  // Assign the RTDB URL (required)
  config.database_url = DATABASE_URL;

  Firebase.reconnectWiFi(true);
  fbdo.setResponseSize(4096);

  // Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h

  // Assign the maximum retry of token generation
  config.max_token_generation_retry = 5;

  // Initialize the library with the Firebase authen and config
  Firebase.begin(&config, &auth);

  // Getting the user UID might take a few seconds
  Serial.println("Getting User UID");
  while ((auth.token.uid) == "") {
    Serial.print('.');
    delay(1000);
  }
  // Print user UID
  uid = auth.token.uid.c_str();
  Serial.print("User UID: ");
  Serial.println(uid);

  // Update database path
  databasePath = "/UsersData/" + uid + "/datos";
}


void leerBoton() {
  for (int nF = 0; nF < numeroFilas; nF++) {
    digitalWrite(pinesFilas[nF], LOW);
    for (int nC = 0; nC < numeroColumnas; nC++) {
      if (digitalRead(pinesColumnas[nC]) == LOW) {
        Serial.print("Tecla: ");
        //Serial.println(teclas[nF][nC]);
        botonPresionado = nF * 3 + nC;
        Serial.println (botonPresionado);
        presiones = presiones + 1;
        while (digitalRead(pinesColumnas[nC]) == LOW) {}
      }
    }
    digitalWrite(pinesFilas[nF], HIGH);
  }
  delay(10);
}

void funcionamiento() {
  switch (estados) {
    case 0:
      {
        randNumber = random(NUM_PIXELS);

        Serial.print("Random: ");
        Serial.println(randNumber);
        estados = 1;
      }
      break;
    case 1:
      {
        leerBoton();
        //int indiceAleatorio = random(0, numColores);
        //Color colorAleatorio = listaDeColores[indiceAleatorio];
        pixels.setPixelColor(randNumber, rojo);
        pixels.show();
        leerBoton();
        if (botonPresionado == randNumber) {
          pixels.setPixelColor(randNumber, apagado);
          pixels.show();
          estados = 0;
          presionesBien = presionesBien + 1;
          //   Serial.print("Presiones correctas: ");
          // Serial.println (presionesBien);
        }
      }
      break;
  }
}

void loop() {
  switch (estadoFirebase) {
    case 0:
      //set todas las variables a 0 p el siguiente ejercicio
      pixels.setPixelColor(randNumber, apagado);
      presiones = 0;
      presionesMal = 0;
      presionesBien = 0;
      promedioBien = 0;
      promedioTotal = 0;
      { //lee el usuario
        if (Firebase.RTDB.getString(&fbdo, "/usuarios/paciente")) {
          if (fbdo.dataType() == "string") {
            nombreUsuario = fbdo.stringData();
            Serial.println(nombreUsuario);
          }
        }
        else {
          Serial.println(fbdo.errorReason());
        }
        //lee el tiempo de duracion
        if (Firebase.RTDB.getInt(&fbdo, "/usuarios/tiempoEntrenamiento")) {
          if (fbdo.dataType() == "int") {
            intValue = fbdo.intData();
            timerDelay = intValue * 1000;
            Serial.println(timerDelay);
          }
        }
        else {
          Serial.println(fbdo.errorReason());
        }

        //lee el boton de comienzo desde firebase
        if (Firebase.RTDB.getBool(&fbdo, "/usuarios/flagInicio")) {
          if (fbdo.dataType() == "boolean") {
            estadoBoton = fbdo.boolData();
            Serial.println(estadoBoton);
          }
        }
        else {
          Serial.println(fbdo.errorReason());
        }
      }
      if (estadoBoton == true) {
        estadoFirebase = 1;
        sendDataPrevMillis = millis();
      }
      break;
    case 1:
      {
        funcionamiento();
        // Send new readings to database
        if (Firebase.ready() && millis() - sendDataPrevMillis > timerDelay) {
          sendDataPrevMillis = millis();
          presionesMal = presiones - presionesBien;
          promedioBien = presionesBien / (timerDelay / 1000);
          promedioTotal = presionesMal / presionesBien;

          //Get current timestamp
          timestamp = getTime();
          Serial.print ("time: ");
          Serial.println (timestamp);

          parentPath = databasePath + "/" + String(nombreUsuario) + "/" + String(timestamp);

          //parentPath = databasePath + "/lectura";

          /*json.set(tempPath.c_str(), String(bme.readTemperature()));*/
          json.set(pressPath.c_str(), String(presiones));
          json.set(WpressPath.c_str(), String(presionesMal));
          json.set(RpressPath.c_str(), String(presionesBien));
          json.set(promPath.c_str(), String(promedioBien));
          json.set(promPressPath.c_str(), String(promedioTotal));
          json.set(timePath, String(timestamp));
          Serial.printf("Set json... %s\n", Firebase.RTDB.setJSON(&fbdo, parentPath.c_str(), &json) ? "ok" : fbdo.errorReason().c_str());
          estadoFirebase = 0;
          Firebase.RTDB.setBool(&fbdo, "/usuarios/flagInicio", false);

        }
      }
  }
}
