package dk.appdictive;

import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
    private static final String IP_ADDRESS_REGEX = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}", PREF_IP_NAME = "SAVED_IP";

    public static void main(String args[])
    {
        String ipAddress = "";

        // Retrieve the user preference node for the package
        Preferences systemRoot = Preferences.userRoot();
        Preferences prefs = systemRoot.node("dk/appdictive/adbconnect");

        // Get the value of the preference;
        // default value is returned if the preference does not exist
        String defaultValue = "";
        String propertyValue = prefs.get(PREF_IP_NAME, defaultValue); // "a string"

        System.out.println("Trying saved IP");

        if (propertyValue.equals("")) {
            //if no IP has been previously saved, get the current
            ipAddress = getIP();
        } else {
            ipAddress = propertyValue;
        }

        System.out.println("IP: " + ipAddress);

        if (connectToIP(ipAddress)) {
            System.out.println("Connected: " + ipAddress);
            saveIP(ipAddress, prefs);
        } else {
            ipAddress = getIP();
            if (connectToIP(ipAddress)) {
                System.out.println("Connected: " + ipAddress);
                saveIP(ipAddress, prefs);
            }
        }

        System.out.println("Done");
    }

    public static void saveIP(String ip, Preferences prefs) {
        prefs.put(PREF_IP_NAME, ip);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    public static boolean connectToIP(String ip) {
        String port = "5555";
        runCommand("adb tcpip " + port);
        String output = runCommand("adb connect " + ip + ":" + port);
        System.out.println(output);
        if (output.contains("unable")) {
            return false;
        } else {
            return true;
        }
    }

    public static String getIP() {
        String output = runCommand("adb shell ip -f inet addr show wlan0");
        System.out.println(output);

        Pattern ipAddressPattern = Pattern.compile(IP_ADDRESS_REGEX);

        Matcher m = ipAddressPattern.matcher(output);
        String ipAddress = "";
        while (m.find()) {
            String s = m.group(0);
            if (ipAddress.equals("")) {
                //we assume the first IP address is the one we want to address
                ipAddress = s;
            }
        }

//        System.out.println(ipAddress);

        return ipAddress;
    }

    public static String runCommand(String command) {
        try
        {
            Process p=Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader=new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );
            String result = "";
            String line;
            while((line = reader.readLine()) != null)
            {
                result += line;
            }
            return result;
        }
        catch(IOException e1) {}
        catch(InterruptedException e2) {}

        return null;
    }





}
