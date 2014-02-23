package net.bvargo.airplay;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Finds airplay devices for connecting.
 */
public class AirPlayFinder {
	private static final String DNSSD_TYPE = "_airplay._tcp.local.";

    /**
     * Search for airplay devices within the given timeout, in ms.
     */
    public List<AirPlay> search(int timeout) throws IOException {
		JmDNS jmdns = JmDNS.create(InetAddress.getByName("0.0.0.0"), null);
		ServiceInfo[] services = jmdns.list(DNSSD_TYPE, timeout);

		List<AirPlay> airplays = new ArrayList<AirPlay>();
		for(int i = 0; i < services.length; i++) {
			ServiceInfo service = services[i];
			Inet4Address[] addresses = service.getInet4Addresses();
			airplays.add(new AirPlay(service.getName(),
                        addresses[0].getHostAddress(),
                        service.getPort()));
		}
		jmdns.close();
		return airplays;
    }
}
