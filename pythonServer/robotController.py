from threading import Thread
import pygame
import robotSend
import time

MAX_ROWS = 4
MAX_SHELVES = 5


def goStraight(speed, direction):
    return "{ \"type\": \"2\" , \"content\" : { \"motor\" : \"3\" , \"direction\" : \"" + str(direction) + "\" , \"speed\" : \"" + str(speed) + "\" }}\n"

def turnRight(speed, direction):
    return "{ \"type\": \"2\" , \"content\" : { \"motor\" : \"1\" , \"direction\" : \"" +str(direction)+ "\" , \"speed\" : \""+ str(speed) + "\" }}\n"

def elevator(angle, direction):
    return "{ \"type\": \"2\" , \"content\" : { \"motor\" : \"4\" , \"direction\" : \"" +str(direction)+ "\" , \"angle\" : \""+ str(angle) + "\" }}\n"

def JSONkey(key, press):
    return "{ \"type\" : \"1\", \"content\" : { \"key\" : \"" +str(key)+ "\", \"press\" : \""+str(press)+"\" }}\n"

def makePath (loc_x, loc_y, dest_x, dest_y):
    if loc_x != dest_x: #location and destination are not on the same row

        if loc_x != 0:
            print("hej")
            mv_on_row = MAX_SHELVES + 1 - loc_y
            for i in range(mv_on_row):
                robotSend.path.append((loc_x, loc_y + i + 1))
            cur_x = loc_x
            cur_y = loc_y + i + 1
            
        else: #if the current row is 0
            mv_on_row = 1
            robotSend.path.append((0, 0))
            cur_x = 0
            cur_y = 0

        for i in range(mv_on_row):
            robotSend.instructions.put(goStraight(200, 1))

        robotSend.instructions.put(turnRight(360, 1))
        robotSend.path.append(None)

        for i in range(abs(loc_x - dest_x)):
            robotSend.instructions.put(goStraight(200, 1))
            if cur_x == 0:
                robotSend.path.append((cur_x + i + 1, cur_y))
            else:
                robotSend.path.append((cur_x - i - 1, cur_y))
        
        if cur_x == 0:
            cur_x = cur_x + i +1
        else:
            cur_x = 0

        robotSend.instructions.put(turnRight(360, 1))
        robotSend.path.append(None)

        for i in range(dest_y):
            robotSend.instructions.put(goStraight(200, 1))
            if cur_x == 0:
                robotSend.path.append((0, 1))
            else:
                robotSend.path.append((cur_x, i + 1))

def grabAndRelease(direction):
    robotSend.path.append(None)
    robotSend.path.append(None)
    robotSend.path.append(False)
    robotSend.path.append(None)
    robotSend.path.append(None)
    
    robotSend.instructions.put(turnRight(360, 1))
    robotSend.instructions.put(goStraight(200, 1))
    robotSend.instructions.put(elevator(200, direction))
    robotSend.instructions.put(goStraight(200, -1))
    robotSend.instructions.put(turnRight(360, -1))

def keyboardMode():
    pygame.init()
    pygame.display.iconify()
    while True:
        for event in pygame.event.get():
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    pygame.display.quit()
                    return
                if event.key == pygame.K_UP:
                    robotSend.instructions.put(JSONkey(3, 1))
                if event.key == pygame.K_DOWN:
                    robotSend.instructions.put(JSONkey(4, 1))
                if event.key == pygame.K_LEFT:
                    robotSend.instructions.put(JSONkey(2, 1))
                if event.key == pygame.K_RIGHT:
                    robotSend.instructions.put(JSONkey(1, 1))
                if event.key == pygame.K_b:
                    robotSend.instructions.put(JSONkey(5, 1))
                if event.key == pygame.K_n:
                    robotSend.instructions.put(JSONkey(6, 1))

            if event.type == pygame.KEYUP:
                if event.key == pygame.K_UP:
                    robotSend.instructions.put(JSONkey(3, -1))
                if event.key == pygame.K_DOWN:
                    robotSend.instructions.put(JSONkey(4, -1))
                if event.key == pygame.K_LEFT:
                    robotSend.instructions.put(JSONkey(2, -1))
                if event.key == pygame.K_RIGHT:
                    robotSend.instructions.put(JSONkey(1, -1))
                if event.key == pygame.K_b:
                    robotSend.instructions.put(JSONkey(5, -1))
                if event.key == pygame.K_n:
                    robotSend.instructions.put(JSONkey(6, -1))


def setup():
    robotSend.sendRobotInstructions("10.0.1.1", 1111)
    makePath(0, 1, 4, 5)
    grabAndRelease(1)
    makePath(4, 5, 0, 1)
    grabAndRelease(-1)
    #keyboardMode()
    print("done")

#if __name__ == "__main__":
    #print("done")
