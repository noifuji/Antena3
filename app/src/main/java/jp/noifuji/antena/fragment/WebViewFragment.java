package jp.noifuji.antena.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jp.noifuji.antena.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WebViewFragment extends Fragment {
    private static final String TAG = "WebViewFragment";
    private WebView webView;
    private View mProgressBar;
    private OnFragmentInteractionListener mListener;

    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);

        mProgressBar = view.findViewById(R.id.progress_view);

        //レイアウトで指定したWebViewのIDを指定する。
        webView = (WebView) view.findViewById(R.id.webView);

        //リンクをタップしたときに標準ブラウザを起動させない
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        Intent intent = this.getActivity().getIntent();
        if (intent.hasExtra("URI")) {
            String uri = intent.getStringExtra("URI");
            webView.loadUrl(uri);
        } else if (intent.hasExtra("HTML")) {
            String html = intent.getStringExtra("HTML");
            webView.loadData(html, "text/html; charset=utf-8", "UTF-8");
        }

        mListener.registerWebView(webView);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        //Activityに渡していたWevViewへの参照を消しておく。
        mListener.registerWebView(null);
        mListener = null;
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void registerWebView(WebView webview);
    }

}
