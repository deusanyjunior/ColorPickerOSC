//for use with W5100 or W5200 based ethernet shields

#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>
#include <OSCBundle.h>
#include <DmxSimple.h>

// you can find this written on the board of some Arduino Ethernets or shields
// it is better to set a random value in the future 
byte mac[] = { 
  0x84, 0x00, 0xD2, 0xCC, 0xEE, 0xC6 }; 

// NOTE: Alternatively, you can assign a fixed IP to configure your
//       Ethernet shield.
//       byte ip[] = { 192, 168, 0, 100 };

int serverPort  = 65535;
EthernetUDP Udp;

void setup(){

  Serial.begin(9600);
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

  DmxSimple.write(1,msg.getInt(0));
  DmxSimple.write(2,msg.getInt(1));
  DmxSimple.write(3,msg.getInt(2));
  DmxSimple.write(4,msg.getInt(4));
}
