package com.tntrip;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class TnNetUtil {
    //private static Logger LOGGER = LoggerFactory.getLogger(TnNetUtil.class);

    public static List<String> getLocalIpAddr() throws UnknownHostException {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> ipAddrEnum = ni.getInetAddresses();
                while (ipAddrEnum.hasMoreElements()) {
                    InetAddress addr = ipAddrEnum.nextElement();
                    if (addr.isLoopbackAddress()) {
                        continue;
                    }
                    String ip = addr.getHostAddress();
                    if (ip.contains(":")) {
                        //skip the IPv6 addr
                        continue;
                    }
                    if (ni.getName().contains("docker")) {
                        System.out.println("Skipped: " + ni.getName() + ", IP: " + ip);
                        continue;
                    }
                    // LOGGER.info("Interface: " + ni.getName() + ", IP: " + ip);
                    //System.out.println("Interface: " + ni.getName() + ", IP: " + ip);
                    ipList.add(ip);
                }
            }

            if (ipList.isEmpty()) {
                ipList.add(InetAddress.getLocalHost().getHostAddress());
            }
        } catch (Exception e) {
            //LOGGER.error("Failed to get local ip list. failover to InetAddress.getLocalHost().getHostAddress()", e);
            System.out.println("Failed to get local ip list. failover to InetAddress.getLocalHost().getHostAddress()");
            e.printStackTrace();
            ipList.add(InetAddress.getLocalHost().getHostAddress());
        }
        //LOGGER.info("ipList={}", ipList);
        System.out.println("ipList=" + ipList);
        return ipList;
    }

    public static int getTomcatPort() {
        try {
            MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objectNames = beanServer.queryNames(
                    new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1"))
            );
            return Integer.parseInt(objectNames.iterator().next().getKeyProperty("port"));
        } catch (MalformedObjectNameException e) {
            //LOGGER.error("Error when getTomcatPort", e);
            System.out.println("Error when getTomcatPort");
            e.printStackTrace();
        }
        return -1;
    }


    public static void main(String[] args) throws Exception {
        System.out.println(getLocalIpAddr());
    }

}