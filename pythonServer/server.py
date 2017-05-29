from threading import Thread
import serialComm
import json
import time
from pprint import pprint
import robotController
#import fire
from firebase import firebase

firebase = firebase.FirebaseApplication('https://runestone-d1faf.firebaseio.com/', None)
packageBeingHandled = False;
packageInFront = 3; #Time until package is recognised as a package (seconds)
temperatureUpdateTimer = 5; #Time between temperature updates (seconds)
packageIncrement = 0; #Used to give packages seperate package names.


def movePackage(oldX, oldY, destinationX,destinationY):
    idleX = 0
    idleY = 1
    robotController.makePath(idleX,idleY, oldX,oldY)
    robotController.grabAndRelease(1)
    robotController.makePath(oldX,oldY, idleX,idleY)
    robotController.makePath(idleX, idleY, destinationX, destinationY)
    robotController.grabAndRelease(-1)
    robotController.makePath(destinationX, destinationY, idleX, idleY)


#Handles a package once it enters the system. Gives the package a name -> finds slot for package in warehouse -> stores package in database -> sends instruction to robot
def packageHandler():
    emptySlot = findEmptySlot()
    global packageIncrement
    packageName = "Package "+str(packageIncrement)
    packageIncrement += 1
    firebase.put('','/warehouse/'+str(packageName), {'row': (emptySlot[0]), 'shelf':(emptySlot[1]),'temperature':0, 'stored': False}) #Store package in database

    #TODO ROBOT STUFF
    #current x,y of robot - 0,1 is idle position of robot
    idleX = 0
    idleY = 1
    destinationX = int(emptySlot[0])
    destinationY = int(emptySlot[1])
    robotController.grabAndRelease(1)
    robotController.makePath(idleX, idleY, destinationX, destinationY)
    robotController.grabAndRelease(-1)
    robotController.makePath(destinationX, destinationY, idleX, idleY)

    global packageBeingHandled
    while(str(firebase.get('/robots/robot1/', None)) != "{u'shelf': 0, u'row': 0}"):
        packageBeingHandled = True
    packageBeingHandled= False
    print(packageBeingHandled)#Tell system it is ready for another package

#Find the first empty slot in the warehouse
def findEmptySlot():
    warehouse = firebase.get("/maps/test_map", None)
    rows = warehouse['rows']
    shelves = warehouse['shelves']
    wares = firebase.get("/warehouse", None)
    slotOccupied = False
    if wares is not None:
        for row in range(1, rows+1):
            for shelf in range(1, shelves+1):
                for item in wares:
                    if(int(wares[item]['row']) == row and int(wares[item]['shelf']) == shelf):
                        slotOccupied = True
                if(not slotOccupied):
                    return (row,shelf)
                else:
                    slotOccupied = False

    return (1,1)

#Updates the temperature value of items in database
def updateTemperature(temp):
    wares = firebase.get("/warehouse", None)
    global temperatureUpdateTimer
    if(temperatureUpdateTimer <= 0 and wares is not None): #Update when timer reaches 0
        for item in wares:
            if(wares[item]['row'] == 1): #Update all items in row 1
                firebase.patch("/warehouse/"+item,{'temperature':temp})
                temperatureUpdateTimer = 5 #Time between temperature updates
    temperatureUpdateTimer = temperatureUpdateTimer-1

#Reads JSON data and executes the appropriate actions according to the values found in the data.
def handleArduinoValues(jsonData):
    global packageInFront
    global packageBeingHandled
    json_object = json.loads(jsonData)
    updateTemperature(json_object['Temperature'])
    #The value 265 is hardcoded and might need to be calibrated according to the lighting
    #in the room.
    if(json_object['Light'] < 265 and not packageBeingHandled):
        packageInFront = packageInFront-1
    else:
        packageInFront = 3 #Package is recognized as package if sensor is shaded for 3 or more seconds.

    #Check if package is in front of light sensor and calls packageHandler if it is.
    if(packageInFront <= 0 and not packageBeingHandled):
        print "New package has arrived!"
        packageBeingHandled = True
        thread = Thread(target = packageHandler, args = ()) #Start a packageHandler for the package
        thread.start()

#Finds and returns the package with name packageName in the database
def findPackage(packageName):
    searchString = '/warehouse/'+packageName
    result = firebase.get(searchString, None)
    return result

#Returns the key of the added package in new_dict compared to old_dict
def findAddedPackage(old_dict, new_dict):
    for old_key in old_dict:
        for new_key in new_dict:
            if (old_key != new_key):
                return new_key

#Returns the key of the removed package from new_dict compared to old_dict
def findRemovedPackage(old_dict, new_dict):
    for old_key in old_dict:
        for new_key in new_dict:
            if (old_key != new_key):
                return old_key

#Returns a tuple with (old row, old shelf, new row, new shelf) with the coordinates from old_dict and new_dict
#TODO: Make sure that the coordinates are actually different
def findChangedPackage(old_dict, new_dict):
    for key in old_dict:
        if (old_dict[key] != new_dict[key]):
            # Return old row, old shelf, new row, new shelf
            return (old_dict[key]['row'], old_dict[key]['shelf'], new_dict[key]['row'], new_dict[key]['shelf'])


#Find if a package has been added, removed, or changed
def findDifferentValue(old_dict, new_dict):
    if (len(old_dict) < len(new_dict)):
        print("Package added")
        print(findAddedPackage(old_dict, new_dict))
    elif (len(old_dict) > len(new_dict)):
        print("Package removed")
        print(findRemovedPackage(old_dict, new_dict))
    else:
        print("Value changed")
        return(findChangedPackage(old_dict, new_dict))

#Continuously checks firebase DB for updates in the database
def checkForDBUpdates():
    old_result = firebase.get('/warehouse', None)
    while (True):
        new_result = firebase.get('/warehouse', None)
        if (old_result != new_result):
            movePackage(findDifferentValue(old_result, new_result))
            old_result = new_result

if __name__ == "__main__":
    #packageHandler();
    robotController.setup();
    checkDBThread = Thread(target = checkForDBUpdates, args = ())
    checkDBThread.start()
    #serialComm.readCommValues("COM3", handleArduinoValues);
    print "hi"
