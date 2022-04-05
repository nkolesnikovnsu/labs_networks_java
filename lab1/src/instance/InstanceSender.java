package instance;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class InstanceSender extends Thread {

    private final ConnectableInstance ownerInstance;

    private final DatagramSocket socket;

    public InstanceSender(ConnectableInstance instance) throws SocketException {
        this.ownerInstance = instance;
        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        while (true) {
            String message =
                String.format(
                    "%s %s",
                    ownerInstance.getInstanceInetAddress(),
                    ownerInstance.getPid()
                );

            byte[] buf = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet =
                new DatagramPacket(
                    buf,
                    buf.length,
                    ownerInstance.getGroupInetAddress(),
                    ownerInstance.getPort()
                );

            try {
                socket.send(packet);

                final long timeToSleep = 5000;
                Thread.sleep(timeToSleep);
            } catch (IOException ignored) {
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }

    @Override
    public void interrupt() {
        socket.close();
        super.interrupt();
    }
}
