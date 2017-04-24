package yandex.ru.yandextranslator;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.MenuItem;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends FragmentActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private LanguagesDBHelper dbh;
    private Map<String, String> langMap;
    private LinkedHashMap<String,String> recentlyLangsFrom, recentlyLangsTo;
    private MyApplication myApp;
    Map<String, String> mapJson;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        myApp = (MyApplication) this.getApplication();

        //загрузка направлений перевода
        File database = getApplicationContext().getDatabasePath(CONST.DATABASE_NAME);
        if (!database.exists()) {
            // Загрузка направлений перевода, если БД не создана
            LoadLangList loadLangList = new LoadLangList();
            loadLangList.execute();
        } else {
            // загрузка списка направлений из бд во внутреннюю память в langMap
            loadLangsToMap();
            createTranslateFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dbh = new LanguagesDBHelper(MainActivity.this);
        SQLiteDatabase db = dbh.getWritableDatabase();
        int i = 1;
        for (String lang: myApp.getRecentlyLangsFrom().keySet()) {
            String strSQL = "UPDATE " + CONST.TABLE_LANGUAGES +
                    " SET " + CONST.COLUMN_RESENTLY_FROM + " = " + i +
                    " WHERE " + CONST.COLUMN_LANG_SHORT +" = " + '"'+myApp.getLangMap().get(lang)+'"';
            db.execSQL(strSQL);
            i++;
        }

        i = 1;
        for (String lang: myApp.getRecentlyLangsTo().keySet()) {
            String strSQL = "UPDATE " + CONST.TABLE_LANGUAGES +
                    " SET " + CONST.COLUMN_RESENTLY_TO + " = " + i +
                    " WHERE " + CONST.COLUMN_LANG_SHORT + " = " + '"' + myApp.getLangMap().get(lang)+'"';
            db.execSQL(strSQL);
            i++;
        }
        dbh.close();
    }

    private void addToBackstack(Fragment fragment, String backstackName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getName())
                .addToBackStack(backstackName)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = null;
        Bundle args=new Bundle();
        switch (item.getItemId()) {
            case R.id.btn_main:
                if (manager.getBackStackEntryCount() != 0) {
                    manager.popBackStack();
                }
                break;
            case R.id.btn_history:
                if (manager.getBackStackEntryCount() != 0) {
                    if (manager.getBackStackEntryAt(0).getName().equals(CONST.TAG_HISTORY)) {
                        return true;
                    }
                    manager.popBackStack();
                }
                args.putString(CONST.ARG_TYPE_FRAGMENT, CONST.TAG_HISTORY);
                fragment = new HistoryListFragment();
                fragment.setArguments(args);
                addToBackstack(fragment, CONST.TAG_HISTORY);
                break;
            case R.id.btn_favor:
                if (manager.getBackStackEntryCount() != 0) {
                    if (manager.getBackStackEntryAt(0).getName().equals(CONST.TAG_FAVORITE)) {
                        return true;
                    }
                    manager.popBackStack();
                }
                args.putString(CONST.ARG_TYPE_FRAGMENT, CONST.TAG_FAVORITE);
                fragment = new HistoryListFragment();
                fragment.setArguments(args);
                addToBackstack(fragment, CONST.TAG_FAVORITE);
                break;
        }
        return true;
    }

    // Загрузка направлений перевода в AsyncTask (запускается, если БД языков не создана)
    public class LoadLangList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //Запрос к серверу для получения списка направлений перевода
            mapJson = new HashMap<>();
            mapJson.put("key", CONST.KEY_TRANSLATE);
            mapJson.put("ui", "ru");

            myApp.gson = new GsonBuilder().create();
            myApp.retrofitTranslate = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(myApp.gson))
                    .baseUrl(CONST.URL_TRANSLATE)
                    .build();

            Call<ResponseData.LanguagesList> call = myApp.serviceTranslate.getLangTranslateList(mapJson);
            Map<String, String> lmap = null;
            try {
                lmap = call.execute().body().getLanguageMap();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Создание и подключение БД
            dbh = new LanguagesDBHelper(MainActivity.this);
            SQLiteDatabase db = dbh.getWritableDatabase();
            ContentValues cv = new ContentValues();

            //Вставка данных в таблицу направдений перевода
            db.beginTransaction();
            for (Map.Entry<String, String> val : lmap.entrySet()) {
                cv.put(CONST.COLUMN_LANG_FULL, val.getValue());
                cv.put(CONST.COLUMN_LANG_SHORT, val.getKey());
                cv.put(CONST.COLUMN_RESENTLY_FROM, 0);
                cv.put(CONST.COLUMN_RESENTLY_TO, 0);
                db.insert(CONST.TABLE_LANGUAGES, null, cv);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            dbh.close();

            // Выгрузить данные из БД во внутреннюю память
            loadLangsToMap();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // установка главного фрагмента
            createTranslateFragment();
        }
    }

    //Создание начального фрагмента при запуске приложения
    // и передача списка направлений перевода фрагменту Translate
    public void createTranslateFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            Bundle args = new Bundle();
            args.putSerializable(CONST.ARG_LANGS_FROM_DB, (HashMap<String, String>) langMap);
            fragment = new TranslateFragment();
            fragment.setArguments(args);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment, CONST.TAG_TRANSLATE)
                    .commit();
        }
    }

    //загрузка списка направлений переводов из БД в map и в Application
    public  void loadLangsToMap() {
        langMap = new HashMap<String,String>();
        recentlyLangsFrom = new LinkedHashMap<>();
        recentlyLangsTo = new LinkedHashMap<>();

        dbh = new LanguagesDBHelper(MainActivity.this);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.query(CONST.TABLE_LANGUAGES, null, null, null, null, null, null);

        Pair<String,String>[] recLangsFrom = new Pair[CONST.SIZE_RECENTLY_LIST];

        Pair<String,String>[] recLangsTo = new Pair[CONST.SIZE_RECENTLY_LIST];

        if (c.moveToFirst()) {
            int langColIndex = c.getColumnIndex(CONST.COLUMN_LANG_FULL);
            int idLangColIndex = c.getColumnIndex(CONST.COLUMN_LANG_SHORT);
            int recLangFromColIndex = c.getColumnIndex(CONST.COLUMN_RESENTLY_FROM);
            int recLangToColIndex = c.getColumnIndex(CONST.COLUMN_RESENTLY_TO);

            // заполнение map недавно используемых языков в порядке добавления

            do {
                langMap.put(c.getString(langColIndex), c.getString(idLangColIndex));
                if (c.getInt(recLangFromColIndex) != 0) {
                    recLangsFrom[c.getInt(recLangFromColIndex)] = new Pair<>(c.getString(langColIndex),c.getString(idLangColIndex));
                }
                if (c.getInt(recLangToColIndex) != 0) {
                    recLangsTo[c.getInt(recLangToColIndex)] = new Pair<>(c.getString(langColIndex),c.getString(idLangColIndex));
                }
            } while (c.moveToNext());
        }

        for (int i = 1; i < recLangsFrom.length; i++) {
            if (recLangsFrom[i] != null) {
                recentlyLangsFrom.put(recLangsFrom[i].first, recLangsFrom[i].second);
            }
        }
        for (int i = 1; i< recLangsTo.length; i++) {
            if (recLangsTo[i] != null) {
                recentlyLangsTo.put(recLangsTo[i].first, recLangsTo[i].second);
            }
        }

        myApp.setLangMap(langMap);
        myApp.setRecenetlyLangsFrom(recentlyLangsFrom);
        myApp.setRecentlyLangsTo(recentlyLangsTo);

        c.close();
        dbh.close();
    }
}
