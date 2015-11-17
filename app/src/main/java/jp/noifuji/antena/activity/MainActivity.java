package jp.noifuji.antena.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import jp.noifuji.antena.R;
import jp.noifuji.antena.constants.Category;
import jp.noifuji.antena.entity.HeadLine;
import jp.noifuji.antena.fragment.HeadLineListFragment;

public class MainActivity extends AppCompatActivity implements HeadLineListFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    ActionBarDrawerToggle mDrawerToggle;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    TextView mNewestEntryTitle;

    boolean isFragmentChanging = false;
    String mFragmentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ツールバーをアクションバーとしてセット
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            HeadLineListFragment fragment = HeadLineListFragment.newInstance(Category.ALL);
            transaction.replace(R.id.entry_list_fragment, fragment);
            transaction.commit();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNewestEntryTitle = (TextView) findViewById(R.id.newest_entry_title);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //((HeadLineListFragment) getFragmentManager().findFragmentById(R.id.entry_list_fragment)).setViewGone();

                //Closing drawer on item click
                mDrawer.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.new_entry:
                        mFragmentCategory = Category.ALL;
                        isFragmentChanging = true;
                        break;
                    case R.id.news:
                        mFragmentCategory = Category.NEWS;
                        isFragmentChanging = true;
                        break;
                    case R.id.sports:
                        mFragmentCategory = Category.SPORTS;
                        isFragmentChanging = true;
                        break;
                    case R.id.vip:
                        mFragmentCategory = Category.VIP;
                        isFragmentChanging = true;
                        break;
                    case R.id.kijo:
                        mFragmentCategory = Category.KIJO;
                        isFragmentChanging = true;
                        break;
                    case R.id.money:
                        mFragmentCategory = Category.MONEY;
                        isFragmentChanging = true;
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }


                return true;
            }
        });

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                Log.i(TAG, "onDrawerClosed");
                if(isFragmentChanging) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    HeadLineListFragment fragment = HeadLineListFragment.newInstance(mFragmentCategory);
                    transaction.replace(R.id.entry_list_fragment, fragment);
                    transaction.commit();
                    isFragmentChanging = false;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.i(TAG, "onDrawerOpened");
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // ActionBarDrawerToggleクラス内の同メソッドにてアイコンのアニメーションの処理をしている。
                // overrideするときは気を付けること。
                super.onDrawerSlide(drawerView, slideOffset);
                Log.i(TAG, "onDrawerSlide : " + slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // 表示済み、閉じ済みの状態：0
                // ドラッグ中状態:1
                // ドラッグを放した後のアニメーション中：2
                Log.i(TAG, "onDrawerStateChanged  new state : " + newState);
            }
        };

        mDrawer.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
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
    public void onSetTitle(String category) {
        int resourceId = 0;
        if(category.equals(Category.ALL)) {
            resourceId = R.string.new_arrival;
        } else if(category.equals(Category.NEWS)) {
            resourceId = R.string.news;
        } else if(category.equals(Category.MONEY)) {
            resourceId = R.string.money;
        } else if(category.equals(Category.KIJO)) {
            resourceId = R.string.kijo;
        } else if(category.equals(Category.SPORTS)) {
            resourceId = R.string.sports;
        } else if(category.equals(Category.VIP)) {
            resourceId = R.string.vip;
        } else {
            return;
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(resourceId));
        }
    }

    @Override
    public void onSetNewestEntryTitle(HeadLine headline) {
        if(headline == null){
            return;
        }
        mNewestEntryTitle.setText(headline.getmTitle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
