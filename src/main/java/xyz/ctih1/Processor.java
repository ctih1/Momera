package xyz.ctih1;

import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.LinkedList;

public class Processor {
    private final LinkedList<Mat> frames = new LinkedList<Mat>();
    final double opacity = 0.5;

    private int maxItems=3;
    private boolean motionDisabled=false;
    private Mat invertedFrame;
    private Mat motionFrame;
    public Processor() {
    }

    public void toggleMotion() {
        motionDisabled=!motionDisabled;
    }

    public void changeFrameDelay(Direction direction ) {
        if (direction == Direction.DOWN||maxItems>1) {
            maxItems--;
        } else if (direction == Direction.UP) {
            maxItems++;
        }
    }
    public void appendFrame(Mat frame) {
        if(frames.size()>=maxItems) {
            frames.removeFirst();
        }
        frames.add(frame.clone());
    }
    public Mat motion(Mat currentFrame) {
        if(frames.size()!=maxItems || motionDisabled) { return currentFrame; }
        assert(currentFrame!=null);
        invertedFrame=currentFrame.clone();
        motionFrame=currentFrame.clone();
        opencv_core.bitwise_not(frames.getFirst(),invertedFrame);
        opencv_core.addWeighted(invertedFrame,opacity,currentFrame,1-opacity,0, motionFrame);
        return motionFrame;
    }
}
