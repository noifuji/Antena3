package jp.noifuji.antena.entity;

/**
 * Created by Ryoma on 2015/11/09.
 */
public class HtmlHistory {
    private int mScrollX;
    private int mScrollY;
    private  String mHtml;

    public HtmlHistory(String html, int scrollX, int scrollY) {
        this.mHtml = html;
        this.mScrollX = scrollX;
        this.mScrollY = scrollY;
    }

    public HtmlHistory(String mHtml) {
        this.mHtml = mHtml;
    }

    public String getmHtml() {
        return mHtml;
    }

    public void setmHtml(String mHtml) {
        this.mHtml = mHtml;
    }


    public int getmScrollY() {
        return mScrollY;
    }

    public void setmScrollY(int mScrollY) {
        this.mScrollY = mScrollY;
    }

    public int getmScrollX() {
        return mScrollX;
    }

    public void setmScrollX(int mScrollX) {
        this.mScrollX = mScrollX;
    }

}
