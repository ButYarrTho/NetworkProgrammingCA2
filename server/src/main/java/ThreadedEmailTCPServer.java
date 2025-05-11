import networking.INetworkLayer;
import networking.TCPNetworkLayer;
import protocols.EmailUtilities;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
public class ThreadedEmailTCPServer {
    private static final int CORE_POOL = 4;
    private static final int MAX_POOL = 10;
    private static final long KEEP_ALIVE = 30L;
    private static final int QUEUE_CAP = 50;

    public static void main(String[] args) {
        boolean serverSessionValid = true;
        try (ExecutorService pool = new ThreadPoolExecutor(
                CORE_POOL, MAX_POOL, KEEP_ALIVE, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAP),
                new ThreadPoolExecutor.CallerRunsPolicy()
        )) {
            Storage store = new Storage();
            store.seed();
            try (ServerSocket server = new ServerSocket(EmailUtilities.PORT)) {
                log.info("Email server on port {}", EmailUtilities.PORT);
                while (serverSessionValid) {
                    Socket client = server.accept();
                    INetworkLayer net = new TCPNetworkLayer(client);
                    pool.submit(new ClientHandler(net, store));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.shutdown();
            }
        } catch (Exception e) {
            log.error("Error occurred when running server.");
        }
    }
}