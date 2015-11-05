package jp.noifuji.antena.model;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.noifuji.antena.entity.HeadLine;
import jp.noifuji.antena.loader.AsyncResult;
import jp.noifuji.antena.loader.RequestEntryAsyncLoader;
import jp.noifuji.antena.util.Utils;

/**
 * Created by ryoma on 2015/11/04.
 */
public class HeadLineListModel implements LoaderManager.LoaderCallbacks<AsyncResult<String>> {
    private static final String TAG = "HeadLineListModel";
    private static final String SAVE_FILE_NAME = "history2.dat";
    private static final int LOADER_ID = 0;
    private static final int ENTRY_LIST_LIMIT = 100;
    private Loader mLoader;
    private Context mContext;
    private HeadLineListModelListener mListener;

    private List<HeadLine> mHeadLineList;

    /**
     * コンストラクタ<br>
     *     ローカルに保存されているファイルがある場合はロードする。<br>
     *         なければ新規にオブジェクトを作成する。
     * @param context コンテキスト
     */
    public HeadLineListModel(Context context) {
        mHeadLineList = (List<HeadLine>) Utils.deserialize(Utils.getSDCardDirectory(context), SAVE_FILE_NAME);

        if (mHeadLineList == null) {
            mHeadLineList = new ArrayList<HeadLine>();
        }
    }

    /**
     * 記事のヘッドラインのリストを取得する。
     * @return
     */
    public List<HeadLine> getHeadLineList(){
        return mHeadLineList;
    }

    /**
     * 最新の記事のヘッドラインを取得する。<br>
     *     ※リストが日付順に並んでいるという前提をおいている。
     * @return
     */
    public HeadLine getLatestEntry() {
        if(mHeadLineList.size() == 0) {
            return null;
        }
        HeadLine hl = mHeadLineList.get(mHeadLineList.size()-1);
        Log.d(TAG, "latest entry's title:" + hl.getmTitle());
        return hl;
    }

    /**
     * ヘッドラインリストの情報をサーバーに問い合わせて更新する。
     * @param lm
     */
    public void update(Context context, LoaderManager lm){
        this.mContext = context;
        Bundle data = new Bundle();
        HeadLine hl = getLatestEntry();
        if(hl == null) {
            data.putString("latestPubDate", "0");
        } else {
            data.putString("latestPubDate", hl.getmPublicationDate());
        }
        mLoader = lm.restartLoader(LOADER_ID, data, this);
        mLoader.forceLoad();
    }

    @Override
    public Loader<AsyncResult<String>> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        return new RequestEntryAsyncLoader(mContext, bundle.getString("latestPubDate"));
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<String>> loader, AsyncResult<String> data) {
        Exception exception = data.getException();
        if (data.getException() != null) {
            //Fragmentへのエラー通知を行う
            mListener.onHeadLineListUpdateError(data.getErrorMessage());
            return;
        }

        String rawJson = data.getData();
        JSONArray jsonEntries = null;
        try {
            JSONObject jsonResponse = new JSONObject(rawJson);
            jsonEntries = jsonResponse.getJSONArray("entries");
            Log.d(TAG, jsonEntries.length() + " entries received");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = jsonEntries.length() - 1; i >= 0; i--) {
            try {
                JSONObject jsonEntry = jsonEntries.getJSONObject(i);
                HeadLine headLine = new HeadLine(jsonEntry);
                Log.d(TAG, "title:" + headLine.getmTitle());
                mHeadLineList.add(headLine);
                if (mHeadLineList.size() > ENTRY_LIST_LIMIT) {
                    mHeadLineList.remove(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mListener.onHeadLineListUpdated(mHeadLineList, jsonEntries.length());
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<String>> loader) {

    }

    /**
     * このクラスから通知を受け取るクラスを登録します。
     * @param listener リスナとして登録するクラスのインスタンス
     */
    public void addListener(HeadLineListModelListener listener) {
        this.mListener = listener;
    }

    /**
     * このクラスに登録したリスナを削除します。
     * @param listener 削除するリスナ
     */
    public void removeListener(HeadLineListModelListener listener) {
        if(this.mListener == listener) {
            this.mListener = null;
        }
    }

    public void saveHeadLineList(Context context) {
        Utils.serialize((Serializable) mHeadLineList, Utils.getSDCardDirectory(context), SAVE_FILE_NAME);
    }

    /**
     * このクラスの通知を受け取るクラスはこのインターフェースを実装する。
     */
    public interface HeadLineListModelListener {
        /**
         * 記事のヘッドラインの更新確認に失敗した場合に呼び出されます。
         * @param errorMessage
         */
        void onHeadLineListUpdateError(String errorMessage);

        /**
         * 記事のヘッドラインの更新確認が完了した場合に呼び出されます。
         * @param headlineList モデルが保持しているヘッドライン情報
         * @param updatedCount 更新された件数
         */
        void onHeadLineListUpdated(List<HeadLine> headlineList, int updatedCount);
    }
}
