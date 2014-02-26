package net.bvargo.airplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * Main CLI driver for the AirPlay tools.
 */
public class Main {
    private static final int TIMEOUT = 2000;

    public static void main(String[] args) {
        try {
            StringBuilder nameBuilder = new StringBuilder();
            String host = null;
            String imagePath = null;
            boolean desktop = false;
            boolean stop = false;
            boolean list = false;

            for(int i = 0; i < args.length; i++) {
                if("--help".equals(args[i]))
                    showUsage();
                return;
            }

            for(int i = 0; i < args.length; i++) {
                if("-h".equals(args[i]) || "--host".equals(args[i]))
                    host = args[++i];
                if("-l".equals(args[i]) || "--list".equals(args[i]))
                    list = true;
                else if("-i".equals(args[i]) || "--image".equals(args[i]))
                    imagePath = args[++i];
                else if("-d".equals(args[i]) || "--desktop".equals(args[i]))
                    desktop = true;
                else if("-s".equals(args[i]) || "--stop".equals(args[i]))
                    stop = true;
                else
                    nameBuilder.append(" ").append(args[i]);
            }
            String name = nameBuilder.toString();

            if(list) {
                List<AirPlay> airplays = findAirPlays();
                System.out.println(listAirPlays(airplays));
                System.exit(0);
                return;
            }

            AirPlay airplay;
            if(host == null && name.isEmpty()) {
                List<AirPlay> airplays = findAirPlays();
                if(airplays.isEmpty()) {
                    System.out.println("No AirPlay devices found.");
                    System.exit(1);
                }

                System.out.println("Please select an AirPlay:");
                System.out.println(listAirPlays(airplays));
                System.out.print("Number: ");

                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String input = br.readLine();

                int selection;
                try {
                    selection = Integer.parseInt(input);
                }
                catch(NumberFormatException e) {
                    System.err.println("Invalid selection: " + input);
                    System.exit(1);
                    return;
                }

                if(selection > airplays.size()) {
                    System.err.println("Invalid selection: " + input);
                    System.exit(1);
                    return;
                }

                airplay = airplays.get(selection);
            }
            else if(host != null) {
                airplay = new AirPlay("CLI", host, AirPlay.DEFAULT_PORT);
            }
            else {
                String n = name.toLowerCase().trim();
                String n2 = n + " apple tv";
                List<AirPlay> airplays = findAirPlays();
                AirPlay a = null;
                for(AirPlay ap : airplays) {
                    String apName = ap.getName().toLowerCase();
                    if(apName.equals(n) || apName.equals(n2))
                        a = ap;
                }

                if(a == null) {
                    System.err.println("Airplay could not be found with name: " + name);
                    System.exit(0);
                    return;
                }
                else {
                    airplay = a;
                }
            }

            if(stop)
                airplay.stop();
            else if(imagePath != null)
                airplay.showImage(imagePath, Transition.NONE);
            else
                airplay.showDesktop(100);
        }
        catch(Exception e) {
            System.err.println("Error: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static List<AirPlay> findAirPlays() throws IOException {
        AirPlayFinder finder = new AirPlayFinder();
        List<AirPlay> airplays = finder.search(TIMEOUT);
        Collections.sort(airplays);
        return airplays;
    }

    private static String listAirPlays(List<AirPlay> airplays) {
        if(airplays.isEmpty()) {
            return "No AirPlay devices found.";
        }
        else {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for(AirPlay airplay : airplays) {
                sb.append(i).append(": ").append(airplay.getName()).append("\n");
                i++;
            }
            return sb.toString();
        }
    }

    private static void showUsage() {
        System.err.println("USAGE: java -jar <jar>");
        System.err.println("USAGE: java -jar <jar> -l [-i <path> | -d | -s]");
        System.err.println("USAGE: java -jar <jar> <device name> [-i <path> | -d | -s]");
        System.err.println("USAGE: java -jar <jar> -h <host> [-i <path> | -d | -s]");
        System.err.println("");
        System.err.println("Options:");
        System.err.println("    -l, --list            Lists available airplay serviers.");
        System.err.println("    -h, --host <host>     The host of the airplay server.");
        System.err.println("    -i, --image <path>    Show a given image.");
        System.err.println("    -d, --desktop         Shows the desktop (mirroring).");
        System.err.println("    -s, --stop            Stops showing content.");
        System.err.println("");
        System.err.println("Omitting all arguments will scan for AirPlay devices. Select a device from the");
        System.err.println("provided menu to begin desktop mirroring.");
        System.err.println("");
        System.err.println("When showing an image or the desktop, use CTRL-C to stop.");
    }
}
