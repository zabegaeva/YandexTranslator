package yandex.ru.yandextranslator;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Afina on 11.04.2017.
 */
public class ResponseData {
    class Translate {
        private int code;
        private String lang;
        private ArrayList<String> text;

        public int getCode() {
            return code;
        }

        public String getLang() {
            return lang;
        }

        public ArrayList<String> getText() {
            return text;
        }
    }

    class LanguagesList {

        Map<String,String> langs;

        public Map<String,String> getLanguageMap() {
            return langs;
        }
    }

    //класс для работы с json Яндекс Словаря
    class Dictionary {
        Head head;
        List<AtributesDef> def = new ArrayList<>();

        public Head getHead() {
            return head;
        }

        public void setHead(Head head) {
            this.head = head;
        }

        public List<AtributesDef> getDef() {
            return def;
        }

        public void setDef(List<AtributesDef> def) {
            this.def = def;
        }
    }
    public class AtributesDef {
        String text;
        @SerializedName("gen")
        String genus;
        @SerializedName("pos")
        String partOfSpeach;
        @SerializedName("tr")
        List<DictionaryTranslation> translations = new ArrayList<>();

        public String getText() {
            return text;
        }

        public String getGenus() {
            return genus;
        }

        public void setGenus(String genus){
            this.genus = genus;
        }

        public String getPartOfSpeach() {
            return partOfSpeach;
        }

        public void setPartOfSpeach(String partOfSpeach) {
            this.partOfSpeach = partOfSpeach;
        }

        public List<DictionaryTranslation> getTranslations() {
            return translations;
        }

        public void setTranslations(List<DictionaryTranslation> translations) {
            this.translations = translations;
        }
    }

    public class DictionaryTranslation {
        String text;
        @SerializedName("pos")
        String partOfSpeach;
        @SerializedName("syn")
        ArrayList<Synonym> synonyms = new ArrayList<>();
        @SerializedName("mean")
        ArrayList<Meaning> meanings = new ArrayList<>();
        @SerializedName("ex")
        ArrayList<Example> examples = new ArrayList<>();

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPartOfSpeach() {
            return partOfSpeach;
        }

        public void setPartOfSpeach(String partOfSpeach) {
            this.partOfSpeach = partOfSpeach;
        }

        public ArrayList<Synonym> getSynonyms() {
            return synonyms;
        }

        public void setSynonyms(ArrayList<Synonym> synonyms) {
            this.synonyms = synonyms;
        }

        public ArrayList<Meaning> getMeanings() {
            return meanings;
        }

        public void setMeanings(ArrayList<Meaning> meanings) {
            this.meanings = meanings;
        }

        public ArrayList<Example> getExamples() {
            return examples;
        }

        public void setExamples(ArrayList<Example> examples) {
            this.examples = examples;
        }

    }

    public class Head {
    }

    public class Synonym {
        String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

    public class Meaning {
        String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public class Example {
        @SerializedName("text")
        String exampleText;
        @SerializedName("tr")
        ArrayList<ExampleTranslation> exampleTranslations;

        public String getExampleText() {
            return exampleText;
        }

        public void setExampleText(String exampleText) {
            this.exampleText = exampleText;
        }

        public ArrayList<ExampleTranslation> getExampleTranslations() {
            return exampleTranslations;
        }

        public void setExampleTranslations(ArrayList<ExampleTranslation> exampleTranslations) {
            this.exampleTranslations = exampleTranslations;
        }

        public class ExampleTranslation {
            String text;

            public void setText(String text) {
                this.text = text;
            }

            public String getText() {
                return text;
            }
        }
    }

}
