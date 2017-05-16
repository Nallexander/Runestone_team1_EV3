from threading import Thread
import serialComm
import json
import time

packageBeingHandled = False;

def packageHandler():
    #TODO - Give first robot in line instructions to pick up package
    print "Handling package"
    time.sleep(2)
    print "Done"

    global packageBeingHandled
    packageBeingHandled = False #Tell system it is ready for another package

def findEmptySlot():
    print "Empty slot found/notfound"
    #TODO - Find empty slot in database

#Reads JSON data and executes the appropriate actions according to the values found in the data.
def handleArduinoValues(jsonData):
    json_object = json.loads(jsonData)
    #print json_object['Temperature']
    #TODO - store temperature value in database?

    global packageBeingHandled
     #Check if package is in front of light sensor and sends to packageHandler if it is.
     #The value 265 is hardcoded and might need to be calibrated according to the lighting
     #in the room.
    if(json_object['Light'] < 265 and not packageBeingHandled):
        print "New package has arrived!"
        packageBeingHandled = True;
        packageHandler()



if __name__ == "__main__":
    serialComm.readCommValues("COM4", handleArduinoValues)
