package yandex.ru.yandextranslator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Afina on 18.04.2017.
 */
public class LanguageListFragment extends Fragment {

    private ListView lv_languages;
    private MyApplication myApp;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_list,container, false);

        myApp = (MyApplication) getActivity().getApplication();
        lv_languages = (ListView)view.findViewById(R.id.language_list_view);
        LanguageItemsArrayAdapter adapter = new LanguageItemsArrayAdapter(container.getContext(), getLangList());
        lv_languages.setAdapter(adapter);

        lv_languages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               onLangItemClick(parent, view, position, id);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        BottomNavigationView navigationView = (BottomNavigationView)getActivity().findViewById(R.id.navigation);
        navigationView.setVisibility(View.VISIBLE);
    }

    // Обработчик нажатия на item
    private void onLangItemClick(AdapterView<?> parent, View view, int position, long id) {
        LanguageItem data = (LanguageItem)parent.getItemAtPosition(position);
        Intent intent = new Intent();

        switch (getTargetRequestCode()) {
            case CONST.REQUEST_CODE_LANG_FROM:
                intent.putExtra(CONST.ARG_LANG_FROM, data.getString());
                if (getActivity().getIntent().getStringExtra(CONST.ARG_LANG_TO) != null) {
                    intent.putExtra(CONST.ARG_LANG_TO, getActivity().getIntent().getStringExtra(CONST.ARG_LANG_TO));
                }
                break;
            case CONST.REQUEST_CODE_LANG_TO:
                intent.putExtra(CONST.ARG_LANG_TO, data.getString());
                if (getActivity().getIntent().getStringExtra(CONST.ARG_LANG_FROM) != null) {
                    intent.putExtra(CONST.ARG_LANG_FROM, getActivity().getIntent().getStringExtra(CONST.ARG_LANG_FROM));
                }
                break;
        }

        myApp.addRecentlyLang(data.getString(), getTargetRequestCode());

        getActivity().setIntent(intent);
        getFragmentManager().popBackStack();
    }


    // формирование списка языков
    private List<LanguageItem> getLangList() {

        Map<String,String> langMap;
        myApp=(MyApplication) getActivity().getApplication();
        langMap = myApp.getLangMap();

        Map<String,String> recentlyLangFrom = myApp.getRecentlyLangsFrom();

        Map<String,String> recentlyLangTo = myApp.getRecentlyLangsTo();

        Set<String> langSet = new TreeSet<String>(langMap.keySet());

        ArrayList<String> recLangFrom = new ArrayList<>(recentlyLangFrom.keySet());
        ArrayList<String> recLangTo = new ArrayList<>(recentlyLangTo.keySet());

        reverseArrayList(recLangFrom);
        reverseArrayList(recLangTo);

        ArrayList<LanguageItem> langList = new ArrayList<>();

        if (getTargetRequestCode() == CONST.REQUEST_CODE_LANG_FROM) {
            if (!recentlyLangFrom.isEmpty()) {
                langList.add(new HeaderItem("НЕДАВНО ИСПОЛЬЗУЕМЫЕ"));
                for (String lang: recLangFrom ) {
                    langList.add(new LangItem(lang));
                }
                langList.add(new HeaderItem("ВСЕ ЯЗЫКИ"));
            }
        } else {
            if (!recentlyLangTo.isEmpty()) {
                langList.add(new HeaderItem("НЕДАВНО ИСПОЛЬЗУЕМЫЕ"));
                for (String lang: recLangTo) {
                    langList.add(new LangItem(lang));
                }
                langList.add(new HeaderItem("ВСЕ ЯЗЫКИ"));
            }
        }

        for (String lang: langSet ) {
            langList.add(new LangItem(lang));
        }

        return langList;

    }

    public ArrayList<String> reverseArrayList(ArrayList<String> arrayList) {
        for (int i = 0; i < (arrayList.size() / 2); i++) {
            String tmp = arrayList.get(i);
            arrayList.set(i, arrayList.get(arrayList.size()-i-1));
            arrayList.set((arrayList.size()-i-1), tmp);
        }
        return arrayList;
    }

    // Адаптер для списка языков
    public static class LanguageItemsArrayAdapter extends ArrayAdapter<LanguageItem> {

        private LayoutInflater inflater;

        public enum RowType {
            HEADER_ITEM, LANG_ITEM
        }

        public LanguageItemsArrayAdapter(Context context, List<LanguageItem> items) {
            super(context,0, items);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getViewTypeCount() {
            return RowType.values().length;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getViewType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(inflater, position,convertView);
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position).isEnabled();
        }
    }

    public class HeaderItem implements LanguageItem {
        private String itemName;

        @Override
        public String getString() {
            return itemName;
        }

        public HeaderItem(String name) {
            itemName = name;
        }
        @Override
        public int getViewType() {
            return LanguageItemsArrayAdapter.RowType.HEADER_ITEM.ordinal();
        }

        @Override
        public View getView(LayoutInflater inflater, int position, View convertView) {
            View view;
            if (convertView == null) {
                view = inflater.inflate(R.layout.item_header, null);
            } else {
                view = convertView;
            }
            TextView text = (TextView) view.findViewById(R.id.header_item_text_view);
            text.setText(itemName);
            return view;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    public class LangItem implements LanguageItem {
        private String language;

        public LangItem(String lang) {
            language = lang;
        }

        @Override
        public String getString() {
            return language;
        }

        @Override
        public int getViewType() {
            return LanguageItemsArrayAdapter.RowType.LANG_ITEM.ordinal();
        }

        @Override
        public View getView(LayoutInflater inflater, int position, View convertView) {
            View view;
            if (convertView == null) {
                view = inflater.inflate(R.layout.item_language, null);
            } else view = convertView;

            TextView textView = (TextView)view.findViewById(R.id.lang_item_text_view);
            textView.setText(language);

            return view;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}

