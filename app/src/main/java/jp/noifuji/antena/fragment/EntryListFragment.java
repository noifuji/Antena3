package jp.noifuji.antena.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import jp.noifuji.antena.R;
import jp.noifuji.antena.entity.Entry;
import jp.noifuji.antena.entity.EntryList;
import jp.noifuji.antena.loader.AsyncResult;
import jp.noifuji.antena.loader.RequestEntryAsyncLoader;
import jp.noifuji.antena.util.Utils;
import jp.noifuji.antena.view.EntryAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EntryListFragment extends Fragment implements LoaderManager.LoaderCallbacks<AsyncResult<JSONArray>> {
    private static final String TAG = "EntryListFragment";
    private static final int ENTRY_LIST_LIMIT = 100;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mUpButton;
    private EntryList mEntryList;

    private OnFragmentInteractionListener mListener;

    public EntryListFragment() {
        // Required empty public constructor
        //
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEntryList = (EntryList) Utils.deserialize(Utils.getSDCardDirectory(this.getActivity()), "history.dat");
        if(mEntryList == null) {
            mEntryList = new EntryList();
        }
        Log.d(TAG, "EntryList has " + mEntryList.size() + " entries.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment   kl
        View view = inflater.inflate(R.layout.fragment_entry_list, container, false);


        //�X���C�v�ݒ�
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        // �F�ݒ�
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1,
                R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);

        //���X�g�r���[�ݒ�
        mListView = (ListView) view.findViewById(R.id.entry_list);

        //FAB�ݒ�
        mUpButton= (FloatingActionButton) view.findViewById(R.id.up_button);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.smoothScrollToPosition(0);
            }
        });

        if(mEntryList.size() == 0) {
            Bundle data = new Bundle();
            data.putString("latestPubDate", "0");
            getLoaderManager().restartLoader(0, data, EntryListFragment.this);
        } else {
            EntryAdapter adapter = new EntryAdapter(this.getActivity(), R.layout.list_item, mEntryList);
            mListView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Bundle data = new Bundle();
                if(mEntryList.size() != 0) {
                    data.putString("latestPubDate", mEntryList.getLatestEntry().getmPublicationDate());
                } else {
                    data.putString("latestPubDate", "0");
                }
                getLoaderManager().restartLoader(0, data, EntryListFragment.this);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry) parent.getAdapter().getItem(position);
                entry.setIsRead(true);
                parent.getAdapter().getView(position, view, parent);
                //WebViewを開く
                mListener.onStartWebView(entry.getmUrl());
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry) parent.getAdapter().getItem(position);

                Toast.makeText(EntryListFragment.this.getActivity(), "Long Pressed", Toast.LENGTH_SHORT).show();

                //別のViewへ通知をとおさない
                return true;
            }
        });
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
        Log.d(TAG, "entered onDetach()");
        mListener = null;
        Utils.serialize(mEntryList, Utils.getSDCardDirectory(this.getActivity()), "history.dat");
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
        void onStartWebView(String uri);
        void onShowTextMessage(String message);
    }

    @Override
    public Loader<AsyncResult<JSONArray>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        RequestEntryAsyncLoader requestEntryAsyncLoader = new RequestEntryAsyncLoader(this.getActivity(), args.getString("latestPubDate"));
        requestEntryAsyncLoader.forceLoad();
        return requestEntryAsyncLoader;
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<JSONArray>> loader, AsyncResult<JSONArray> data) {

        Exception exception = data.getException();
        if (data.getException() != null) {
            mListener.onShowTextMessage(data.getErrorMessage());
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        JSONArray jsonData = data.getData();

        for (int i = jsonData.length()-1; i >= 0; i--) {
            try {
                JSONObject jsonEntry = jsonData.getJSONObject(i);
                Entry entry = new Entry(jsonEntry);
                mEntryList.add(entry);
                if(mEntryList.size() > ENTRY_LIST_LIMIT) {
                    mEntryList.remove(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            EntryAdapter adapter = new EntryAdapter(this.getActivity(), R.layout.list_item, mEntryList);
            mListView.setAdapter(adapter);

        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<JSONArray>> loader) {

    }

    //未実装
    private void updateEntryList(EntryList list) {
        Date lastUpdate =  Utils.getDayInMonth(new Date(Long.valueOf(list.getLatestEntry().getmPublicationDate())));
        Date now = Utils.getDayInMonth(Utils.getNowDate());

        //エントリーリストが空であれば、最新エントリーを○○件取得する
        //エントリーリストが空でないが、最終アップデートが過去の日付である場合、リストを空にして○○件取得する
        //エントリーリストが空でなく、最終アップデートが同じ日付である場合、最終アップデート以降を取得する
        if(list.size() == 0) {

        }
    }

}
