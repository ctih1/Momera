package xyz.ctih1;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Camera implements KeyListener {

    public FrameGrabber capture;
    public Mat frame;
    private final Processor process = new Processor();
    private int cameraId;
    public EventManager eventManager;
    private CanvasFrame window;
    OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

    public Camera() {
        eventManager = new EventManager();
        window = new CanvasFrame("frame");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        capture = new OpenCVFrameGrabber(0);
        window.addKeyListener(this);
        window.setFocusable(true);
        window.setFocusTraversalKeysEnabled(false);

    }
    public void changeCamera(Direction direction) {
        if(direction==Direction.DOWN) { cameraId--; }
        else { cameraId++; }

        try { capture.stop(); }
        catch (FrameGrabber.Exception e) {throw new RuntimeException(e);}

        capture = new OpenCVFrameGrabber(cameraId);

        try { capture.start(); }
        catch (FrameGrabber.Exception e) {throw new RuntimeException(e);}
    }
    public void changeResolution(int width, int height) {

    }
    public void startCamera(int id) throws FrameGrabber.Exception, InterruptedException {
        cameraId=id;
        capture.start();
        while((frame=converter.convert(capture.grab()))!=null){
            process.appendFrame((frame));

            if(!window.isVisible()) { return; }
            window.requestFocusInWindow();
            window.showImage(
                    converter.convert(
                            eventManager.update(process.motion(frame))
                    )
            );
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        eventManager.newEvent("Testing","This is a testing message",Event.NOTIFICATION);
        switch(e.getKeyCode()) {
            case 72: // H
                process.toggleMotion();
                break;
            case 75: // K
                changeCamera(Direction.DOWN);
                break;
            case 76: // L
                changeCamera(Direction.UP);
                break;
            default:
                break;
        }
    }
}
