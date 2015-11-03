package jp.noifuji.antena.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Ryoma on 2015/10/24.
 */
public class RequestRawHtmlAsyncLoader extends AsyncTaskLoader<AsyncResult<JSONArray>> {


    private static final String TAG = "RequestRawHtml";

    private static String mUrl;

    public RequestRawHtmlAsyncLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    public AsyncResult<JSONArray> loadInBackground() {
        AsyncResult<JSONArray> result = new AsyncResult<JSONArray>();

        URL url = null;
        try {
            url = new URL(this.mUrl);

            HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.setInstanceFollowRedirects(false);
            urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");

            urlconn.connect();

            Map headers = urlconn.getHeaderFields();
            Iterator it = headers.keySet().iterator();
            System.out.println("レスポンスヘッダ:");
            while (it.hasNext()) {
                String key = (String) it.next();
                System.out.println("  " + key + ": " + headers.get(key));
            }

            System.out.println("レスポンスコード[" + urlconn.getResponseCode() + "] " +
                    "レスポンスメッセージ[" + urlconn.getResponseMessage() + "]");
            System.out.println("\n---- ボディ ----");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(urlconn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
            //result.setData();

            reader.close();
            urlconn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
