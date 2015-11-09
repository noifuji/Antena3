package jp.noifuji.antena.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

import jp.noifuji.antena.R;
import jp.noifuji.antena.activity.WebViewActivity;
import jp.noifuji.antena.entity.HtmlHistory;
import jp.noifuji.antena.model.EntryModel;
import jp.noifuji.antena.model.ModelFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WebViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WebViewFragment extends Fragment implements EntryModel.EntryModelListener, WebViewActivity.BackKeyListener{
    private static final String TAG = "WebViewFragment";
    private WebView webView;
    private View mProgressBar;
    private OnFragmentInteractionListener mListener;
    private EntryModel mEntryModel;
    private ArrayList<HtmlHistory> mHtmlPageStack;
    private boolean isGoBack = false;

    public WebViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEntryModel = ModelFactory.getInstance().getmEntryModel();
        mEntryModel.addListener(this);
        mHtmlPageStack = new ArrayList<>();
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
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                if (isGoBack) {
                    isGoBack = false;
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Load position X:" + mHtmlPageStack.get(mHtmlPageStack.size() - 2).getmScrollX() + ", Y:" + mHtmlPageStack.get(mHtmlPageStack.size() - 2).getmScrollY());
                            webView.setScrollX(0);
                            webView.setScrollY(mHtmlPageStack.get(mHtmlPageStack.size() - 2).getmScrollY());
                            Log.d(TAG, "present position X:" + webView.getScrollX() + ", Y:" + webView.getScrollY());
                            mHtmlPageStack.remove(mHtmlPageStack.size() - 1);
                        }
                        // Delay the scrollTo to make it work
                    }, 100);
                }

            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "Clicked URL =  " + url);
                Log.d(TAG, "Save position X:" + webView.getScrollX() + ", Y:" + webView.getScrollY());
                mHtmlPageStack.get(mHtmlPageStack.size() - 1).setmScrollX(webView.getScrollX());
                mHtmlPageStack.get(mHtmlPageStack.size() - 1).setmScrollY(webView.getScrollY());
                mEntryModel.loadEntry(WebViewFragment.this.getActivity(), getLoaderManager(), url);
                mProgressBar.setVisibility(View.VISIBLE);
                return true;
            }
        });


        Intent intent = this.getActivity().getIntent();
        mEntryModel.loadEntry(this.getActivity(), getLoaderManager(), intent.getStringExtra("URI"));
        mProgressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach()");
        //Activityに渡していたWevViewへの参照を消しておく。
        mListener = null;
        mEntryModel.removeListener(this);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onLoadEntryError(String errorMessage) {
        mProgressBar.setVisibility(View.GONE);
//        mListener.onShowTextMessage(errorMessage);
    }

    @Override
    public void onEntryLoaded(String html) {
        Log.d(TAG, "html length:" + html.length());
        mHtmlPageStack.add(new HtmlHistory(html));
        //webView.loadData(html, "text/html; charset=utf-8", "UTF-8");  buggyらしい
        webView.loadDataWithBaseURL(null, html, "text/html; charset=utf-8", "UTF-8", null);//webviewのhistoryにためない
    }

    @Override
    public void onBackPressed(Activity activity) {
        Log.d(TAG, "onBackPressed mHtmlPageStack = " + mHtmlPageStack.size());
        if(mHtmlPageStack.size() > 1) {
            this.isGoBack = true;
            //前ページをロードする
            webView.loadDataWithBaseURL(null, mHtmlPageStack.get(mHtmlPageStack.size() - 2).getmHtml(), "text/html; charset=utf-8", "UTF-8", null);
        } else {
            //ヒストリがなくなったら、リスト画面に戻る
            mHtmlPageStack = null;
            activity.finish();
        }
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
        void onShowTextMessage(String message);
    }

    //onPageLoad html保存    urlクリック 場所保存 ページ遷移 バック スタック参照 htmlロード 場所ロード

}
