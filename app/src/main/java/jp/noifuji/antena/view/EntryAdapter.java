package jp.noifuji.antena.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jp.noifuji.antena.R;
import jp.noifuji.antena.entity.HeadLine;

/**
 * Created by Ryoma on 2015/10/24.
 */
public class EntryAdapter extends ArrayAdapter<HeadLine> {

    private Context mContext;
    private int mTextViewResourceId;
    private List<HeadLine> mItems;
    private LayoutInflater mInflater;


    public EntryAdapter(Context context, int resource, List<HeadLine> objects) {
        super(context, resource, objects);

        mContext = context;
        mTextViewResourceId = resource;
        mItems = objects;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 逆順に取得する。
     * @param position
     * @return
     */
    @Override
    public HeadLine getItem(int position) {
        return mItems.get(getCount() - 1 - position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(mTextViewResourceId, null);
        }

        HeadLine item = getItem(position);
        TextView tagNewTextView = (TextView) view.findViewById(R.id.tag_new);
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView publicationDateTextView = (TextView) view.findViewById(R.id.publication_date);
        TextView siteTitleTextView = (TextView) view.findViewById(R.id.site_title);

        titleTextView.setText(item.getmTitle());
        //publicationDateTextView.setText(item.getFormedPublicationDate("MM/dd HH:mm"));
        publicationDateTextView.setText(item.getTwitterLikeDate());
        siteTitleTextView.setText(item.getmSiteTitle());

        if(item.isNew()) {
            tagNewTextView.setVisibility(View.VISIBLE);
        } else {
            tagNewTextView.setVisibility(View.GONE);
        }

        if(item.isRead()) {
            titleTextView.setTypeface(null, Typeface.NORMAL);
            titleTextView.setTextColor(Color.argb(170, 0, 0, 0));
            publicationDateTextView.setTextColor(Color.argb(170, 0, 0, 0));
            siteTitleTextView.setTextColor(Color.argb(170, 0, 0, 0));
        }else{
            titleTextView.setTypeface(null, Typeface.BOLD);
            titleTextView.setTextColor(Color.BLACK);
            publicationDateTextView.setTextColor(Color.argb(170, 0, 0, 255));
            siteTitleTextView.setTextColor(Color.BLACK);
        }


        //以下にリストに表示したい項目を追加する。

        return view;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }
}
