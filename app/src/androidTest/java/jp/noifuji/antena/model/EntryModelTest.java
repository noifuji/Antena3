package jp.noifuji.antena.model;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.lang.reflect.Field;

import jp.noifuji.antena.activity.MainActivity;
import jp.noifuji.antena.exception.AntenaSystemException;

import static org.mockito.Mockito.mock;

/**
 * Created by ryoma on 2015/11/05.
 */
public class EntryModelTest extends ActivityInstrumentationTestCase2<MainActivity> {

    EntryModel mEntryModel;
    Activity mActivity;

    public EntryModelTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mEntryModel = ModelFactory.getInstance().getmEntryModel();
        mActivity = getActivity();
    }


/*    @Test
    public void test001() throws Exception {
        EntryModel.EntryModelListener listenerMock = mock(EntryModel.EntryModelListener.class);
        mEntryModel.addListener(listenerMock);
        String url = "http://himasoku.com/archives/51925840.html";
        mEntryModel.loadEntry(getActivity(), getActivity().getLoaderManager(), url);

        verify(listenerMock, timeout(10000)).onEntryLoaded(anyString());
        mEntryModel.removeListener(listenerMock);
    }*/

    @Test
     public void testRemoveTagFromHtml() throws Exception {
        String html = "<html><head><head/><body><a></a></body></html>";
        String expect = "<html><head><head/><body></body></html>";
        Document doc = Jsoup.parse(html);
        Document expectDoc = Jsoup.parse(expect);
        doc = EntryModel.removeTagFromHtml(doc, "a");
        assertEquals(expectDoc.html(), doc.html());
    }

    @Test
    public void testRemoveTagFromHtmlAgr1IsNull() throws Exception {
        String html = "<html><body><a></a></body></html>";
        Document doc = Jsoup.parse(html);
        try {
            doc = EntryModel.removeTagFromHtml(null, "a");
            assertTrue(false);
        }catch(AntenaSystemException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testRemoveTagFromHtmlAgr2IsNull() throws Exception {
        String html = "<html><body><a></a></body></html>";
        Document doc = Jsoup.parse(html);
        try {
            doc = EntryModel.removeTagFromHtml(doc, null);
            assertTrue(false);
        }catch(AntenaSystemException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddListener() throws Exception {
        EntryModel.EntryModelListener listener = mock(EntryModel.EntryModelListener.class);
        mEntryModel.addListener(listener);
        // Fugaの「クラス情報」が収められたオブジェクト
        Class<EntryModel> c = EntryModel.class;
        // privateな変数を読み取り
        Field f1 = c.getDeclaredField( "mListener" );
        f1.setAccessible(true);
        EntryModel.EntryModelListener m = (EntryModel.EntryModelListener) f1.get( mEntryModel );

        //検証
        assertEquals(listener, m);
    }

    @Test
    public void testRemoveListener() throws Exception {
        EntryModel.EntryModelListener listener = mock(EntryModel.EntryModelListener.class);
        mEntryModel.addListener(listener);
        mEntryModel.removeListener(listener);
        // Fugaの「クラス情報」が収められたオブジェクト
        Class<EntryModel> c = EntryModel.class;
        // privateな変数を読み取り
        Field f1 = c.getDeclaredField( "mListener" );
        f1.setAccessible(true);
        EntryModel.EntryModelListener m = (EntryModel.EntryModelListener) f1.get( mEntryModel );

        //検証
        assertEquals(m, null);
    }

    @Test
    public void testRemoveListenerDifferentListener() throws Exception {
        EntryModel.EntryModelListener listener = mock(EntryModel.EntryModelListener.class);
        EntryModel.EntryModelListener listener2 = mock(EntryModel.EntryModelListener.class);
        mEntryModel.addListener(listener);
        mEntryModel.removeListener(listener2);
        // Fugaの「クラス情報」が収められたオブジェクト
        Class<EntryModel> c = EntryModel.class;
        // privateな変数を読み取り
        Field f1 = c.getDeclaredField( "mListener" );
        f1.setAccessible(true);
        EntryModel.EntryModelListener m = (EntryModel.EntryModelListener) f1.get( mEntryModel );

        //検証
        assertEquals(listener, m);
        mEntryModel.removeListener(listener);
    }
}