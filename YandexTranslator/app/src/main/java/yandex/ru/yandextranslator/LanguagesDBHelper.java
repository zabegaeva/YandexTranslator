package yandex.ru.yandextranslator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Afina on 11.04.2017.
 */
public class LanguagesDBHelper extends SQLiteOpenHelper {

    public LanguagesDBHelper(Context context) {
        super(context, CONST.DATABASE_NAME, null, CONST.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ CONST.TABLE_LANGUAGES +"("
                + CONST.COLUMN_LANG_FULL +","
                + CONST.COLUMN_RESENTLY_FROM +","
                + CONST.COLUMN_LANG_SHORT +","
                + CONST.COLUMN_RESENTLY_TO +");");

        db.execSQL("create table "+ CONST.TABLE_HISTORY +"("
                +  CONST.COLUMN_ID +" INTEGER PRIMARY KEY,"
                +  CONST.COLUMN_INPUT + ", "
                +  CONST.COLUMN_LANG_FROM + ", "
                +  CONST.COLUMN_LANG_TO + ", "
                +  CONST.COLUMN_TRANSLATE +", "
                +  CONST.COLUMN_FAVORITE + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
