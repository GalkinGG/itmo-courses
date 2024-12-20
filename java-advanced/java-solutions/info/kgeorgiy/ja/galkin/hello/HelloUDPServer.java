package info.kgeorgiy.ja.galkin.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPServer implements NewHelloServer {

    private ExecutorService workers;
    private ExecutorService receiver;
    private final Map<Integer, DatagramSocket> sockets = new HashMap<>();

    /**
     * Usage: port | number of threads.
     *
     * @param args parameters.
     */
    public static void main(String[] args) {
        int port;
        int numberOfThreads;

        try {
            port = Integer.parseInt(Objects.requireNonNull(args[0]));
            numberOfThreads = Integer.parseInt(Objects.requireNonNull(args[1]));
        } catch (NullPointerException e) {
            System.err.println("Usage: <port> <number of threads>. Arguments must be non-null.");
            return;
        }

        try (HelloServer server = new HelloUDPServer()) {
            server.start(port, numberOfThreads);
        } catch (RuntimeException e) {
            System.err.println("Usage: <port> <number of threads>.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(int threads, Map<Integer, String> ports) {

        ports.forEach((port, response) -> {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                sockets.put(port, socket);
            } catch (SocketException e) {
                System.err.println("Unable to create socket connected to this port " + port);
            }
        });

        // isEmpty
        if (sockets.size() < 1) {
            return;
        }

        receiver = Executors.newFixedThreadPool(sockets.size());
        workers = Executors.newFixedThreadPool(threads);

        ports.forEach((port, responseString) -> {
            DatagramSocket socket = sockets.get(port);
            receiver.submit(() -> {
                while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                    try {
                        byte[] data = new byte[socket.getReceiveBufferSize()];
                        DatagramPacket packet = new DatagramPacket(data, data.length);

                        socket.receive(packet);

                        workers.submit(() -> {
                            String responseText = new String(
                                    packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8
                            );
                            byte[] responseData = new byte[0];

                            DatagramPacket response = new DatagramPacket(
                                    responseData, 0, packet.getSocketAddress()
                            );

                            response.setData(
                                    (responseString
                                            .replaceAll("\\$", responseText)
                                            .getBytes(StandardCharsets.UTF_8))
                            );

                            try {
                                socket.send(response);
                            } catch (IOException e) {
                                System.err.println("IO exception during sending: " + e.getMessage());
                            }
                        });
                    } catch (IOException e) {
                        if (!socket.isClosed()) {
                            System.err.println("IO exception with datagram: " + e.getMessage());
                        }
                    }
                }
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        sockets.forEach((ports, socket) -> socket.close());
        if (receiver != null) {
            receiver.close();
        }
        if (workers != null) {
            workers.close();
        }
    }
}
