package jp.noifuji.antena.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import jp.noifuji.antena.R;
import jp.noifuji.antena.fragment.WebViewFragment;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    private BackKeyListener mBackKeyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            WebViewFragment fragment = new WebViewFragment();
            transaction.replace(R.id.web_view_fragment, fragment);
            transaction.commit();
            mBackKeyListener = fragment;
        }


    }

/*    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onShowTextMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mBackKeyListener != null) {
                Log.d(TAG, "onBackPressed");
                mBackKeyListener.onBackPressed(this);
            }
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    public interface BackKeyListener {
        public void onBackPressed(Activity activity);
    }
}
