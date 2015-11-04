package jp.noifuji.antena.model;

import android.content.Context;

/**
 * Created by ryoma on 2015/11/04.
 */
public class ModelFactory {

    private static ModelFactory instance = new ModelFactory();
    private HeadLineListModel mHeadLineListModel;
    private ModelFactory(){}

    public static  ModelFactory getInstance() {
        return instance;
    }

    public HeadLineListModel getmHeadLineListModel(Context context) {
        if(mHeadLineListModel == null) {
            mHeadLineListModel = new HeadLineListModel(context);
        }
        return mHeadLineListModel;
    }
}
