from threading import Thread
import pygame
import robotController

def checkForManual():
    pygame.init()
    pygame.display.iconify()
    
    def check():
        while True:
            for event in pygame.event.get():
                if event.type == pygame.KEYDOWN:
                    print("You are now in manual mode")
                    robotController.keyboardMode()
        
    checkThread = Thread(target = check, args =())
    checkThread.start()