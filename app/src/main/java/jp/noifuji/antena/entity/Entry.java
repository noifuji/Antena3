package jp.noifuji.antena.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ryoma on 2015/10/24.
 */
public class Entry implements Serializable {
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

    public Entry() {
    }

    /**
     *
     * @param json
     * @throws JSONException title, publicationDate, sitetitle, summary, url, thumbnailのいずれかががない場合
     */
    public Entry(JSONObject json) throws JSONException {
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
}
