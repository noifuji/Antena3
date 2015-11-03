package jp.noifuji.antena.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import jp.noifuji.antena.R;
import jp.noifuji.antena.fragment.EntryListFragment;

public class MainActivity extends AppCompatActivity implements EntryListFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            EntryListFragment fragment = new EntryListFragment();
            transaction.replace(R.id.entry_list_fragment, fragment);
            transaction.commit();
        }

        // loaderの初期化
        //getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            // 次画面のアクティビティ起動
            startActivity(intent);
            return true;
        } else if (id == R.id.action_info) {
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStartWebView(String uri) {
        // インテントのインスタンス生成
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra("URI", uri);
        // 次画面のアクティビティ起動
        startActivity(intent);
    }

    @Override
    public void onShowTextMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
