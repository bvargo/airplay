package net.bvargo.airplay;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

/**
 * An AirPlay device.
 */
public class AirPlay implements Comparable<AirPlay> {
    public static final int DEFAULT_PORT = 7000;
    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;

    private final String name;
    private final String host;
    private final int port;

    private final int airplayWidth = DEFAULT_WIDTH;
    private final int airplayHeight = DEFAULT_HEIGHT;

    private DesktopPoster desktopPoster;

    /**
     * AirPlay at the given hostname with the given connection password.
     *
     * @param name The human-readable name of the AirPlay device.
     * @param host Host.
     * @param port Port.
     */
    public AirPlay(String name, String host, int port) {
        if(name == null)
            name = "";
        this.name = name;

        String[] hostParts = host.split(":", 2);
        if(hostParts.length == 1) {
            this.host = host;
            this.port = DEFAULT_PORT;
        }
        else {
            this.host = hostParts[0];
            this.port = Integer.parseInt(hostParts[1]);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public void stop() throws IOException {
        this.stopDesktopPoster();
        this.http("POST", "/stop");
    }

    public void showImage(String path, Transition transition)
            throws FileNotFoundException, IOException {
        this.showImage(new File(path), transition);
    }

    public void showImage(File imageFile, Transition transition)
            throws IOException {
		BufferedImage image = ImageIO.read(imageFile);
		this.showImage(image, transition);
    }

	public void showImage(BufferedImage image, Transition transition)
            throws IOException {
		//BufferedImage scaled = new ImageScaler().scaleImage(image,
        //        this.airplayWidth,
        //        this.airplayHeight);
        this.sendImage(image, transition);
	}

    /**
     * Sends the given image with the given transition to the Apple TV.
     */
    private void sendImage(BufferedImage image, Transition transition) throws IOException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Apple-Transition", transition.getName());

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		boolean written = ImageIO.write(image, "png", os);
        if(!written)
            throw new UnsupportedOperationException("Cannot write image type. No writer found.");

		this.http("PUT", "/photo", os, headers);
    }

    /**
     * Shows the desktop on the AirPlay device.
     *
     * @param interval The interval, in ms, that the desktop image should be
     * updated.
     */
    public void showDesktop(int interval) {
		this.stopDesktopPoster();
		this.desktopPoster = new DesktopPoster(this, interval);
		this.desktopPoster.start();
    }

    /**
     * Stops the desktop poster, if it is running.
     */
    private void stopDesktopPoster() {
        if(this.desktopPoster != null) {
            this.desktopPoster.interrupt();
            while(desktopPoster.isAlive()) {
                // wait for the thread to die
            }
            desktopPoster = null;
        }
    }

    private void http(String method, String path) throws IOException {
        Map<String, String> headers = Collections.emptyMap();
        this.http(method, path, null, headers);
    }

	private String http(String method,
            String path,
            ByteArrayOutputStream os,
            Map<String, String> headers)
            throws IOException {
		URL url = null;
		try {
			url = new URL("http://" + this.host + ":" + this.port + path);
		}
        catch(MalformedURLException e) {
            throw new IllegalArgumentException("Bad URL.", e);
        }

		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setRequestMethod(method);

        conn.setRequestProperty("User-Agent", "MediaControl/1.0");
		if(headers.size() > 0) {
            for(Map.Entry<String, String> entry : headers.entrySet())
                conn.setRequestProperty(entry.getKey(), entry.getValue());
		}

		if (os != null) {
			byte[] data = os.toByteArray();
			conn.setRequestProperty("Content-Length", "" + data.length);
		}

		conn.connect();

		if(os != null) {
			os.writeTo(conn.getOutputStream());
			os.flush();
			os.close();
		}

        InputStream is = conn.getInputStream();
        BufferedReader input = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while((line = input.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
        input.close();
        return response.toString();
	}

    @Override
    public int compareTo(AirPlay other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return "AirPlay{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
