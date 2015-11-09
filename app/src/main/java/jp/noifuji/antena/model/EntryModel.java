package jp.noifuji.antena.model;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jp.noifuji.antena.exception.AntenaSystemException;
import jp.noifuji.antena.loader.AsyncResult;
import jp.noifuji.antena.loader.RequestRawHtmlAsyncLoader;

/**
 * Created by ryoma on 2015/11/05.
 */
public class EntryModel implements LoaderManager.LoaderCallbacks<AsyncResult<Document>> {
    private static final String TAG = "EntryModel";
    private static final int LOADER_ID = 1;
    private EntryModelListener mListener;
    private Context mContext;
    private Loader mLoader;

    EntryModel() { }

    public void loadEntry(Context context, LoaderManager lm, String url) {
        if(url == null) {
            throw new NullPointerException();
        }
        this.mContext = context;
        Bundle data = new Bundle();
        data.putString("URL", url);
        mLoader = lm.restartLoader(LOADER_ID, data, this);
        mLoader.forceLoad();
    }

    public static Document removeTagFromHtml(Document doc, String tagName) {
        if(doc == null || tagName == null) {
            throw new AntenaSystemException();
        }
        Elements tags = doc.getElementsByTag(tagName);
        for (Element tag : tags) {
            tag.remove();
        }
        return doc;
    }

    @Override
    public Loader<AsyncResult<Document>> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        return new RequestRawHtmlAsyncLoader(mContext, bundle.getString("URL"));
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<Document>> loader, AsyncResult<Document> data) {
        Log.d(TAG, "onLoadFinished");
        Exception exception = data.getException();
        if (exception != null) {
            //Fragmentへのエラー通知を行う
            if (mListener != null) {
                mListener.onLoadEntryError(data.getErrorMessage());
            }
            return;
        }
        if (mListener != null) {
            Document htmlDoc = data.getData();
            htmlDoc = removeTagFromHtml(htmlDoc, "aside");
            htmlDoc = removeTagFromHtml(htmlDoc, "script");
            htmlDoc = removeTagFromHtml(htmlDoc, "nav");
            mListener.onEntryLoaded(htmlDoc.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<Document>> loader) {

    }

    /**
     * このクラスから通知を受け取るクラスを登録します。
     *
     * @param listener リスナとして登録するクラスのインスタンス
     */
    public void addListener(EntryModelListener listener) {
        this.mListener = listener;
    }

    /**
     * このクラスに登録したリスナを削除します。
     *
     * @param listener 削除するリスナ
     */
    public void removeListener(EntryModelListener listener) {
        if (this.mListener == listener) {
            this.mListener = null;
        }
    }

    /**
     * このクラスの通知を受け取るクラスはこのインターフェースを実装する。
     */
    public interface EntryModelListener {
        /**
         * エラー時
         *
         * @param errorMessage
         */
        void onLoadEntryError(String errorMessage);

        /**
         * 正常時
         *
         * @param html
         */
        void onEntryLoaded(String html);
    }
}
