import RPi.GPIO as GPIO
import time
import board
import neopixel
import schedule


pixels = neopixel.NeoPixel(board.D10,4)
buzzer = 21
scale = [261, 294, 329, 349, 392, 392, 392]

GPIO.setup(buzzer,GPIO.OUT)
p = GPIO.PWM(buzzer,600)
time.sleep(0.5)



while True:
    
    GPIO.setmode(GPIO.BCM) # setting GPIO pins for using sensor
    GPIO.setwarnings(False) # prohibit any warnings of GPIO
    LIGHT_PIN = 23 

    GPIO.setup(LIGHT_PIN, GPIO.IN) # setup GPIO Pin (Sensor, IN&OUT)
    
    lOld = not GPIO.input(LIGHT_PIN) 
  #setting value
 
    if GPIO.input(LIGHT_PIN) == lOld:
   # if GPIO.input(LIGHT_PIN): 
      #print ('Take this pill with LED :D')
        pixels.fill((0,0,255))
        pixels.show()
        time.sleep(3)
        pixels.fill((0,0,0))
        f = open('/home/pi/jodo_time.txt','w')
        import datetime
        now = datetime.datetime.now().strftime("%Y%m%d %H:%M:%S")
        print(now)
            
        f.write(now)
        f.close()
        time.sleep(5)
#     print(lOld)
#     time.sleep(0.2)
    
"""
    else:
        p.start(50)
        try:
            for i in range(7):
                p.ChangeFrequency(scale[i])
                time.sleep(0.5)

        finally:
            p.stop()
            GPIO.cleanup()

    #else:
     # print ('')

   # lOld = not GPIO.input(LIGHT_PIN)
"""