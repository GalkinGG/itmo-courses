package info.kgeorgiy.ja.galkin.hello;

import info.kgeorgiy.java.advanced.hello.*;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class HelloUDPClient implements HelloClient {

    /**
     * Usage: host | prefix | port | number of threads | number of requests.
     *
     * @param args parameters.
     */
    public static void main(String[] args) {
        String host;
        int port;
        String prefix;
        int numberOfThreads;
        int numberOfRequests;

        try {
            host = args[0];
            port = Integer.parseInt(Objects.requireNonNull(args[1]));
            prefix = args[2];
            numberOfThreads = Integer.parseInt(Objects.requireNonNull(args[3]));
            numberOfRequests = Integer.parseInt(Objects.requireNonNull(args[4]));

            new HelloUDPClient().run(host, port, prefix, numberOfThreads, numberOfRequests);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            System.err.println("Usage: <host> <prefix> <port> <number of threads> <number of requests>. " +
                    "Arguments must be non-null.");
        } catch (RuntimeException e) {
            System.err.println("Usage: <host> <prefix> <port> <number of threads> <number of requests>");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        SocketAddress socketAddress = new InetSocketAddress(host, port);

        ExecutorService workers = Executors.newFixedThreadPool(threads);

        for (int i = 1; i <= threads; i++) {
            final int threadId = i;

            workers.submit(new Thread(() -> {
                try (DatagramSocket datagramSocket = new DatagramSocket()) {

                    byte[] dataResponse = new byte[datagramSocket.getReceiveBufferSize()];
                    DatagramPacket response = new DatagramPacket(dataResponse, datagramSocket.getReceiveBufferSize());

                    for (int requestId = 1; requestId <= requests; requestId++) {

                        String requestText = String.format("%s%d_%d", prefix, threadId, requestId);
                        byte[] dataRequest = requestText.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket request = new DatagramPacket(dataRequest, requestText.length(), socketAddress);

                        datagramSocket.setSoTimeout(200);
                        while (true) {
                            try {
                                datagramSocket.send(request);
                                datagramSocket.receive(response);
                            } catch (SocketTimeoutException e) {
                                System.err.println("Timeout: " + e.getMessage());
                            } catch (PortUnreachableException e) {
                                System.err.println("Connection destination is unavailable: " + e.getMessage());
                            } catch (IOException e) {
                                System.err.println("IO exception during sending: " + e.getMessage());
                            }

                            String result = new String(response.getData(), response.getOffset(),
                                    response.getLength(), StandardCharsets.UTF_8);

                            if (result.contains(requestText)) {
                                System.out.println(result);
                                break;
                            }
                        }
                    }
                } catch (SocketException e) {
                    System.err.println("The socket could not be opened: " + e.getMessage());
                }
            }));
        }
        workers.close();
    }
}
