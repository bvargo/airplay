package net.bvargo.airplay;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Thread that posts a desktop screenshot to the AirPlay at a given interval.
 */
class DesktopPoster extends Thread {
    // color/radius of the mouse pointer
    private static final Color COLOR = Color.RED;
    private static final int RADIUS = 10;

    private final AirPlay airplay;
    private final int interval;

    /**
     * @param interval Interval, in ms, at which the desktop image should be
     * posted.
     */
    public DesktopPoster(AirPlay airplay, int interval) {
        this.airplay = airplay;
        this.interval = interval;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            BufferedImage image = this.captureScreen();
            if(image != null) {
                try {
                    airplay.showImage(image, Transition.NONE);
                    try {
                        Thread.sleep(this.interval);
                    }
                    catch(InterruptedException e) {
                        // stop running
                        break;
                    }
                }
                catch(IOException e) {
                    System.err.println("Could not post image: " + e);
                }
            }
        }
    }

    /**
     * Captures the screen.
     *
     * @return null if a screenshot could not be taken.
     */
    private BufferedImage captureScreen() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        Rectangle rect = new Rectangle(dim);
        Robot robot = null;

        try {
            robot = new Robot();
        }
        catch(AWTException e) {
            return null;
        }
        BufferedImage image = robot.createScreenCapture(rect);

        // draw an oval where the mouse pointer is, since the screen capture
        // does not include the pointer
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;

        Graphics g = image.createGraphics();
        g.setColor(COLOR);
        g.fillOval(x - RADIUS, y - RADIUS, RADIUS * 2, RADIUS * 2);
        g.dispose();

        return image;
    }
}
