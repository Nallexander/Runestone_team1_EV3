import socket
import server
import select
import queue
from threading import Thread

instructions = queue.Queue()

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
            print("here")
            msg += s.recv(1).decode("UTF-8")
            if '\n' in msg:  
                print(msg)
                msg=''
            
    
    thread = Thread(target = send, args = ())
    thread.start()
    thread1 = Thread(target = recv, args = ())
    thread1.start()
            