package jp.noifuji.antena.activity;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import jp.noifuji.antena.R;
import jp.noifuji.antena.fragment.WebViewFragment;

public class WebViewActivity extends AppCompatActivity implements WebViewFragment.OnFragmentInteractionListener {
    private static final String TAG = "WebViewActivity";
    private WebViewFragment mWebViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        // ツールバーをアクションバーとしてセット
        Toolbar toolbar = (Toolbar) findViewById(R.id.web_view_tool_bar);
        setSupportActionBar(toolbar);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            WebViewFragment fragment = new WebViewFragment();
            transaction.replace(R.id.web_view_fragment, fragment);
            transaction.commit();
            mWebViewFragment = fragment;


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onShowTextMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebViewFragment != null) {
                Log.d(TAG, "onBackPressed");
                mWebViewFragment.onBackPressed(this);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
