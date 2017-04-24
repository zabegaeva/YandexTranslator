package yandex.ru.yandextranslator;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Afina on 19.04.2017.
 */
public class MyApplication extends Application {
    public Gson gson;
    public Retrofit retrofitTranslate;
    public YandexTranslateService serviceTranslate;
    public Retrofit retrofitDictionary;
    public YandexDictionaryService serviceDictionary;

    private Map<String, String> langMap;
    private LinkedHashMap<String,String> recenetlyLangsFrom;
    private LinkedHashMap<String,String> recenetlyLangsTo;


    @Override
    public void onCreate() {
        super.onCreate();

        gson = new GsonBuilder().create();
        retrofitTranslate = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(CONST.URL_TRANSLATE)
                .build();

        retrofitDictionary = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(CONST.URL_DICTIONARY)
                .build();

        serviceTranslate = retrofitTranslate.create(YandexTranslateService.class);
        serviceDictionary= retrofitDictionary.create(YandexDictionaryService.class);

    }

    public Map<String, String> getLangMap() {
        return langMap;
    }

    public  LinkedHashMap<String, String> getRecentlyLangsFrom() {
        return recenetlyLangsFrom;
    }

    public LinkedHashMap<String, String> getRecentlyLangsTo() {
        return recenetlyLangsTo;
    }

    public void setRecentlyLangsTo(LinkedHashMap<String, String> recenetlyLangsTo) {
        this.recenetlyLangsTo = recenetlyLangsTo;
    }

    private void addRecentlyLangTo(String recenetlyLangsFull, String recentlyLangShort) {
        this.recenetlyLangsTo.put(recenetlyLangsFull, recentlyLangShort);
    }
    private void addRecentlyLangFrom(String recenetlyLangsFull, String recentlyLangShort) {
        this.recenetlyLangsFrom.put(recenetlyLangsFull, recentlyLangShort);
    }

    public void setLangMap(Map<String, String> langMap) {
        this.langMap = langMap;
    }

    public void setRecenetlyLangsFrom(LinkedHashMap<String, String> recenetlyLangsFrom) {
        this.recenetlyLangsFrom = recenetlyLangsFrom;
    }

    // Добавление в недавно используемые языки
    public void addRecentlyLang(String data, int requestCode) {

        switch (requestCode) {
            case CONST.REQUEST_CODE_LANG_FROM:
                if (getRecentlyLangsFrom().size() < CONST.SIZE_RECENTLY_LIST-1) {
                    this.addRecentlyLangFrom(data,getLangMap().get(data));
                }
                else {
                    // если язык выбран из недавно используемых, переносим его наверх
                    if(this.getRecentlyLangsFrom().containsValue(getLangMap().get(data))) {
                        getRecentlyLangsFrom().remove(data);
                        addRecentlyLangFrom(data,getLangMap().get(data));
                        break;
                    }
                    //если язык выбран из списка, удаляем первый добавленный
                    // в недавно используемые и добавляем новый язык
                    String firstLang = null;
                    for (String lang : getRecentlyLangsFrom().keySet()) {
                        firstLang = lang;
                        break;
                    }
                    getRecentlyLangsFrom().remove(firstLang);
                    addRecentlyLangFrom(data,getLangMap().get(data));
                }
                break;
            case CONST.REQUEST_CODE_LANG_TO:
                if (getRecentlyLangsTo().size() < CONST.SIZE_RECENTLY_LIST-1) {
                    addRecentlyLangTo(data,getLangMap().get(data));
                }
                else {
                    if(getRecentlyLangsTo().containsValue(getLangMap().get(data))) {
                        getRecentlyLangsTo().remove(data);
                        addRecentlyLangTo(data,getLangMap().get(data));
                        break;
                    }
                    String firstLang = null;
                    for (String lang : getRecentlyLangsTo().keySet()) {
                        firstLang = lang;
                        break;
                    }
                    getRecentlyLangsTo().remove(firstLang);
                    addRecentlyLangTo(data,getLangMap().get(data));
                }
                break;
        }
    }
}
