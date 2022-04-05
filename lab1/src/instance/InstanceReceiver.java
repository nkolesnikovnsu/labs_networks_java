package instance;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class InstanceReceiver extends Thread {

    private final int timeout = 30;

    private final Map<String, Instant> knownInstancesAndCounters;

    private final MulticastSocket socket;

    public InstanceReceiver(ConnectableInstance instance) throws IOException {
        this.knownInstancesAndCounters = new HashMap<>();
        this.socket = new MulticastSocket(instance.getPort());
        socket.joinGroup(instance.getGroupInetAddress());
    }

    @Override
    public void run() {
        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
            } catch (IOException ignored) {
            }

            handle(packet);
        }
    }

    private void handle(DatagramPacket packet) {
        String data = new String(packet.getData(), 0, packet.getLength());
        if (!knownInstancesAndCounters.containsKey(data)) {
            printConnected(data);
        }
        knownInstancesAndCounters.put(data, Instant.now());

        Predicate<Map.Entry<String, Instant>> connectionTimeout =
            entry -> Duration.between(Instant.now(), entry.getValue()).getSeconds() > 30;

        Consumer<Map.Entry<String, Instant>> disconnect =
            entry -> {
                printDisconnected(entry.getKey());
                knownInstancesAndCounters.remove(entry.getKey());
            };

        knownInstancesAndCounters
            .entrySet()
            .stream()
            .filter(connectionTimeout)
            .forEach(disconnect);
    }

    private void printConnected(String data) {
        System.out.println(data + " " + "CONNECTED");
    }

    private void printDisconnected(String data) {
        System.out.println(data + " " + "DISCONNECTED");
    }

    @Override
    public void interrupt() {
        socket.close();
        super.interrupt();
    }
}
