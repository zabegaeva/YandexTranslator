package yandex.ru.yandextranslator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Afina on 11.04.2017.
 */
public class TranslateFragment extends Fragment {

    private Map<String,String>  langMap;
    private String langFromTo;

    private Button btn_langFrom, btn_langTo;
    private ImageButton btn_changeLang;
    private EditText et_input_text;
    private TextView tv_translate;
    private CheckBox btn_set_favorite;
    private ImageButton btn_clear_text;
    private BottomNavigationView navigationView;
    private RelativeLayout rel_input_text;
    private DictionaryAdapter dictionaryAdapter;
    private ArrayAdapter exampleAdapter;

    private TextView tv_def_text, tv_def_gen, tv_def_pos;
    private RecyclerView dictionary_article_rec_view;


    private Map <String, String> mapJson;

    MyApplication myApp;
    private LanguagesDBHelper dbh;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myApp=(MyApplication) getActivity().getApplication();
        langMap=myApp.getLangMap();

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_translate, container, false);

        btn_langFrom = (Button)v.findViewById(R.id.lang_from_button);
        btn_langTo = (Button)v.findViewById(R.id.lang_to_button);
        btn_changeLang = (ImageButton)v.findViewById(R.id.change_lang_button);
        et_input_text = (EditText)v.findViewById(R.id.input_text_edit_text);
        et_input_text.setBackgroundColor(Color.TRANSPARENT);
        tv_translate = (TextView)v.findViewById(R.id.tv_translate);
        btn_set_favorite = (CheckBox)v.findViewById(R.id.set_favorite_button);
        btn_clear_text = (ImageButton)v.findViewById(R.id.clear_text_button);
        navigationView = (BottomNavigationView)getActivity().findViewById(R.id.navigation);
        tv_def_gen = (TextView) v.findViewById(R.id.def_gen_text_view);
        tv_def_text = (TextView)v.findViewById(R.id.def_text_view);
        tv_def_pos = (TextView)v.findViewById(R.id.def_pos_text_view);
        dictionary_article_rec_view = (RecyclerView)v.findViewById(R.id.dict_article_recycle_view);
        rel_input_text = (RelativeLayout)v.findViewById(R.id.input_text_relative_layout);

        dictionary_article_rec_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        clearDictionaryView();


        if (tv_translate.getText().toString().isEmpty()) {
            btn_set_favorite.setVisibility(View.INVISIBLE);
        }

        //заполнение полей направления перевода
        if (getActivity().getIntent().getStringExtra(CONST.ARG_LANG_FROM) != null) {
            btn_langFrom.setText(getActivity().getIntent().getStringExtra(CONST.ARG_LANG_FROM));
        }
        if (getActivity().getIntent().getStringExtra(CONST.ARG_LANG_TO) != null) {
            btn_langTo.setText(getActivity().getIntent().getStringExtra(CONST.ARG_LANG_TO));
        }

        btn_langFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChooseLanguage(v);
            }
        });
        btn_langTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChooseLanguage(v);
            }
        });
        btn_changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChangeLanguage(v);
            }
        });
        btn_set_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {onCheckedFavoriteChanged(compoundButton, isChecked);}});
        et_input_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                addToHistory();
                if (isFocus) {
                    rel_input_text.setBackgroundResource(R.drawable.shape_focus_edit_text);
                }
            }
        });
        if (et_input_text.isFocusable()) {
            rel_input_text.setBackgroundResource(R.drawable.shape_edit_text);
        }
        et_input_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                translate(charSequence);
                dictionary(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return  v;
    }

    @Override
    public void onResume() {
        super.onResume();

        btn_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickClearText(v);
            }
        });
        //заполнение текста, если он взят из истории или избранного

        if (getActivity().getIntent().getStringExtra(CONST.ARG_INPUT) != null) {
            et_input_text.setText(getActivity().getIntent().getStringExtra(CONST.ARG_INPUT).toString(),TextView.BufferType.EDITABLE);
        }
        langFromTo = langMap.get(btn_langFrom.getText().toString())+"-"+langMap.get(btn_langTo.getText().toString());

        if (!et_input_text.getText().toString().isEmpty()) {
            translate(et_input_text.getText().toString());
            dictionary(et_input_text.getText().toString());
        }
    }

    public void onClickClearText(View v) {
        addToHistory();
        et_input_text.setText(" ");
        et_input_text.setText(null);

        clearDictionaryView();
        btn_set_favorite.setVisibility(View.INVISIBLE);
    }

    //обработчик кнопки Избранное
    public void onCheckedFavoriteChanged(CompoundButton compoundButton, boolean isChecked) {
        addToHistory();

        dbh = new LanguagesDBHelper(getActivity());
        SQLiteDatabase db = dbh.getWritableDatabase();
        Cursor c = db.query(CONST.TABLE_HISTORY,null,null,null,null,null,null);

        if (c.moveToLast()) {
            int idIndex = c.getColumnIndex(CONST.COLUMN_ID);
            int favorite;
            if (isChecked) {
                favorite = 1;
                String strSQL = "UPDATE " + CONST.TABLE_HISTORY + " SET " + CONST.COLUMN_FAVORITE + " = " + favorite + " WHERE " + CONST.COLUMN_ID + " = " + c.getInt(idIndex);
                db.execSQL(strSQL);
            }
        }
        dbh.close();

    }

    public void translate(CharSequence text) {
        mapJson = new HashMap<>();
        mapJson.put("key", CONST.KEY_TRANSLATE);
        mapJson.put("text", text.toString());
        mapJson.put("lang",langFromTo);

        btn_set_favorite.setVisibility(View.VISIBLE);
        btn_set_favorite.setChecked(false);

        //запрос к серверу на перевод
        Call<ResponseData.Translate> call = myApp.serviceTranslate.translate(mapJson);
        call.enqueue(new Callback<ResponseData.Translate>() {
            @Override
            public void onResponse(Call<ResponseData.Translate> call, Response<ResponseData.Translate> response) {
                if (response.isSuccessful()) {
                    ResponseData.Translate data = response.body();
                    for (String dat : data.getText()) {
                        tv_translate.setText(dat);
                    }
                    tv_translate.setTextSize(TypedValue.COMPLEX_UNIT_PX, et_input_text.getTextSize());
                    tv_translate.setTextColor(et_input_text.getTextColors());
                }
            }
            @Override
            public void onFailure(Call<ResponseData.Translate> call, Throwable t) {
                tv_translate.setText("Ошибка соединения");
                tv_translate.setTextColor(Color.GRAY);
            }
        });
    }

    public void dictionary(String text) {
        mapJson = new HashMap<>();
        mapJson.put("key", CONST.KEY_DICTIONARY);
        mapJson.put("text", text);
        mapJson.put("lang",langFromTo);
        mapJson.put("ui","ru");

        Call<ResponseData.Dictionary> dictionaryCall = myApp.serviceDictionary.getDictionary(mapJson);
        dictionaryCall.enqueue(new Callback<ResponseData.Dictionary>() {
            @Override
            public void onResponse(Call<ResponseData.Dictionary> call, Response<ResponseData.Dictionary> response) {
                if (response.isSuccessful()) {
                    ResponseData.Dictionary responce = response.body();
                    List<DictionaryArticleItem> dictionaryItems = new ArrayList<DictionaryArticleItem>();
                    DictionaryArticleItem dicItem;

                    if (responce.getDef() != null && (responce.getDef().size()>0)) {
                        tv_def_text.setText(responce.getDef().get(0).getText());
                        if (responce.getDef().get(0).getPartOfSpeach() != null) {
                            tv_def_pos.setText(responce.getDef().get(0).getPartOfSpeach());
                        }
                        if (responce.getDef().get(0).getGenus() != null) {
                            tv_def_gen.setText(responce.getDef().get(0).getGenus());
                        }
                        for (int j = 0; j < responce.getDef().get(0).getTranslations().size(); j++) {
                            dicItem = new DictionaryArticleItem();
                            dicItem.setTranslate(responce.getDef().get(0).getTranslations().get(j).getText());
                            dicItem.setNumber(j + 1);
                            if (responce.getDef().get(0).getTranslations().get(j).getSynonyms() != null) {
                                dicItem.setSynonyms(responce.getDef().get(0).getTranslations().get(j).getSynonyms());
                            }
                            if (responce.getDef().get(0).getTranslations().get(j).getMeanings() != null) {
                                dicItem.setMeanings(responce.getDef().get(0).getTranslations().get(j).getMeanings());
                            }
                            if (responce.getDef().get(0).getTranslations().get(j).getExamples() != null) {
                                dicItem.setExamples(responce.getDef().get(0).getTranslations().get(j).getExamples());
                            }
                            dictionaryItems.add(dicItem);
                            dictionaryAdapter = new DictionaryAdapter(dictionaryItems);
                            dictionary_article_rec_view.setAdapter(dictionaryAdapter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData.Dictionary> call, Throwable t) {
                tv_translate.setText("Ошибка соединения");
                tv_translate.setTextColor(Color.GRAY);
            }
        });
    }
    public void translate(String text) {
        mapJson = new HashMap<>();
        mapJson.put("key", CONST.KEY_TRANSLATE);
        mapJson.put("text", text);
        mapJson.put("lang",langFromTo);

        btn_set_favorite.setVisibility(View.VISIBLE);
        btn_set_favorite.setChecked(false);

        //запрос к серверу на перевод
        Call<ResponseData.Translate> call = myApp.serviceTranslate.translate(mapJson);
        call.enqueue(new Callback<ResponseData.Translate>() {
            @Override
            public void onResponse(Call<ResponseData.Translate> call, Response<ResponseData.Translate> response) {
                if (response.isSuccessful()) {
                    ResponseData.Translate data = response.body();
                    for (String dat : data.getText()) {
                        tv_translate.setText(dat);
                    }
                    tv_translate.setTextSize(TypedValue.COMPLEX_UNIT_PX, et_input_text.getTextSize());
                    tv_translate.setTextColor(et_input_text.getTextColors());
                    addToHistory();
                }
            }
            @Override
            public void onFailure(Call<ResponseData.Translate> call, Throwable t) {
                tv_translate.setText("Ошибка соединения");
                tv_translate.setTextColor(Color.GRAY);
            }
        });
    }

    public void addToHistory() {
        //проверка на пустоту полей перевода и ввода текста
        if ( et_input_text.getText().toString().isEmpty() || tv_translate.getText().toString().isEmpty()){
            return;
        }

        dbh = new LanguagesDBHelper(getActivity());
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Cursor c = db.query(CONST.TABLE_HISTORY,null,null,null,null,null,null);

        int favorite = -1;
        //Проверка, что добавляемое слово не совпадает с одним из 7 последних
        //слов в истории
        if (c.moveToLast()) {
            String input, langFrom,langTo,translate;
            int inputIndex,langFromIndex,langToIndex,translateIndex, idIndex, favoriteIndex;
            int count = 0;
            do {
                inputIndex = c.getColumnIndex(CONST.COLUMN_INPUT);
                langFromIndex = c.getColumnIndex(CONST.COLUMN_LANG_FROM);
                langToIndex = c.getColumnIndex(CONST.COLUMN_LANG_TO);
                translateIndex = c.getColumnIndex(CONST.COLUMN_TRANSLATE);
                idIndex = c.getColumnIndex(CONST.COLUMN_ID);
                favoriteIndex = c.getColumnIndex(CONST.COLUMN_FAVORITE);
                input = c.getString(inputIndex);
                langFrom = c.getString(langFromIndex);
                langTo = c.getString(langToIndex);
                translate = c.getString(translateIndex);
                if (input.equals(et_input_text.getText().toString()) &&
                        langFrom.equals(btn_langFrom.getText().toString()) &&
                        langTo.equals(btn_langTo.getText().toString()) &&
                        translate.equals(tv_translate.getText().toString())) {

                            favorite = c.getInt(favoriteIndex);
                            String strSQL = "DELETE FROM " +CONST.TABLE_HISTORY +
                                            " WHERE "+ CONST.COLUMN_ID +" = " + c.getInt(idIndex);
                            db.execSQL(strSQL);
                }
                count++;
            } while (c.moveToPrevious() && (count < 7)) ;
        }

        db.beginTransaction();

        cv.put(CONST.COLUMN_INPUT, et_input_text.getText().toString());
        cv.put(CONST.COLUMN_LANG_FROM, btn_langFrom.getText().toString());
        cv.put(CONST.COLUMN_LANG_TO, btn_langTo.getText().toString());
        cv.put(CONST.COLUMN_TRANSLATE, tv_translate.getText().toString());
        if (favorite != -1) {
            cv.put(CONST.COLUMN_FAVORITE, favorite);
        } else cv.put(CONST.COLUMN_FAVORITE, 0);

        db.insert(CONST.TABLE_HISTORY, null, cv);

        db.setTransactionSuccessful();
        db.endTransaction();
        dbh.close();
    }

    //Метод проверки открытия клавиатуры
    private boolean keyboardShown(View rootView) {

        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    //Обработка кнопок выбора языков
    public void onClickChooseLanguage(View v) {
        addToHistory();
        tv_translate.setText("");
        clearDictionaryView();

        // прячем клавиатуру
        if (keyboardShown(v.getRootView())) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS,0);
        }

        LanguageListFragment languageListFragment = new LanguageListFragment();
        switch (v.getId()) {
            case R.id.lang_from_button:
                languageListFragment.setTargetFragment(this, CONST.REQUEST_CODE_LANG_FROM);
                break;
            case R.id.lang_to_button:
                languageListFragment.setTargetFragment(this, CONST.REQUEST_CODE_LANG_TO);
                break;
        }
        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        navigationView.setVisibility(View.GONE);
        ft.replace(R.id.fragment_container, languageListFragment).addToBackStack(null);

        btn_set_favorite.setChecked(false);
        ft.commit();
    }

    //обработчик нажатия кнопки смены направления перевода
    public void onClickChangeLanguage(View v) {
        addToHistory();
        clearDictionaryView();

        String langTo = btn_langTo.getText().toString();
        btn_langTo.setText(btn_langFrom.getText().toString());
        btn_langFrom.setText(langTo);
        langFromTo = langMap.get(btn_langFrom.getText().toString())+"-"+langMap.get(btn_langTo.getText().toString());

        String text = et_input_text.getText().toString();
        et_input_text.setText(tv_translate.getText().toString());
        tv_translate.setText(text);

        translate(et_input_text.getText().toString());
        dictionary(et_input_text.getText().toString());

        //запись в недавно используемые
        myApp.addRecentlyLang(btn_langFrom.getText().toString(), CONST.REQUEST_CODE_LANG_FROM);
        myApp.addRecentlyLang(btn_langTo.getText().toString(), CONST.REQUEST_CODE_LANG_TO);

        // передача значения через Activity, чтобы оно обновилось при следующем запуске TranslateFragment
        Intent intent = new Intent();
        intent.putExtra(CONST.ARG_LANG_FROM,btn_langFrom.getText().toString());
        intent.putExtra(CONST.ARG_LANG_TO,btn_langTo.getText().toString());
        getActivity().setIntent(intent);
        btn_set_favorite.setChecked(false);
    }

    public void clearDictionaryView() {
        tv_def_text.setText("");
        tv_def_gen.setText("");
        tv_def_pos.setText("");
        List<DictionaryArticleItem> list = new ArrayList<DictionaryArticleItem>();
        dictionaryAdapter = new DictionaryAdapter(list);
        dictionary_article_rec_view.setAdapter(dictionaryAdapter);
    }

    //Holder для статей словаря
    private class DictionaryHolder extends RecyclerView.ViewHolder {
        private TextView dic_number_article;
        private TextView tv_dic_translate_text;
        private TextView tv_dic_synonims;
        private TextView tv_dic_meanings;
        private ListView lv_dic_exampes;
        private String synonyms = "";
        private String meanings = "";
        private String example = "";
        private List<String> examples = new ArrayList<>();

        private DictionaryArticleItem dictionaryItem = new DictionaryArticleItem();

        public DictionaryHolder(View itemView) {
            super(itemView);

            dic_number_article = (TextView) itemView.findViewById(R.id.dic_number_article_text_view);
            tv_dic_translate_text = (TextView) itemView.findViewById(R.id.dic_translate_text_view);
            tv_dic_synonims = (TextView) itemView.findViewById(R.id.dic_synonim_text_view);
            tv_dic_meanings = (TextView) itemView.findViewById(R.id.dic_meaning_text_view);
            lv_dic_exampes = (ListView) itemView.findViewById(R.id.dic_example_list_view);

        }

        public void bindDictionaryArticle(DictionaryArticleItem dictionary) {
            dictionaryItem = dictionary;
            tv_dic_translate_text.setText(dictionary.getTranslate());
            dic_number_article.setText(dictionary.getNumber().toString());
            if (dictionary.getSynonyms() != null) {
                for (ResponseData.Synonym syn: dictionary.getSynonyms()) {
                    Log.d("ml", syn.getText());
                    synonyms = synonyms.concat(syn.getText()+", ");
                }
                synonyms = synonyms.substring(0, synonyms.length()-2);
                tv_dic_synonims.setText(synonyms);
            }
            if (dictionary.getMeanings() != null) {
                meanings = meanings.concat("(");
                for (ResponseData.Meaning mean: dictionary.getMeanings()) {
                    meanings = meanings.concat(mean.getText()+", ");
                }
                meanings = meanings.substring(0,meanings.length()-2);
                meanings = meanings.concat(")");
                tv_dic_meanings.setText(meanings);
            }
            if (dictionary.getExamples() != null) {
                for (ResponseData.Example exmp: dictionary.getExamples()) {
                    example = example.concat(exmp.getExampleText()+" - ");
                    for (ResponseData.Example.ExampleTranslation exmptrans: exmp.getExampleTranslations()) {
                        example = example.concat(exmptrans.getText()+", ");
                    }
                    example = example.substring(0, example.length()-2);
                    Log.d("ml",example);
                    examples.add(example);
                }

                exampleAdapter = new ArrayAdapter(getActivity(),R.layout.list_item_dictionary_example,examples);
                lv_dic_exampes.setAdapter(exampleAdapter);
            }
        }
    }

    // Адаптер для Recycle View в словарной статье
    public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryHolder> {

        private List<DictionaryArticleItem> dictionaryItems;

        public DictionaryAdapter(List<DictionaryArticleItem> dictionaryItems) {
            this.dictionaryItems = dictionaryItems;
        }

        @Override
        public DictionaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_dictionary_article, parent,false);

            return new DictionaryHolder(view);
        }

        @Override
        public void onBindViewHolder(final DictionaryHolder holder, final int position) {

            final DictionaryArticleItem dictionaryItem = dictionaryItems.get(position);
            holder.bindDictionaryArticle(dictionaryItem);
        }

        @Override
        public int getItemCount() {
            return dictionaryItems.size();
        }

    }

}

