package xyz.ctih1;
import org.bytedeco.javacv.FrameGrabber;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            final Camera camera = new Camera();
            new Thread(() -> {
                camera.startCamera(0);
            }).start();
        });

    }
}