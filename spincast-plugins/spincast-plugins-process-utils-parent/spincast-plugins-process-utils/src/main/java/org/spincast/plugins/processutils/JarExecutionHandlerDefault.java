package org.spincast.plugins.processutils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.processutils.exceptions.PortNotOpenException;

public class JarExecutionHandlerDefault implements JarExecutionHandler {

    protected static final Logger logger = LoggerFactory.getLogger(JarExecutionHandlerDefault.class);

    private Process jarProcess;
    private boolean killingRequested = false;

    @Override
    public Process getJarProcess() {
        return this.jarProcess;
    }

    @Override
    public void setJarProcess(Process jarProcess) {
        this.jarProcess = jarProcess;
    }

    protected boolean isKillingRequested() {
        return this.killingRequested;
    }

    public void setKillingRequested() {
        this.killingRequested = true;
    }

    /**
     * Wait for the specified host:port to be listened to.
     *
     * @throws PortNotOpenException if the port is still not
     * reachable after "sleepMilliseconds" milliseconds.
     */
    public void waitForPortOpen(String host, int port, int nbrTry, int sleepMilliseconds) {

        try {
            boolean portReached = false;
            int loopNbr = 1;
            while (loopNbr++ <= nbrTry) {

                if (!getJarProcess().isAlive()) {
                    onException(new RuntimeException("Jar process not alive!"));
                }

                try {
                    logger.info("Try to connect to connect to " + host + ":" + port + "...");
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(host, port));
                    portReached = true;

                    try {
                        socket.close();
                    } catch (Exception ex) {
                        //...
                    }
                    break;
                } catch (IOException e) {
                    Thread.sleep(sleepMilliseconds);
                }
            }

            if (!portReached) {
                onException(new PortNotOpenException("Unable to connect to " + host + ":" + port + ", even after " +
                                                     sleepMilliseconds +
                                                     " milliseconds."));
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Kill the jar execution process.
     */
    @Override
    public void killJarProcess() {

        setKillingRequested();

        if (!getJarProcess().isAlive()) {
            return;
        }

        try {
            logger.info("Killing jar process...");

            getJarProcess().destroy();

            int secondNbr = 0;
            while (secondNbr++ < 10) {
                if (!getJarProcess().isAlive()) {
                    break;
                }
                Thread.sleep(1000);
            }

            //==========================================
            // Force
            //==========================================
            if (getJarProcess().isAlive()) {
                getJarProcess().destroyForcibly();
            }
            logger.info("Jar process killed.");
        } catch (Exception ex) {

            //==========================================
            // Last chance before an exception
            //==========================================
            try {
                Thread.sleep(100);
            } catch (Exception ex2) {
                // ...
            }
            if (!getJarProcess().isAlive()) {
                return;
            }

            throw new RuntimeException("Unable to kill the jar process properly", ex);
        }
    }

    /**
     * Called when the jar execution process exits
     */
    @Override
    public void onExit(int exitVal) {

        if (isKillingRequested()) {
            logger.info("Jar thread killed (exited with code " + exitVal + ").");
        } else if (exitVal != 0) {
            logger.error("Jar thread exited with code " + exitVal + ".");
        } else {
            logger.info("Jar thread exited succesfully");
        }
    }

    @Override
    public void onException(Exception ex) {

        try {
            killJarProcess();
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }

        throw new RuntimeException("Exception in the jar execution thread", ex);
    }

}
