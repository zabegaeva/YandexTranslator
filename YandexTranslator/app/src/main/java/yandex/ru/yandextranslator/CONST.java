package yandex.ru.yandextranslator;

/**
 * Created by Afina on 21.04.2017.
 */
public class CONST {
    public static final String URL_TRANSLATE = "https://translate.yandex.net";
    public static final String KEY_TRANSLATE = "trnsl.1.1.20170424T063007Z.ca20bbfa91ee1e11.aa3ec594e130ed556b0eeaf7f926835af22980a9";
    public static final String URL_DICTIONARY = "https://dictionary.yandex.net";
    public static final String KEY_DICTIONARY = "dict.1.1.20170424T062922Z.d16a202a6ccafaa3.f5ff90e94327b20a64c1d842bb8448e902c77889";

    public static final String DATABASE_NAME = "Languages.db";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INPUT = "input";
    public static final String COLUMN_LANG_FROM = "langFrom";
    public static final String COLUMN_LANG_TO = "langTo";
    public static final String COLUMN_TRANSLATE = "translate";
    public static final String COLUMN_FAVORITE = "favorite";
    public static final String COLUMN_LANG_FULL = "langFull";
    public static final String COLUMN_LANG_SHORT = "langShort";
    public static final String COLUMN_RESENTLY_FROM = "recentlyFrom";
    public static final String COLUMN_RESENTLY_TO = "recentlyTo";
    public static final String TABLE_LANGUAGES = "languages";
    public static final String TABLE_HISTORY = "history";

    public static final String ARG_LANGS_FROM_DB = "langMapDB";
    public static final String ARG_TYPE_FRAGMENT = "type fragment";
    public static final String ARG_INPUT="input";
    public static final String ARG_TRANSLATE = "translate";
    public static final String ARG_LANG_FROM = "langFrom";
    public static final String ARG_LANG_TO = "langTo";


    public static final int REQUEST_CODE_LANG_FROM = 1;
    public static final int REQUEST_CODE_LANG_TO = 2;

    public static final String TAG_HISTORY = "history";
    public static final String TAG_FAVORITE = "favorite";
    public static final String TAG_TRANSLATE = "translate";

    public static final int SIZE_RECENTLY_LIST = 6;

    public static final int DATABASE_VERSION = 1;
}
