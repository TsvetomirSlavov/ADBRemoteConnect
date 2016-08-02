package dk.appdictive;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main
{
    private static final String IP_ADDRESS_REGEX = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";

    public static void main(String args[])
    {
        String output = runCommand("adb -d shell ip -f inet addr show wlan0");
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

        System.out.println("IP: " + ipAddress);

        String port = "5555";
        runCommand("adb tcpip " + port);
        output = runCommand("adb connect " + ipAddress + ":" + port);
        System.out.println(output);

        System.out.println("Done");
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
