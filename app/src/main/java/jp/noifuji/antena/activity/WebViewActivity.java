package jp.noifuji.antena.activity;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Toast;

import jp.noifuji.antena.R;
import jp.noifuji.antena.fragment.WebViewFragment;

public class WebViewActivity extends AppCompatActivity implements WebViewFragment.OnFragmentInteractionListener {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            WebViewFragment fragment = new WebViewFragment();
            transaction.replace(R.id.web_view_fragment, fragment);
            transaction.commit();
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void registerWebView(WebView webview) {
        mWebView = webview;
    }

    @Override
    public void onShowTextMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mWebView != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
