package yandex.ru.yandextranslator;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Afina on 11.04.2017.
 */
public class HistoryListFragment extends Fragment {

    private RecyclerView historyRecycleView;
    private TextView titleTextView;
    private HistoryAdapter adapter;
    LanguagesDBHelper dbh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list,container, false);

        historyRecycleView = (RecyclerView) view.findViewById(R.id.history_recycle_view);
        titleTextView = (TextView) view.findViewById(R.id.title_tv);

        historyRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (getArguments().getString(CONST.ARG_TYPE_FRAGMENT).equals(CONST.TAG_FAVORITE)) {
            titleTextView.setText(R.string.favorite);
        } else titleTextView.setText(R.string.history);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    // обновление списка истории или избранного
    private void updateUI() {
        List<HistoryItem> historyItems = getHistoryList(getFragmentManager().getBackStackEntryAt(0).getName());

        //установить в адаптер
        if (adapter == null) {
            adapter = new HistoryAdapter(historyItems);
            historyRecycleView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    //получения списка истории или избранного
    private List<HistoryItem> getHistoryList (String tag_fragment) {
        List<HistoryItem> historyItems = new ArrayList<>();
        dbh = new LanguagesDBHelper(getActivity());
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.query(CONST.TABLE_HISTORY,null,null,null,null,null,null,null);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex(CONST.COLUMN_ID);
            int inputIndex = c.getColumnIndex(CONST.COLUMN_INPUT);
            int langFromIndex = c.getColumnIndex(CONST.COLUMN_LANG_FROM);
            int langToIndex = c.getColumnIndex(CONST.COLUMN_LANG_TO);
            int translateIndex = c.getColumnIndex(CONST.COLUMN_TRANSLATE);
            int favoriteIndex = c.getColumnIndex(CONST.COLUMN_FAVORITE);
            do {
                if (tag_fragment.equals(CONST.TAG_FAVORITE)) {
                    if (c.getShort(favoriteIndex)== 1) {
                        historyItems.add( new HistoryItem(c.getInt(idIndex),c.getString(inputIndex),c.getString(langFromIndex),c.getString(langToIndex),c.getString(translateIndex),c.getShort(favoriteIndex)));
                }
                } else {
                    historyItems.add( new HistoryItem(c.getInt(idIndex),c.getString(inputIndex),c.getString(langFromIndex),c.getString(langToIndex),c.getString(translateIndex),c.getShort(favoriteIndex)));
                }
            } while (c.moveToNext());
        }
        //перевернуть arraylist
        for (int i = 0; i < (historyItems.size() / 2); i++) {
            HistoryItem tmp = historyItems.get(i);
            historyItems.set(i, historyItems.get(historyItems.size()-i-1));
            historyItems.set((historyItems.size()-i-1), tmp);
        }
        c.close();
        dbh.close();

        return historyItems;
    }

    private class HistoryHolder extends RecyclerView.ViewHolder {

        private TextView tv_input;
        private TextView tv_lang;
        private TextView tv_translate;
        private CheckBox rb_favorite;
        RelativeLayout relativeLayout;

        private HistoryItem historyItem;

        public HistoryHolder(View itemView) {
            super(itemView);

            tv_input = (TextView) itemView.findViewById(R.id.list_item_history_input_tv);
            tv_lang = (TextView) itemView.findViewById(R.id.list_item_history_lang_tv);
            tv_translate = (TextView) itemView.findViewById(R.id.list_item_history_translate_tv);
            rb_favorite = (CheckBox) itemView.findViewById(R.id.list_item_history_favorite_cb);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.holger_history_item);

            rb_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    historyItem.setFavorite(isChecked);
                    dbh = new LanguagesDBHelper(getActivity());
                    SQLiteDatabase db = dbh.getWritableDatabase();
                    String strSQL = "UPDATE "+ CONST.TABLE_HISTORY+ " SET " + CONST.COLUMN_FAVORITE + " = "+ historyItem.getFavorite() +" WHERE "+ CONST.COLUMN_ID +" = "+ historyItem.getId();
                    db.execSQL(strSQL);
                    dbh.close();
                }
            });
        }

        public void bindHistory(HistoryItem history) {
            historyItem = history;
            tv_input.setText(historyItem.getInput());
            historyItem.setLangFrom(history.getLangFrom());
            historyItem.setLangTo(history.getLangTo());
            MyApplication myApp=(MyApplication) getActivity().getApplication();
            Map<String,String> langMap = new HashMap<String,String>(myApp.getLangMap());
            String langFromTo = langMap.get(history.getLangFrom())+"-"+langMap.get(history.getLangTo());
            tv_lang.setText(langFromTo);
            tv_translate.setText(historyItem.getTranslate());
            rb_favorite.setChecked(historyItem.isFavorite());
        }
    }

   public class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

        private List<HistoryItem> historyItems;

        public HistoryAdapter(List<HistoryItem> history) {
            historyItems = history;
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_history, parent,false);

            return new HistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(final HistoryHolder holder, final int position) {

            final HistoryItem historyItem = historyItems.get(position);
            holder.bindHistory(historyItem);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();

                    intent.putExtra(CONST.ARG_INPUT, holder.tv_input.getText().toString());
                    intent.putExtra(CONST.ARG_LANG_FROM, holder.historyItem.getLangFrom());
                    intent.putExtra(CONST.ARG_LANG_TO, holder.historyItem.getLangTo());
                    intent.putExtra(CONST.ARG_TRANSLATE, holder.tv_translate.getText().toString());

                    getActivity().setIntent(intent);

                    BottomNavigationView bottomNavigation = (BottomNavigationView)getActivity().findViewById(R.id.navigation);
                    bottomNavigation.findViewById(R.id.btn_main).performClick();
                }
            });
        }

        @Override
        public int getItemCount() {
            return historyItems.size();
        }

    }
}
