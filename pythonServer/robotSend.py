import socket
import select
import Queue
from threading import Thread
from firebase import firebase

firebase = firebase.FirebaseApplication('https://runestone-d1faf.firebaseio.com/', None)
instructions = Queue.Queue()
path = []


def sendRobotInstructions(addr, port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((addr, port))

    def send():
        while(True):
            if not instructions.empty():
                string = instructions.get()
                print(string)
                s.send(string.encode("UTF-8"))

    def recv():
        msg =''
        while(True):
            msg, _ = s.recvfrom(1024)
            if msg != '':
                pathTuple = path.pop(0)
                if(pathTuple):
                    print(pathTuple)
                    firebase.put('','/robots/robot1', {'row': pathTuple[0], 'shelf':pathTuple[1]})
                

    sendThread = Thread(target = send, args = ())
    sendThread.start()
    recvThread = Thread(target = recv, args = ())
    recvThread.start()
