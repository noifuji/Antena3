package jp.noifuji.antena.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import jp.noifuji.antena.constants.ErrorMessage;

/**
 * Created by Ryoma on 2015/10/24.
 */
public class RequestNewHeadLineAsyncLoader extends AsyncTaskLoader<AsyncResult<String>> {


    private static final String TAG = "RequestHeadLine";

    private String mLatestPublicationDate;
    private String mCategory;

    public RequestNewHeadLineAsyncLoader(Context context, String latestPublicationDate) {
        super(context);
        mLatestPublicationDate = latestPublicationDate;
        mCategory = "";
    }

    public RequestNewHeadLineAsyncLoader(Context context, String latestPublicationDate, String category) {
        super(context);
        mLatestPublicationDate = latestPublicationDate;
        mCategory = category;
    }

    @Override
    public AsyncResult<String> loadInBackground() {
        AsyncResult<String> result = new AsyncResult<String>();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("https://antena-noifuji.c9.io/entry" + "?time=" + mLatestPublicationDate + "&category=" + mCategory);
        //HttpGet httpGet = new HttpGet("http://183.181.0.117:8080/entry" + "?time=" + mLatestPublicationDate + "&category=" + mCategory);
        HttpResponse httpResponse = null;
        String str = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            StatusLine statusline = httpResponse.getStatusLine();
            Log.d(TAG, "StatusCode" + statusline.getStatusCode());
            str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            result.setException(e, ErrorMessage.E001);
            return result;
        }

/*        JSONArray jsonEntries = null;
        try {
        JSONObject jsonResponse = new JSONObject(str);

            jsonEntries = jsonResponse.getJSONArray("entries");
            Log.d(TAG, jsonEntries.length() + " entries received");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        result.setData(jsonEntries);
        */

        result.setData(str);

        return result;
    }
}
