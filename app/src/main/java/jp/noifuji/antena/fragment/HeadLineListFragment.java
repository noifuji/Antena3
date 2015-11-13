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

import butterknife.Bind;
import butterknife.ButterKnife;
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
public class HeadLineListFragment extends Fragment implements HeadLineListModel.HeadLineListModelListener {
    private static final String TAG = "HeadLineListFragment";
    private static final String CATEGORY = "category";
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mUpButton;
    private HeadLineListModel mHeadLineListModel;
    private OnFragmentInteractionListener mListener;
    private String mCategory;


    @Bind(R.id.headline_list_progress)
    View mProgressBar;

    public HeadLineListFragment() {
        // Required empty public constructor
        //
    }

    public static HeadLineListFragment newInstance(String category) {
        HeadLineListFragment fragment = new HeadLineListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(CATEGORY);
        }
        mHeadLineListModel = ModelFactory.getInstance().getmHeadLineListModel(this.getActivity().getApplication());
        Log.d(TAG, "NEW EntryList has " + mHeadLineListModel.getHeadLineList(mCategory).size() + " entries.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment   kl
        View view = inflater.inflate(R.layout.fragment_entry_list, container, false);
        ButterKnife.bind(this, view);

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

        //アップデートする
        mHeadLineListModel.pullNewHeadLine(this.getActivity(), getLoaderManager(), mCategory);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mListener.onSetTitle(mCategory);
        mProgressBar.setVisibility(View.GONE);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh");
                mHeadLineListModel.pullNewHeadLine(HeadLineListFragment.this.getActivity(), getLoaderManager(), mCategory);
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
        Log.d(TAG, "onAttach");
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
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView()");
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHeadLineListUpdateError(String errorMessage) {
        mListener.onShowTextMessage(errorMessage);
        EntryAdapter adapter = new EntryAdapter(this.getActivity(), R.layout.list_item, mHeadLineListModel.getHeadLineList(mCategory));
        mListView.setAdapter(adapter);
    }

    @Override
    public void onHeadLineListUpdated(List<HeadLine> headlineList, int updatedCount) {
        if (updatedCount > 0) {
            EntryAdapter adapter = new EntryAdapter(this.getActivity(), R.layout.list_item, headlineList);
            mListView.setAdapter(adapter);
            mCategory = headlineList.get(0).getmCategory();
        } else {
            EntryAdapter adapter = new EntryAdapter(this.getActivity(), R.layout.list_item, mHeadLineListModel.getHeadLineList(mCategory));
            mListView.setAdapter(adapter);
        }

        //更新ダイアログを停止する。
        mSwipeRefreshLayout.setRefreshing(false);

    }

    public void setViewGone() {
        mListView.setVisibility(View.GONE);
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

        void onSetTitle(String category);
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
