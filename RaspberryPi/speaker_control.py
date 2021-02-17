import schedule
import RPi.GPIO as GPIO
import time

def ControlSpeaker():
    GPIO.setwarnings(False)
    GPIO.setmode(GPIO.BCM)

    buzzer = 21
    scale = [261, 294, 329, 349, 392, 440, 493]
    
    GPIO.setup(buzzer,GPIO.OUT)

    p = GPIO.PWM(buzzer,600)
    
    p.start(50)
    try:
        for i in range(7):
            p.ChangeFrequency(scale[i])
            time.sleep(0.5)
    finally:
        p.stop()
        GPIO.cleanup()

f = open('./set_time.txt', 'r')
for data in f:
    schedule.every().day.at(data).do(ControlSpeaker)

while True:
    schedule.run_pending()
    time.sleep(1)
