package instance;

import java.net.InetAddress;

public class ConnectableInstance {

    private final int port = 10000;

    private final long pid;

    private final String instanceInetAddress;

    private final InetAddress groupInetAddress;

    private final InstanceSender sender;

    private final InstanceReceiver receiver;

    public ConnectableInstance(String groupInetAddress) throws Exception {
        this.groupInetAddress = InetAddress.getByName(groupInetAddress);
        this.pid = ProcessHandle.current().pid();
        this.instanceInetAddress = InetAddress.getLoopbackAddress().getHostAddress();

        this.sender = new InstanceSender(this);
        this.receiver = new InstanceReceiver(this);

        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                this.sender.interrupt();
                this.receiver.interrupt();
            })
        );

        this.sender.start();
        this.receiver.start();
    }

    public int getPort() {
        return port;
    }

    public long getPid() {
        return pid;
    }

    public String getInstanceInetAddress() {
        return instanceInetAddress;
    }

    public InetAddress getGroupInetAddress() {
        return groupInetAddress;
    }

}
