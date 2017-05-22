import serial
import time
import json
import struct
from threading import Thread

jsonData = "";

#Check if string is in valid json format
def is_json(myjson):
  try:
    json_object = json.loads(myjson)
  except ValueError, e:
    return False
  return True

#Starts a thread which will read values from a COM port and return
#the value (if it is formatted as JSON) to the callback function.
def readCommValues(port,callback):
    def read(port, callback):
        global ser
        ser = serial.Serial(port,timeout=1)
        data = "";
        while 1:
            data = data + str(ser.read())
            if(is_json(data)):
                print data
                callback(jsonData=data)
                data = ""
    thread = Thread(target = read, args = (port,callback, ))
    thread.start()
