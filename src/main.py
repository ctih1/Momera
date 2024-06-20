import os
os.environ["OPENCV_VIDEOIO_MSMF_ENABLE_HW_TRANSFORMS"] = "0"
import cv2
import time
from collections import deque
import datetime
import string
deque_size:int=3
screenX:int=0
screenY:int=0
frames:deque=deque([],deque_size)
active_camera:int=0
motion_disabled:bool=False
temp_capture:bool=None
writing:bool=False
text:str=""
try:
    capture=cv2.VideoCapture(active_camera,cv2.CAP_ANY)
    capture.set(cv2.CAP_PROP_AUTO_EXPOSURE, 3)
except:
    with open("log.txt","w") as f:
        f.write("ERROR: It does not seem like you have a camera attached to your system.")
names={"deq":"Frame offset", "cam":"Active Camera", "res":"Resolution"}
events:list=[] #[{"event":{"started":timestamp,"end":timestamp}}]

def change_camera(direction):
    global capture, active_camera, screenX, screenY
    testing_camera=active_camera
    if(direction=="left"):
        testing_camera-=1
    if(direction=="right"):
        testing_camera+=1
    try:
        test_capture=cv2.VideoCapture(testing_camera)
        assert test_capture.get(cv2.CAP_PROP_FRAME_WIDTH)>0
        assert test_capture.get(cv2.CAP_PROP_FRAME_HEIGHT)>0
    except Exception as e:
        print(e)
        events.append({"event":"cam","end":time.time()+5,"value":f"Failed camera switch #{testing_camera}"})
        return
    events.append({"event":"cam","end":time.time()+2,"value":f"Using camera #{testing_camera}"})
    screenX=int(capture.get(cv2.CAP_PROP_FRAME_WIDTH))
    screenY=int(capture.get(cv2.CAP_PROP_FRAME_HEIGHT))
    cv2.resizeWindow("frame", screenX, screenY)
    capture=test_capture
    active_camera=testing_camera

def change_deque_size(event,x,y,flags,param):
    global frames, deque_size
    if(event==cv2.EVENT_MOUSEWHEEL):
        new_size=deque_size
        if(flags>0): 
            new_size=deque_size+1
        else:
            if(deque_size<=1):return
            new_size=deque_size-1
        events.append({"event":"deq","end":time.time()+2,"value":new_size})
        deque_size=new_size
        frames = deque(frames,new_size)
def apply_resolution() -> None:
    global screenX, screenY,frames,capture
    resl=text.split("@")
    res=resl[0].split("x")
    if(len(res)!=2):
        events.append({"event":"res","end":time.time()+5,"value":"Invalid resolution"})
        return
    fps=capture.get(cv2.CAP_PROP_FPS)
    if("@" in text):
        fps=resl[1]
    screenX=res[0]
    screenY=res[1]
    frames=deque([],deque_size)
    capture.release()
    capture=cv2.VideoCapture(active_camera)
    try:
        capture.set(cv2.CAP_PROP_FRAME_WIDTH, int(screenX))
        capture.set(cv2.CAP_PROP_FRAME_HEIGHT, int(screenY))
        capture.set(cv2.CAP_PROP_FPS, int(fps))
    except:
        events.append({"event":"res","end":time.time()+5,"value":"Resolution not supported"})
        capture.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
        capture.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
        return
    
def add_info(frame):
    p_frame=frame #processed frame
    added_events=0
    for index, event in enumerate(events):
        if(event["end"]<time.time()):
            events.pop(index)
        added_events+=1
        p_frame= cv2.putText(p_frame,
                             f'{names.get(event["event"], "Undefined")}: {event["value"]}',
                             (00,24*added_events),
                             cv2.FONT_HERSHEY_SIMPLEX,
                             1,
                             (0,0,0),
                             2,
                             cv2.LINE_AA,
                             False
                )
    if writing:
        (x,y,w,h)=cv2.getWindowImageRect("frame")
        p_frame=cv2.putText(p_frame,
            "Enter new camera resolution (ex: 1920x1080@60)",
            (round(w/2)-150, round(h/2)-40),
            cv2.FONT_HERSHEY_SIMPLEX,
            0.5,
            (0,0,0),
            1,
            cv2.LINE_AA,
            False,
        )
        p_frame=cv2.rectangle(p_frame,
                              (round(w/2)-100, round(h/2)-20),
                              (round(w/2)+100, round(h/2)+20),
                              (0,0,0),-1)
        p_frame=cv2.putText(p_frame,
                            text,
                            (round(w/2)-100, round(h/2)+10),
                            cv2.FONT_HERSHEY_SIMPLEX,
                            1,
                            (255,255,255),
                            1,
                            cv2.LINE_AA,
                            False,
        )
    return p_frame
def process_frames(frame):
    if(frames.__len__()!=frames.maxlen): return frame
    if(not motion_disabled):
        opacity:float=0.5
        inverted_frame=cv2.bitwise_not(frames[0])
        frame=(cv2.addWeighted(inverted_frame,opacity,frame,1-opacity,0))
    return add_info(frame)

cv2.namedWindow("frame")
cv2.setMouseCallback("frame",change_deque_size)
if __name__ == "__main__":
    (_,_,w,h)=cv2.getWindowImageRect("frame")
    screenX=w
    screenY=h
    while True:
        if(not capture.isOpened()): change_camera("right")
        ret, frame = capture.read()
        n=datetime.datetime.now()
        cv2.setWindowTitle("frame",f'Motion extraction enabled: {str(not motion_disabled).lower()} camera id: {active_camera} frame delay: {deque_size} @ {n.day}/{n.month}/{n.year} {n.hour}:{n.minute}:{n.second}')
        cv2.imshow("frame" ,process_frames(frame))
        frames.append(frame)
        key = cv2.waitKey(1) & 0xFF
        if key==13:
            writing=not writing
        if(not writing):
            if(text!=""):
                apply_resolution()
                text=""
            if key==27:
                events=[]
            if key==ord("q"):
                break
            if key==ord("k"):
                change_camera("left")
            if key==ord("l"):
                change_camera("right")
            if key==ord("h"):
                motion_disabled=not motion_disabled
        else: 
            for char in string.ascii_lowercase + string.digits + "@":
                if(key==ord(char)):
                    text+=char
            if(key==8):
                text = text[:-1]
    capture.release()
    cv2.destroyAllWindows()