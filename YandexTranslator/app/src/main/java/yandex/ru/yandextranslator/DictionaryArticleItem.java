package yandex.ru.yandextranslator;

import java.util.ArrayList;

/**
 * Created by Afina on 24.04.2017.
 */
public class DictionaryArticleItem {
    Integer number;
    String translate;
    ArrayList<ResponseData.Synonym> synonyms;
    ArrayList<ResponseData.Meaning> meanings;
    ArrayList<ResponseData.Example> examples;

    public Integer getNumber() {
        return number;
    }

    public String getTranslate() {
        return translate;
    }

    public ArrayList<ResponseData.Synonym> getSynonyms() {
        return synonyms;
    }

    public ArrayList<ResponseData.Meaning> getMeanings() {
        return meanings;
    }

    public ArrayList<ResponseData.Example> getExamples() {
        return examples;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public void setSynonyms(ArrayList<ResponseData.Synonym> synonyms) {
        this.synonyms = synonyms;
    }

    public void setMeanings(ArrayList<ResponseData.Meaning> meanings) {
        this.meanings = meanings;
    }

    public void setExamples(ArrayList<ResponseData.Example> examples) {
        this.examples = examples;
    }
}
