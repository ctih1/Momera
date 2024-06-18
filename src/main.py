import os
os.environ["OPENCV_VIDEOIO_MSMF_ENABLE_HW_TRANSFORMS"] = "0"
import cv2
import time
from collections import deque
import datetime
deque_size=3
frames=deque([],deque_size)
active_camera=0
motion_disabled:bool=False
capture=cv2.VideoCapture(active_camera)
capture.set(cv2.CAP_PROP_AUTO_EXPOSURE, 3)
names={"deq":"Frame offset", "cam":"Active Camera"}
events:list=[] #[{"event":{"started":timestamp,"end":timestamp}}]

def change_camera(direction):
    global capture, active_camera
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
        events.append({"event":"cam","end":time.time()+5,"value":f"Failed camer switch #{testing_camera}"})
        return
    events.append({"event":"cam","end":time.time()+2,"value":f"Using camera #{testing_camera}"})
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
def add_info(frame):
    if(events.__len__()==0): return frame
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
    
    while True:
        ret, frame = capture.read()
        n=datetime.datetime.now()
        cv2.setWindowTitle("frame",f'Motion extraction enabled: {str(motion_disabled).lower()} camera id: {active_camera} frame delay: {deque_size} @ {n.day}/{n.month}/{n.year} {n.hour}:{n.minute}:{n.second}')
        cv2.imshow("frame" ,process_frames(frame))
        frames.append(frame)
        key = cv2.waitKey(1) & 0xFF
        if key == 27:
            events=[]
        if key==ord("q"):
            break
        if key==ord("k"):
            change_camera("left")
        if key==ord("l"):
            change_camera("right")
        if key==ord("h"):
            motion_disabled=not motion_disabled
    capture.release()
    cv2.destroyAllWindows()