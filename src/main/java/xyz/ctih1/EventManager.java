package xyz.ctih1;


import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.util.*;

import static java.lang.Integer.parseInt;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class EventManager {
    private int alerts=0;
    private List<HashMap<String,Object>> events = new ArrayList<>();
    private HashMap<String,Object> tempEvent;
    public EventManager() {

    }
    public Mat update(Mat frame) {

        for(HashMap<String,Object> element : events) {
            if(parseInt(element.get("expire").toString())>System.currentTimeMillis() / 1000L) {
                events.remove(element);
                return frame;
            }
            putText(frame,element.get("name").toString(),new Point(0,30*alerts),FONT_HERSHEY_COMPLEX,2.0,new Scalar(255,255,255,2.0));
            //int[] textSize = new int[0];
            //getTextSize(element.get("name").toString(),FONT_HERSHEY_PLAIN,2.0,2,textSize);
            //System.out.println(Arrays.toString(textSize));
            rectangle(frame,new Point(0,(30*alerts)-30),new Point(150,(30*alerts)+10), Scalar.BLACK);
        }
        return frame;
    }
    public void newEvent(String name, String description, Event event) {
        alerts++;
        tempEvent = new HashMap<>();
        tempEvent.put("name",name);
        tempEvent.put("description",description);
        tempEvent.put("event",event);
        tempEvent.put("expire",(System.currentTimeMillis() / 1000L)+5);
        events.add(tempEvent);
    }
}
