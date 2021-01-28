
package org.apache.zookeeper.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.zookeeper.common.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workaround for Zookeeper defect in JDK-14+
 */
public final class ConnectStringParser {
    private static final int DEFAULT_PORT = 2181;
    private static Logger log = LoggerFactory.getLogger(ConnectStringParser.class);

    private final String chrootPath;

    private final ArrayList<InetSocketAddress> serverAddresses = new ArrayList<InetSocketAddress>();

    /**
     *
     * @throws IllegalArgumentException
     *             for an invalid chroot path.
     */
    public ConnectStringParser(String connectString) {
        // parse out chroot, if any
        int off = connectString.indexOf('/');
        if (off >= 0) {
            String chrootPath = connectString.substring(off);
            // ignore "/" chroot spec, same as null
            if (chrootPath.length() == 1) {
                this.chrootPath = null;
            } else {
                PathUtils.validatePath(chrootPath);
                this.chrootPath = chrootPath;
            }
            connectString = connectString.substring(0, off);
        } else {
            this.chrootPath = null;
        }

        String hostsList[] = connectString.split(",");
        for (String host : hostsList) {
            int port = DEFAULT_PORT;
            int pidx = host.lastIndexOf(':');
            if (pidx >= 0) {
                // otherwise : is at the end of the string, ignore
                if (pidx < host.length() - 1) {
                    port = Integer.parseInt(host.substring(pidx + 1));
                }
                host = host.substring(0, pidx);
            }
            try {
                serverAddresses.add(new InetSocketAddress(InetAddress.getByName(host), port));
            } catch (UnknownHostException e) {
                log.error("Unable to resolve the hostname of the Zookeeper server", e);
                throw new RuntimeException(e);
            }
        }
    }

    public String getChrootPath() {
        return chrootPath;
    }

    public ArrayList<InetSocketAddress> getServerAddresses() {
        return serverAddresses;
    }
}