package yandex.ru.yandextranslator;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Afina on 22.04.2017.
 */
public interface LanguageItem {
    int getViewType();
    View getView(LayoutInflater inflater, int position, View convertView);
    boolean isEnabled();
    String getString();
}


