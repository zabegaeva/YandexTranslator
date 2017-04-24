package yandex.ru.yandextranslator;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Afina on 24.04.2017.
 */
public interface YandexDictionaryService {

    @FormUrlEncoded
    @POST("/api/v1/dicservice.json/lookup")
    Call<ResponseData.Dictionary> getDictionary(@FieldMap Map<String, String> map);

}
