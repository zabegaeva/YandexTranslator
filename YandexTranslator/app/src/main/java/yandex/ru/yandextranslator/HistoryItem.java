package yandex.ru.yandextranslator;


/**
 * Created by Afina on 16.04.2017.
 */
public class HistoryItem {

    private int id;
    private String input;
    private String langFrom;

    private String langTo;
    private String translate;
    private short favorite;

    public HistoryItem(int id, String input, String langFrom, String langTo, String translate, short favorite) {
        this.id = id;
        this.input = input;
        this.langFrom = langFrom;
        this.langTo = langTo;
        this.translate = translate;
        this.favorite = favorite;
    }

    public int getId() {return id;}

    public String getInput() {return input;}

    public String getLangFrom() {return langFrom;}

    public String getLangTo() {return langTo;}

    public String getTranslate() {return translate;}

    public int getFavorite() {return favorite;}

    public boolean isFavorite() {
        if (favorite == 1) {
             return true;
        }else return false;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setLangFrom(String langFrom) {this.langFrom = langFrom;}

    public void setLangTo(String langTo) {this.langTo = langTo;}

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public void setFavorite( boolean favorite) {
        if (favorite) {
            this.favorite = 1;
        } else this.favorite = 0;
    }


}
