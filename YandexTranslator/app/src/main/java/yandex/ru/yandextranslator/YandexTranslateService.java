package yandex.ru.yandextranslator;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Afina on 11.04.2017.
 */
public interface YandexTranslateService {
    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/translate")
    Call<ResponseData.Translate> translate(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/getLangs")
    Call<ResponseData.LanguagesList> getLangTranslateList(@FieldMap Map<String, String> map);

}
