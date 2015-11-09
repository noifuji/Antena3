package jp.noifuji.antena.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import jp.noifuji.antena.R;
import jp.noifuji.antena.entity.HeadLine;
import jp.noifuji.antena.model.HeadLineListModel;
import jp.noifuji.antena.model.ModelFactory;
import jp.noifuji.antena.view.EntryAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EntryListFragment extends Fragment implements HeadLineListModel.HeadLineListModelListener {
    private static final String TAG = "EntryListFragment";
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mUpButton;
    private HeadLineListModel mHeadLineListModel;
    private OnFragmentInteractionListener mListener;

    public EntryListFragment() {
        // Required empty public constructor
        //
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeadLineListModel = ModelFactory.getInstance().getmHeadLineListModel(this.getActivity().getApplication());
        Log.d(TAG, "EntryList has " + mHeadLineListModel.getHeadLineList().size() + " entries.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment   kl
        View view = inflater.inflate(R.layout.fragment_entry_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1,
                R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);

        mListView = (ListView) view.findViewById(R.id.entry_list);

        mUpButton = (FloatingActionButton) view.findViewById(R.id.up_button);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.smoothScrollToPosition(0);
            }
        });

        if (mHeadLineListModel.getHeadLineList().size() == 0) {
            //ストレージに何も保存されていなければアップデートする
            mHeadLineListModel.update(this.getActivity(), getLoaderManager());
        } else {
            //前回の内容をそのまま表示
            EntryAdapter adapter = new EntryAdapter(this.getActivity(), R.layout.list_item, mHeadLineListModel.getHeadLineList());
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
                Log.d(TAG, "onRefresh");
                mHeadLineListModel.update(EntryListFragment.this.getActivity(), getLoaderManager());
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HeadLine headLine = (HeadLine) parent.getAdapter().getItem(position);
                headLine.setIsRead(true);
                parent.getAdapter().getView(position, view, parent);
                //WebViewを開く
                mListener.onStartWebView(headLine.getmUrl());
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HeadLine headLine = (HeadLine) parent.getAdapter().getItem(position);

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
    public void onStart() {
        super.onStart();
        mHeadLineListModel.addListener(this);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "entered onDetach()");
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "entered onStop()");
        mHeadLineListModel.removeListener(this);
        mHeadLineListModel.saveHeadLineList(this.getActivity());
        super.onStop();
    }

    @Override
    public void onHeadLineListUpdateError(String errorMessage) {
        mListener.onShowTextMessage(errorMessage);
    }

    @Override
    public void onHeadLineListUpdated(List<HeadLine> headlineList, int updatedCount) {
        if (updatedCount > 0) {
            EntryAdapter adapter = new EntryAdapter(this.getActivity(), R.layout.list_item, headlineList);
            mListView.setAdapter(adapter);
        }

        //更新ダイアログを停止する。
        mSwipeRefreshLayout.setRefreshing(false);

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


    //未実装
/*    private void updateEntryList(EntryList list) {
        Date lastUpdate = Utils.getDayInMonth(new Date(Long.valueOf(list.getLatestEntry().getmPublicationDate())));
        Date now = Utils.getDayInMonth(Utils.getNowDate());

        //エントリーリストが空であれば、最新エントリーを○○件取得する
        //エントリーリストが空でないが、最終アップデートが過去の日付である場合、リストを空にして○○件取得する
        //エントリーリストが空でなく、最終アップデートが同じ日付である場合、最終アップデート以降を取得する
        if (list.size() == 0) {

        }
    }*/

}
