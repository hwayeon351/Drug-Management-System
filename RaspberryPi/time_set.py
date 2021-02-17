import schedule
import RPi.GPIO as GPIO
import time
import requests


    
            
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
        
web_url = "http://210.94.185.17:8888/mysetting2_php.php"
r = requests.get(web_url)
time=r.text
split_data = time.split(" ")
time1 = split_data[0] 
time2 = split_data[1]
time3 = split_data[2]
print(time1)
print(time2)
print(time3)

schedule.every().day.at(time1).do(ControlSpeaker)
schedule.every().day.at(time2).do(ControlSpeaker)
#schedule.every().day.at(time3).do(ControlSpeaker)
                     
while True:
    schedule.run_pending()
    time.sleep(1)

