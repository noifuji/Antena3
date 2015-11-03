package jp.noifuji.antena.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import jp.noifuji.antena.R;

/**
 * Created by Ryoma on 2015/10/30.
 */
public class Utils {
    private static final String TAG = "Utils";
    private Utils() {
    }

    static public String getSDCardDirectory(Context context) {
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        String appDir = (String) context.getResources().getText(R.string.app_name);

        return sdcardPath + File.separator + appDir;
    }

    static public Serializable deserialize(String path, String filename) {
        Serializable data = null;
        try {
            String fullPath = path + File.separator + filename;
            ObjectInputStream objInStream = new ObjectInputStream(new FileInputStream(fullPath));
            data = (Serializable) objInStream.readObject();
            objInStream.close();
        } catch (FileNotFoundException e) {
            Log.w(TAG, "History file is not found.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void serialize(Serializable data, String path, String filename) {
        try {
            File dir = new File(path);
            //ディレクトリがない場合は作成します。
            if(!dir.exists()) {
                dir.mkdir();
            }
            String fullPath = path + File.separator + filename;
            ObjectOutputStream objOutStream = new ObjectOutputStream(new FileOutputStream(fullPath));
            objOutStream.writeObject(data);
            objOutStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 現在日時をDate型で取得する。
     */
    public static Date getNowDate(){
        final Date date = new Date(System.currentTimeMillis());
        return date;
    }

    public static Date getDayInMonth(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
}
