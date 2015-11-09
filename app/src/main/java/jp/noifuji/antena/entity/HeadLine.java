package jp.noifuji.antena.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ryoma on 2015/10/24.
 */
public class HeadLine implements Serializable {
    static final long serialVersionUID = 123123123L;
    private static final String SYS_ID = "_id";
    private static final String TITLE = "title";
    private static final String PUBLICATIONDATE = "publicationDate";
    private static final String SITETITLE = "sitetitle";
    private static final String SUMMARY = "summary";
    private static final String URL = "url";
    private static final String THUMBNAIL = "thumbnail";


    private String mSysId = "";
    private String mTitle = "";
    private String mUrl = "";
    private String mPublicationDate = "";
    private String mSiteTitle = "";
    private String mSummary = "";
    private String mThumbnailUrl = "";
    private boolean isRead = false;
    private boolean isNew = true;

    public HeadLine() {
    }

    /**
     *
     * @param json
     * @throws JSONException title, publicationDate, sitetitle, summary, url, thumbnailのいずれかががない場合
     */
    public HeadLine(JSONObject json) throws JSONException {
        setmSysId(json.getString(SYS_ID));
        setmTitle(json.getString(TITLE));
        setmPublicationDate(json.getString(PUBLICATIONDATE));
        setmSiteTitle(json.getString(SITETITLE));
        setmSummary(json.getString(SUMMARY));
        setmUrl(json.getString(URL));
        setmThumbnailUrl(json.getString(THUMBNAIL));
    }

    public String getmSysId() {
        return mSysId;
    }

    public void setmSysId(String mSysId) {
        this.mSysId = mSysId;
    }
    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmPublicationDate() {
        return mPublicationDate;
    }

    public String getFormedPublicationDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(mPublicationDate)));
    }

    public void setmPublicationDate(String mPublicationDate) {
        this.mPublicationDate = mPublicationDate;
    }

    public String getTwitterLikeDate() {
        Date now = new Date();
        Date publication = new Date(Long.valueOf(mPublicationDate));
        long diff = now.getTime() - publication.getTime();
        String result = "";

        if(diff < 1000*60*60) {
            result = diff/(1000*60) + "分前";
        } else if(diff >= 1000*60 && diff < 1000*60*60*24) {
            result = diff/(1000*60*60) + "時間前";
        } else {
            result = getFormedPublicationDate("MM/dd HH:mm");
        }

        return result;
    }

    public String getmSiteTitle() {
        return mSiteTitle;
    }

    public void setmSiteTitle(String mSiteTitle) {
        this.mSiteTitle = mSiteTitle;
    }

    public String getmSummary() {
        return mSummary;
    }

    public void setmSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public String getmThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setmThumbnailUrl(String mThumbnailUrl) {
        this.mThumbnailUrl = mThumbnailUrl;
    }


    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
