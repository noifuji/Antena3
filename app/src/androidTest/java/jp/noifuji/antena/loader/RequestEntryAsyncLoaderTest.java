package jp.noifuji.antena.loader;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jp.noifuji.antena.activity.MainActivity;

/**
 * Created by ryoma on 2015/11/05.
 */
public class RequestEntryAsyncLoaderTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private static final String TAG = "RequestEntryALTest";
    private MainActivity mActivity;
    private CountDownLatch mLatch;

    public RequestEntryAsyncLoaderTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity = getActivity();
        mLatch = new CountDownLatch(1);
    }

    @Test
    public void testRequestEntryAsyncLoader() throws Exception {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run");
                mActivity.getLoaderManager()
                        .initLoader(0, null,
                                new LoaderManager.LoaderCallbacks<AsyncResult<String>>() {
                                    @Override
                                    public Loader<AsyncResult<String>> onCreateLoader(int id, Bundle args) {
                                        Log.d(TAG, "onCreateLoader");
                                        RequestEntryAsyncLoader loader = new RequestEntryAsyncLoader(mActivity, "0");
                                        loader.forceLoad();
                                        return loader;
                                    }

                                    @Override
                                    public void onLoadFinished(Loader<AsyncResult<String>> loader,
                                                               AsyncResult<String> data) {
                                        Log.d(TAG, "onLoadFinished");
                                        assertEquals("エラーは生じていません。", data.getException(), null);
                                        mLatch.countDown();
                                    }

                                    @Override
                                    public void onLoaderReset(Loader<AsyncResult<String>> loader) {

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