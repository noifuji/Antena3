package jp.noifuji.antena.activity;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.json.JSONArray;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jp.noifuji.antena.loader.AsyncResult;
import jp.noifuji.antena.loader.RequestRawHtmlAsyncLoader;

/**
 * Created by ryoma on 2015/11/04.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String TAG = "MainActivityTest";
    private MainActivity mActivity;
    CountDownLatch mLatch;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity = getActivity();
        mLatch = new CountDownLatch(1);
    }

    @Test
    public void testAsyncTaskLoader_GetItem() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run");
                mActivity.getLoaderManager()
                        .initLoader(0, null,
                                new LoaderManager.LoaderCallbacks<AsyncResult<JSONArray>>() {
                                    @Override
                                    public Loader<AsyncResult<JSONArray>> onCreateLoader(int id, Bundle args) {
                                        Log.d(TAG, "onCreateLoader");
                                        RequestRawHtmlAsyncLoader loader = new RequestRawHtmlAsyncLoader(mActivity, "http://himasoku.com/archives/51926959.html");
                                        loader.forceLoad();
                                        return loader;
                                    }

                                    @Override
                                    public void onLoadFinished(Loader<AsyncResult<JSONArray>> loader,
                                                               AsyncResult<JSONArray> data) {
                                        Log.d(TAG, "onLoadFinished");
                                        mLatch.countDown();
                                    }

                                    @Override
                                    public void onLoaderReset(Loader<AsyncResult<JSONArray>> loader) {

                                    }
                                });
            }
        });
        //UIスレッドが終了するまで待つ
        boolean res = false;
        Log.d(TAG, "await");
        try {
            res = mLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "assert true");
        assertTrue(res);
    }

}