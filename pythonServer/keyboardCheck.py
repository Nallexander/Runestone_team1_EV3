from threading import Thread
import robotController

def checkForManual():
    pygame.init()
    pygame.display.iconify()
    
    def check():
        while True:
            for event in pygame.event.get():
                if event.type == pygame.KEYDOWN:
                    robotController.keyboardMode()
        
    checkThread = Thread(target = check, args =())
    checkThread.start()