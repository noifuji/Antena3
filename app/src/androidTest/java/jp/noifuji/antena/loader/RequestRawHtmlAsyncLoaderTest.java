package jp.noifuji.antena.loader;


import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jp.noifuji.antena.activity.MainActivity;

/**
 * Created by ryoma on 2015/11/04.
 */
public class RequestRawHtmlAsyncLoaderTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String TAG = "RequestRawHtmlALTest";
    private MainActivity mActivity;
    CountDownLatch mLatch;

    public RequestRawHtmlAsyncLoaderTest() {
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
                                new LoaderManager.LoaderCallbacks<AsyncResult<Document>>() {
                                    @Override
                                    public Loader<AsyncResult<Document>> onCreateLoader(int id, Bundle args) {
                                        Log.d(TAG, "onCreateLoader");
                                        RequestRawHtmlAsyncLoader loader = new RequestRawHtmlAsyncLoader(mActivity, "http://www.google.co.jp");
                                        loader.forceLoad();
                                        return loader;
                                    }

                                    @Override
                                    public void onLoadFinished(Loader<AsyncResult<Document>> loader,
                                                               AsyncResult<Document> data) {
                                        Log.d(TAG, "onLoadFinished");
                                        mLatch.countDown();
                                    }

                                    @Override
                                    public void onLoaderReset(Loader<AsyncResult<Document>> loader) {

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