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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.noifuji.antena.constants.Category;
import jp.noifuji.antena.entity.HeadLine;
import jp.noifuji.antena.loader.AsyncResult;
import jp.noifuji.antena.loader.RequestNewHeadLineAsyncLoader;
import jp.noifuji.antena.util.Utils;

/**
 * Created by ryoma on 2015/11/04.
 */
public class HeadLineListModel implements LoaderManager.LoaderCallbacks<AsyncResult<String>> {
    private static final String TAG = "HeadLineListModel";
    private static final String SAVE_FILE_NAME = "history3.dat";
    private static final int LOADER_ID = 0;
    private static final int ENTRY_LIST_LIMIT = 100;
    private Loader mLoader;
    private Context mContext;
    private HeadLineListModelListener mListener;

    private Map<String, List<HeadLine>> mCategoryHeadLineListMap;

    /**
     * コンストラクタ<br>
     *     ローカルに保存されているファイルがある場合はロードする。<br>
     *         なければ新規にオブジェクトを作成する。
     * @param context コンテキスト
     */
    public HeadLineListModel(Context context) {
        mCategoryHeadLineListMap = (Map<String, List<HeadLine>>) Utils.deserialize(Utils.getSDCardDirectory(context), SAVE_FILE_NAME);

        if (mCategoryHeadLineListMap == null) {
            mCategoryHeadLineListMap = new HashMap();
            mCategoryHeadLineListMap.put(Category.ALL, new ArrayList<HeadLine>());
            mCategoryHeadLineListMap.put(Category.VIP, new ArrayList<HeadLine>());
            mCategoryHeadLineListMap.put(Category.SPORTS, new ArrayList<HeadLine>());
            mCategoryHeadLineListMap.put(Category.NEWS, new ArrayList<HeadLine>());
            mCategoryHeadLineListMap.put(Category.MONEY, new ArrayList<HeadLine>());
            mCategoryHeadLineListMap.put(Category.KIJO, new ArrayList<HeadLine>());
        }
    }

    /**
     * 記事のヘッドラインのリストを取得する。
     * @return
     */
    public List<HeadLine> getHeadLineList(String category){
        return mCategoryHeadLineListMap.get(category);
    }

    /**
     * 最新の記事のヘッドラインを取得する。<br>
     *     ※リストが日付順に並んでいるという前提をおいている。
     * @return
     */
    public HeadLine getLatestEntry(String category) {
        if(getHeadLineList(category).size() == 0) {
            return null;
        }
        HeadLine hl = getHeadLineList(category).get(getHeadLineList(category).size()-1);
        Log.d(TAG, "latest entry's title of " + category + "list :" + hl.getmTitle());
        return hl;
    }

    /**
     * ヘッドラインリストの情報をサーバーに問い合わせて更新する。
     * @param lm
     */
    public void pullNewHeadLine(Context context, LoaderManager lm){
        pullNewHeadLine(context, lm, Category.ALL);
    }

    public void pullNewHeadLine(Context context, LoaderManager lm, String category) {
        this.mContext = context;
        Bundle data = new Bundle();
        HeadLine hl = getLatestEntry(category);
        if(hl == null) {
            data.putString("latestPubDate", "0");
        } else {
            data.putString("latestPubDate", hl.getmPublicationDate());
        }
        data.putString("category", category);
        mLoader = lm.restartLoader(LOADER_ID, data, this);
        mLoader.forceLoad();
    }

    @Override
    public Loader<AsyncResult<String>> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        return new RequestNewHeadLineAsyncLoader(mContext, bundle.getString("latestPubDate"), bundle.getString("category"));
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<String>> loader, AsyncResult<String> data) {
        Exception exception = data.getException();
        if (data.getException() != null) {
            //Fragmentへのエラー通知を行う
            if(mListener != null) {
                mListener.onHeadLineListUpdateError(data.getErrorMessage());
            }
            return;
        }

        String rawJson = data.getData();
        JSONArray jsonEntries = null;
        String category = "";
        try {
            JSONObject jsonResponse = new JSONObject(rawJson);
            jsonEntries = jsonResponse.getJSONArray("entries");
            category = jsonResponse.getString("category");
            Log.d(TAG, jsonEntries.length() + " entries received");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //すでにリスト内に存在する記事情報のNEWフラグをfalseにしておく
        for(HeadLine h : getHeadLineList(category)) {
            h.setIsNew(false);
        }

        for (int i = jsonEntries.length() - 1; i >= 0; i--) {
            try {
                JSONObject jsonEntry = jsonEntries.getJSONObject(i);
                HeadLine headLine = new HeadLine(jsonEntry);
                Log.d(TAG, "title:" + headLine.getmTitle() + " , category:" + headLine.getmCategory());
                getHeadLineList(category).add(headLine);
                if (getHeadLineList(category).size() > ENTRY_LIST_LIMIT) {
                    getHeadLineList(category).remove(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(mListener != null) {
            mListener.onHeadLineListUpdated(getHeadLineList(category), jsonEntries.length());
        }
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
        Utils.serialize((Serializable) mCategoryHeadLineListMap, Utils.getSDCardDirectory(context), SAVE_FILE_NAME);
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
