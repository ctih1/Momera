package xyz.ctih1;

import org.bytedeco.opencv.opencv_core.Scalar;

enum Direction {
    DOWN,
    UP
}
enum EVENT {
    WARNING(5, new Scalar(0,176,255,2.0)), // JavaCVs Scalar uses the BGR color format.
    NOTIFICATION(2, new Scalar(45,45,45,2.0)),
    ERROR(15, new Scalar(26,26,255,2.0));
    private int time;
    private Scalar color;

    EVENT(int time, Scalar color) {
        this.time = time;
        this.color = color;
    }
    public int getTime() {
        return time;
    }
    public Scalar getColor() {
        return color;
    }
}