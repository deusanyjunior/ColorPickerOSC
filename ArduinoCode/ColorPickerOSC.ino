// source: https://raw.githubusercontent.com/TrippyLighting/OSCuino_TouchOSC/master/OSCuino_TouchOSC.ino

//DHCP-based OSC server test code
//for use with IDE 1.0.5
//for use with W5100 or W5200 based ethernet shields

#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>
#include <OSCBundle.h>

#include <DmxSimple.h>

// you can find this written on the board of some Arduino Ethernets or shields
byte mac[] = { 
  0x84, 0x00, 0xD2, 0xCC, 0xEE, 0xC6 }; 

// NOTE: Alternatively, you can assign a fixed IP to configure your
//       Ethernet shield.
//       byte ip[] = { 192, 168, 0, 154 };


int serverPort  = 65535; //TouchOSC (incoming port)
int destPort = 9000;    //TouchOSC (outgoing port)
int ledPin =  13;       //pin 13 on Arduino Uno. Pin 6 on a Teensy++2
int ledState = LOW;

int vOne = 0;
int vTwo = 0;
int vThree = 0;
int vFour = 0;

//Create UDP message object
EthernetUDP Udp;

void setup(){
  Serial.begin(9600); //9600 for a "normal" Arduino board (Uno for example). 115200 for a Teensy ++2 
  Serial.println("OSC test");

  // start the Ethernet connection:
  // NOTE: Alternatively, you can assign a fixed IP to configure your
  //       Ethernet shield.
  //       Ethernet.begin(mac, ip);   
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // no point in carrying on, so do nothing forevermore:
    while(true);
  }
  // print your local IP address:
  Serial.print("Arduino IP address: ");
  for (byte thisByte = 0; thisByte < 4; thisByte++) {
    // print the value of each byte of the IP address:
    Serial.print(Ethernet.localIP()[thisByte], DEC);
    Serial.print("."); 
  }

  Udp.begin(serverPort);

  // Setup the DMX
  DmxSimple.usePin(7);
  DmxSimple.maxChannel(4);
}

void loop(){
  //process received messages
  OSCMsgReceive();
  //delay(2000);
} 

void OSCMsgReceive(){
  OSCMessage msgIN;
  int size;
  if((size = Udp.parsePacket())>0){
    while(size--)
      msgIN.fill(Udp.read());
    if(!msgIN.hasError()){
      msgIN.route("/colorpickerosc",color);
    }
  }
}

void color(OSCMessage &msg, int addrOffset){
  
//  Serial.print("color received: ");
  
  vOne = msg.getInt(0);
  vTwo = msg.getInt(1);
  vThree = msg.getInt(2);
  vFour = msg.getInt(3);

  DmxSimple.write(1,vOne);
  DmxSimple.write(2,vTwo);
  DmxSimple.write(3,vThree);
  DmxSimple.write(4,vFour);

//  Serial.print(vOne);
//  Serial.print(", ");
//  Serial.print(vTwo);
//  Serial.print(", ");
//  Serial.print(vThree);
//  Serial.print(", ");
//  Serial.print(vFour);
//  Serial.println("!");
}









