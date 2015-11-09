package jp.noifuji.antena.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import jp.noifuji.antena.constants.ErrorMessage;

/**
 * Created by Ryoma on 2015/10/24.
 */
public class RequestRawHtmlAsyncLoader extends AsyncTaskLoader<AsyncResult<Document>> {


    private static final String TAG = "RequestRawHtml";

    private String mUrl;

    public RequestRawHtmlAsyncLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    public AsyncResult<Document> loadInBackground() {
        AsyncResult<Document> result = new AsyncResult<>();

        try {
            Log.e(TAG, "url is " + this.mUrl);
            Document doc = Jsoup.connect(this.mUrl).get();
            result.setData(doc);
        } catch (IOException e) {
            e.printStackTrace();
            result.setException(e, ErrorMessage.E001);
        }

        /*URL url = null;
        try {

            url = new URL(this.mUrl);

            HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.setInstanceFollowRedirects(false);
            urlconn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");

            urlconn.connect();

            Map headers = urlconn.getHeaderFields();
            Iterator it = headers.keySet().iterator();
            Log.d(TAG, "レスポンスヘッダ:");
            while (it.hasNext()) {
                String key = (String) it.next();
                Log.d(TAG, "  " + key + ": " + headers.get(key));
            }

            Log.d(TAG, "レスポンスコード[" + urlconn.getResponseCode() + "] " +
                    "レスポンスメッセージ[" + urlconn.getResponseMessage() + "]");
            Log.d(TAG, "---- ボディ ----");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(urlconn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                sb.append(line);
                //sb.append(System.getProperty("line.separator"));
                if (line == null) {
                    break;
                }
            }
            result.setData(sb.toString());

            reader.close();
            urlconn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return result;
    }
}
