package jp.noifuji.antena.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Ryoma on 2015/10/24.
 */
public class RequestRawHtmlAsyncLoader extends AsyncTaskLoader<AsyncResult<String>> {


    private static final String TAG = "RequestRawHtml";

    private static String mUrl;

    public RequestRawHtmlAsyncLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    public AsyncResult<String> loadInBackground() {
        AsyncResult<String> result = new AsyncResult<String>();

        try {
            Document doc = Jsoup.connect(this.mUrl).get();
            Elements asides = doc.getElementsByTag("aside");
            for(Element aside : asides) {
                aside.remove();
            }
            Elements scripts = doc.getElementsByTag("script");
            for(Element script : scripts) {
                script.remove();
            }
            Elements navs = doc.getElementsByTag("nav");
            for(Element nav : navs) {
                nav.remove();
            }
            result.setData(doc.toString());
        } catch (IOException e) {
            e.printStackTrace();
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
