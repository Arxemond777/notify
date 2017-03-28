package Notification;

import Notification.database.SelectHashesChrome;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
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

    public static void main(String[] args) throws IOException, InterruptedException {

        SelectHashesChrome selectHashesChrome = new SelectHashesChrome();
        //checkMemory();

        Set<Map<String, String>> set = selectHashesChrome.getQuery();
        System.out.println(set);
        System.exit(100500);
        //System.gc(); //TODO

        //ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ExecutorService executor = Executors.newCachedThreadPool(); //Для большого кол-во коротких ассинк запросов
        //checkMemory();

        NumberFormat formatter = new DecimalFormat("#0.00000");

        //InetAddress address = InetAddress.getByName(new URL("http://arxemond.ru/").getHost());

        /*FileInputStream serviceAccount = new FileInputStream("/home/arxemond777/Downloads/Notify-RG-4ea6ee0983d2.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://notify-rg.firebaseio.com/")
                .build();
        FirebaseApp defaultApp = FirebaseApp.initializeApp(options);
        FirebaseAuth defaultAuth = FirebaseAuth.getInstance(defaultApp);
        FirebaseDatabase defaultDatabase = FirebaseDatabase.getInstance(defaultApp);

        System.out.println(defaultApp);
        System.out.println(defaultAuth);
        System.out.println(defaultDatabase);*/

        InetAddress address = InetAddress.getByName(new URL("https://gcm-http.googleapis.com/gcm/send").getHost());
        //final URL url = new URL("https://" + address.getHostAddress() + "/gcm/send");
        final URL url = new URL("https://gcm-http.googleapis.com/gcm/send");

        /***/
        checkMemory();
        final long startTime = System.currentTimeMillis();
        /***/

        for (int i = 0; i < 130; i++) {
            final int finalI = i;
            executor.submit(() -> {
                OutputStream os = null;
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization", "key=AAAAmyvHmSI:APA91bHBflVSnWMZxDAH_xtIE_447b3-zRRwKf3deK6SwLkUXKLrfciDrOLjoG9MT8rQsiAezzBgTdHI_fBYXAkHLlhCXUz8nUQYzb4SLIjqA5Dp7v4J2UhXC1LrBHg2-DmEOGrrwz4g279NPUhl1-2o7IQkMhAfIg");

                    String input = "{\"registration_ids\" : [\"fyDpGBfsxDU:APA91bFjdRhGebAHGS9p_mZdc54cKwOnkntwNGDFqe3xFJz0Ui8qAEU8MnIoPsaV7QCbkX5CcUY_756BvY156BxwC-JhjQvmgT1cV8cAc8FyQObXgoEG5DIZ5DYWcmdCK5j57iviHlIh\", \"1f9jPNMS0rho:APA91bH_2xBbd-TOFrpLC2snziEuJrCwDdmZ-ZJ_mu1q9fndkww8wJ48f2JSJxDNu-q6J_X87PO8m29Rlo9obBSWY-1mh8PPWUU9UmDKapGtQswu3jZjJwty5jdeYcF1dpQwD6r9BzLa1\"],\"data\" : {\"message\": \"hai  welcome\"}}";

                    os = conn.getOutputStream();
                    os.write(input.getBytes());
                    os.flush();


                    checkMemory();
                    System.out.println(/*conn.getResponseCode() + "-->>" + conn.getResponseMessage() +*/ " | Num: " + finalI);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (os != null)
                            os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        /***/
        System.out.println("Total execution time: " + formatter.format((System.currentTimeMillis() - startTime) / 1000d) + " Sec ");
        checkMemory();
        /***/

        executor.shutdown();


        /*for (int i = 0; i < 130; i++) {
            Future<Response> response = executor.submit(() -> {

                Response response1 = null;
                try {
                    //url = new URL("https://gcm-http.googleapis.com/gcm/send");
                    //String uri = "http://" + address.getHostAddress() + "/";
                    //System.out.println(uri);
                    String uri = "http://example.com/";
                    url = new URL(uri);
                    //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //conn.setRequestMethod("POST");
                    //conn.setConnectTimeout(5000);

                    //conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    //conn.setRequestProperty("Authorization", "key=AAAAmyvHmSI:APA91bHBflVSnWMZxDAH_xtIE_447b3-zRRwKf3deK6SwLkUXKLrfciDrOLjoG9MT8rQsiAezzBgTdHI_fBYXAkHLlhCXUz8nUQYzb4SLIjqA5Dp7v4J2UhXC1LrBHg2-DmEOGrrwz4g279NPUhl1-2o7IQkMhAfIg");
                    *//**
                     * body        = {'registration_ids': googleHashesArray},
                     * dataType    = 'json',
                     *//*
                    response1 = new Response(url.openStream(), Thread.currentThread().getName());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } finally {
                    return response1;
                }
            });

            try {
                InputStream body = response.get().getBody();
                //System.out.println(body + " | " + i + " | " + response.get().getThread());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }*/

        //System.out.println("Total execution time: " + formatter.format((System.currentTimeMillis() - startTime) / 1000d) + " Sec");

        //for (Map<String, String> count: set)
        //    for (Map.Entry<String, String> entry: count.entrySet())
        //        System.out.println(entry.getKey() + " = " + entry.getValue());

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
