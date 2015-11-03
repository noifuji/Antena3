package jp.noifuji.antena.entity;

import java.util.ArrayList;

/**
 * Created by Ryoma on 2015/10/30.
 */
public class EntryList extends ArrayList<Entry> {

    public EntryList() {
        super();
    }

    /**
     * 最新のエントリを取得します。//@古いものから順に詰め込まれている前提
     * @return
     */
    public Entry getLatestEntry() {
        if(size() == 0) {
            return null;
        }
        return get(this.size()-1);
    }

}
