package org.spincast.plugins.processutils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.processutils.exceptions.PortNotOpenException;

public class ProcessExecutionHandlerDefault implements HttpServerProcessHandlerExecutionHandler {

    protected static final Logger logger = LoggerFactory.getLogger(ProcessExecutionHandlerDefault.class);

    private Process process;
    private boolean killingRequested = false;
    private ProcessKiller processKiller = null;

    protected Process getProcess() {
        return this.process;
    }

    /**
     * Can be <code>null</code> if the process
     * was not created.
     */
    protected ProcessKiller getProcessKiller() {
        return this.processKiller;
    }

    @Override
    public boolean isProcessAlive() {
        return getProcess() != null && getProcess().isAlive();
    }

    @Override
    public void setProcessAndProcessKiller(Process process, ProcessKiller processKiller) {
        this.process = process;
        this.processKiller = processKiller;
    }

    protected boolean isKillingRequested() {
        return this.killingRequested;
    }

    public void setKillingRequested() {
        this.killingRequested = true;
    }

    @Override
    public void waitForPortOpen(String host, int port, int nbrTry, int sleepMilliseconds) throws PortNotOpenException {

        boolean portConnectable = false;

        try {
            int loopNbr = 1;
            while (loopNbr++ <= nbrTry) {

                if (isKillingRequested()) {
                    return;
                }

                if (getProcess() == null) {
                    Thread.sleep(sleepMilliseconds);
                    continue;
                }

                if (!getProcess().isAlive()) {
                    logger.warn("Process not alive anymore!");
                    return;
                }

                try {
                    logger.info("Try to connect to connect to " + host + ":" + port + "...");
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(host, port));
                    portConnectable = true;

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
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

        if (!portConnectable) {
            throw new PortNotOpenException("Unable to connect to " + host + ":" + port + ".");
        }
    }

    @Override
    public void killProcess() {
        setKillingRequested();
        //==========================================
        // The process killer is only available if
        // the process if created.
        //==========================================
        if (getProcessKiller() != null) {
            getProcessKiller().killProcess();
        }
    }

    /**
     * Called when the execution process exits
     */
    @Override
    public void onExit(int exitCode) {

        String msg = "Executed program exited with code " + exitCode + ".";
        if (exitCode == 0) {
            logger.info(msg);
        } else {
            logger.error(msg);
        }
    }

    @Override
    public void onLaunchException(Exception ex) {
        logger.error("Exception launching the program:\n" + SpincastStatics.getStackTrace(ex));
    }

    @Override
    public void onEnd() {
        if (isKillingRequested()) {
            logger.info("Executed program killed!");
        }
    }

    @Override
    public void onTimeoutException() {
        logger.error("Timeout exceeded running the program");
    }

    @Override
    public void onSystemOut(String line) {
        System.out.println(line);
    }

    @Override
    public void onSystemErr(String line) {
        System.err.println(line);
    }


}
