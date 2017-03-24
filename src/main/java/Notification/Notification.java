package Notification;

import Notification.database.SelectHashesChrome;

import java.io.InputStream;
import java.net.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Notification
{
    public static final boolean ENVIRONMENT_DEV = true; //Если true, то dev
    public final static Logger logger = java.util.logging.Logger.getLogger("ch11_main_tools.LoggingApi");

    static { //Реализация препроцессора
        final int COUNT_CORE_FROM_DEVELOPERS_MACHINE = 8; //Если когда-то компы станут мощнее, сменить

        if (Runtime.getRuntime().availableProcessors() > COUNT_CORE_FROM_DEVELOPERS_MACHINE && Notification.ENVIRONMENT_DEV)
            logger.warning("Ты уверен, что это запущенно не на проде?");
        else if (Runtime.getRuntime().availableProcessors() <= COUNT_CORE_FROM_DEVELOPERS_MACHINE && !Notification.ENVIRONMENT_DEV)
            logger.warning("Ты уверен, что незабыл включить Notification.ENVIRONMENT_DEV?");
    }

    static void checkMemory() {
        System.out.println("Used Memory   :  " + (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (double)(1024 * 1024) + " MB");
    }

    public static void main(String[] args) throws MalformedURLException, UnknownHostException, InterruptedException {

        SelectHashesChrome selectHashesChrome = new SelectHashesChrome();
        checkMemory();

        Set<Map<String, String>> set = selectHashesChrome.getQuery();
        //System.gc(); //TODO

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        checkMemory();

        NumberFormat formatter = new DecimalFormat("#0.00000");

        //InetAddress address = InetAddress.getByName(new URL("http://arxemond.ru/").getHost());

        final long startTime = System.currentTimeMillis();

        for (int i = 0; i < 130; i++) {
            Future<Response> response = executor.submit(() -> {
                URL url = null;
                Response response1 = null;
                try {
                    //url = new URL("https://gcm-http.googleapis.com/gcm/send");
                    //String uri = "http://" + address.getHostAddress() + "/";
                    //System.out.println(uri);
                    String uri = "http://example.com/";
                    url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //conn.setRequestMethod("POST");
                    //conn.setConnectTimeout(5000);

                    //conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    //conn.setRequestProperty("Authorization", "key=AAAAmyvHmSI:APA91bHBflVSnWMZxDAH_xtIE_447b3-zRRwKf3deK6SwLkUXKLrfciDrOLjoG9MT8rQsiAezzBgTdHI_fBYXAkHLlhCXUz8nUQYzb4SLIjqA5Dp7v4J2UhXC1LrBHg2-DmEOGrrwz4g279NPUhl1-2o7IQkMhAfIg");
                    /**
                     * body        = {'registration_ids': googleHashesArray},
                     * dataType    = 'json',
                     */
                    response1 = new Response(url.openStream(), Thread.currentThread().getName());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } finally {
                    return response1;
                }
            });

            try {
                InputStream body = response.get().getBody();
                System.out.println(body + " | " + i + " | " + response.get().getThread());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        System.out.println("Total execution time: " + formatter.format((System.currentTimeMillis() - startTime) / 1000d) + " Sec");

        //for (Map<String, String> count: set)
        //    for (Map.Entry<String, String> entry: count.entrySet())
        //        System.out.println(entry.getKey() + " = " + entry.getValue());


        executor.shutdown();

        checkMemory();
    }
}

class Response {
    private InputStream body;
    private String thread;

    public Response(InputStream body, String thread) {
        this.body = body;
        this.thread = thread;
    }

    public InputStream getBody() {
        return body;
    }

    public String getThread() {
        return thread;
    }
}
