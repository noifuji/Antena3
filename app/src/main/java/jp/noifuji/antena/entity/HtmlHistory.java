package jp.noifuji.antena.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ryoma on 2015/11/09.
 */
public class HtmlHistory extends ArrayList<HtmlPage> implements Serializable{
    private static long serialVersionUID = 4324423434L;
    public HtmlPage getLatestHistory() {
        if(this.size() == 0) {
            return null;
        }

        return get(this.size() - 1);
    }

    /**
     * ヒストリがあればtrue
     * @return
     */
    public boolean hasHistory() {
        if (this.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void push(HtmlPage h) {
        this.add(h);
    }

    public HtmlPage pop() {
        if(this.size() == 0) {
            return null;
        }

        HtmlPage h = this.getLatestHistory();
        this.remove(this.size()-1);
        return h;
    }
}
