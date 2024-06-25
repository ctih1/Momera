package xyz.ctih1;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Camera implements KeyListener {

    public FrameGrabber capture;
    private final Processor process = new Processor();
    private int cameraId;
    public EventManager eventManager;
    private CanvasFrame window;
    private boolean capturing=true;
    int oldCameraId=0;
    private Mat cameraFeed;
    OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

    public Camera() {
        eventManager = new EventManager();
        window = new CanvasFrame("frame");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        capture = new VideoInputFrameGrabber(0);
        try {
            capture.start();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        window.addKeyListener(this);
        window.setFocusable(true);
        window.setFocusTraversalKeysEnabled(false);

    }
    public void changeCamera(Direction direction) throws InterruptedException, FrameGrabber.Exception {
        eventManager.newEvent("Camera change","Please wait, trying to change camera...",EVENT.NOTIFICATION);
        int tempCameraId=cameraId;
        if(direction==Direction.DOWN) { tempCameraId--; }
        else { tempCameraId++; }
        _changeCamera(tempCameraId);
    }

    private boolean _changeCamera(int id) {
        capturing=false;
        try {
            capture.stop();
            capture.release();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        capture = new VideoInputFrameGrabber(id);
        try {
            capture.start();
            capture.grab();
        } catch (FrameGrabber.Exception e) {
            eventManager.newEvent("Failed to switch camera","Failed to switch to camera id " + cameraId, EVENT.WARNING);
        }

        cameraId=id;
        capturing=true;
        startCamera(cameraId);
        oldCameraId=cameraId;
        return true;
    }
    public void changeResolution(int width, int height) {

    }
    public void startCamera(int id) {
        cameraId=id;
        while(capturing) {
            try {
                cameraFeed=converter.convert(capture.grab());
            } catch (FrameGrabber.Exception e) {
                eventManager.newEvent("Failed to switch camera","Failed to switch to camera id, reverting" + cameraId, EVENT.WARNING);
                _changeCamera(oldCameraId);
            }
            process.appendFrame((cameraFeed));
            if(!window.isVisible()) { return; }
            window.requestFocusInWindow();
            window.showImage(
                    converter.convert(
                            eventManager.update(process.motion(cameraFeed))
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
        switch(e.getKeyCode()) {
            case 38: // ^ (arrow up)
                eventManager.newEvent("Frame delay", String.format("Frame delay changed to %s",process.changeFrameDelay(Direction.UP)), EVENT.NOTIFICATION);
                break;
            case 40: // v (arrow down)
                eventManager.newEvent("Frame delay", String.format("Frame delay changed to %s",process.changeFrameDelay(Direction.DOWN)), EVENT.NOTIFICATION);
                break;
            case 72: // H
                eventManager.newEvent("Motion extraction",String.format("Changed motion extraction status to %s",process.toggleMotion()),EVENT.NOTIFICATION);
                break;
            case 75: // K
                try {
                    changeCamera(Direction.DOWN);
                } catch (InterruptedException | FrameGrabber.Exception ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case 76: // L
                try {
                    changeCamera(Direction.UP);
                } catch (InterruptedException | FrameGrabber.Exception ex) {
                    throw new RuntimeException(ex);
                }
                break;
            default:
                break;
        }
    }
}
