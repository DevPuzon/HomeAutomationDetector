
#include <SoftwareSerial.h>

int light = 12;
int tv = 11;
int fan = 10;
int waterd = 9;
int aircon = 8;

SoftwareSerial bth(3, 4);
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  bth.begin(9600);
  pinMode(light,OUTPUT);
  pinMode(tv,OUTPUT);
  pinMode(fan,OUTPUT);
  pinMode(aircon,OUTPUT);
  pinMode(waterd,OUTPUT);
  digitalWrite(light,LOW);
  digitalWrite(tv,LOW);
  digitalWrite(fan,LOW);
  digitalWrite(aircon,LOW);
  digitalWrite(waterd,LOW); 
}

String save[5] = {"false","false","false","false","false"};
void loop() {
  // put your main code here, to run repeatedly:
    String command = bth.readString(); 
    Serial.println(command);
  if(command.indexOf("light on") != -1){ 
   digitalWrite(light,HIGH);
   save[0]="true";
  }  
  if(command.indexOf("light off") != -1){ 
   digitalWrite(light,LOW);
   save[0]="false";
  }  

  
  if(command.indexOf("tv on") != -1){ 
   digitalWrite(tv,HIGH);
   save[1]="true";
  }  
  if(command.indexOf("tv off") != -1){ 
   digitalWrite(tv,LOW);
   save[1]="false";
  }  

  
  if(command.indexOf("fan on") != -1){ 
   digitalWrite(fan,HIGH);
   save[2]="true";
  }  
  if(command.indexOf("fan off") != -1){ 
   digitalWrite(fan,LOW);
   save[2]="false";
  }   
  
  if(command.indexOf("air on") != -1){ 
   digitalWrite(aircon,HIGH);
   save[3]="true";
  }  
  if(command.indexOf("air off") != -1){ 
   digitalWrite(aircon,LOW);
   save[3]="false";
  }
  
  
  if(command.indexOf("water on") != -1){ 
   digitalWrite(waterd,HIGH);
   save[4]="true";
  }  
  if(command.indexOf("water off") != -1){ 
   digitalWrite(waterd,LOW);
   save[4]="false";
  }
  
  Serial.println(command.indexOf("save"));
  if(command.indexOf("save") != -1 ){ 
    String tosave ="("+String(save[0])+","+String(save[1])+","+String(save[2])+","+String(save[3])+","+String(save[4])+")";
    bth.println(tosave);
    Serial.println(tosave);
  }
  bth.println("..");
}
