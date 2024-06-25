package xyz.ctih1;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import java.util.*;

import static java.lang.Integer.parseInt;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class EventManager {
    private int alerts=0;
    private List<HashMap<String,Object>> events = new ArrayList<>();
    private List<HashMap<String,Object>> toRemove = new ArrayList<>();
    private Mat tempFrame;
    private final int padding=10;
    private HashMap<String,Object> tempEvent;
    public EventManager() {

    }
    public Mat update(Mat frame) {
        tempFrame = frame;
        tempFrame=drawEvents();
        return tempFrame;
    }
    private Mat drawEvents() {
        int nth=0;
        events.removeAll(toRemove);
        for(Iterator<HashMap<String,Object>> iterator = events.iterator(); iterator.hasNext();) {
            nth++;
            HashMap<String,Object> element = iterator.next();
            if(parseInt(element.get("expire").toString())<System.currentTimeMillis() / 1000L) {
                iterator.remove();
            }
            IntPointer baseLine = new IntPointer();
            Size textSize = getTextSize(element.get("name").toString(),FONT_HERSHEY_COMPLEX,1.0,2, baseLine);
            Size descSize = getTextSize(element.get("description").toString(),FONT_HERSHEY_COMPLEX,0.5,2, baseLine);
            rectangle(tempFrame,new Point(0,(30*nth)-(textSize.get(1))),new Point(Math.max(textSize.get(0),descSize.get(0))+padding,((30*nth)+textSize.get(1))+descSize.get(1)), EVENT.valueOf(String.valueOf(element.get("event"))).getColor(),-1,4,0);
            putText(tempFrame,element.get("name").toString(),new Point(0,30*nth),FONT_HERSHEY_COMPLEX,1.0,new Scalar(255,255,255,2.0));
            putText(tempFrame,element.get("description").toString(),new Point(0,(30*nth)+textSize.get(1)),FONT_HERSHEY_COMPLEX,0.5,new Scalar(200,200,200,2.0));
        }
        return tempFrame;
    }
    public void newEvent(String name, String description, EVENT event) {
        tempEvent = new HashMap<>();
        tempEvent.put("name",name);
        tempEvent.put("description",description);
        tempEvent.put("event",event);
        tempEvent.put("expire",(System.currentTimeMillis() / 1000L)+event.getTime());
        events.add(tempEvent);
    }
}
