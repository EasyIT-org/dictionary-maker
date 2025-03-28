package org.easyit.dictmaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WordCard {
    private String name;
    private Object trans;
    private String usphone;
    private String ukphone;
    private String pron;
    private String example;
    private transient boolean skip = true;

    public WordCard(String name) {
        this.name = name;
        this.trans = new ArrayList<>();
        this.usphone = "";
        this.ukphone = "";
        this.pron = "";
    }

    public WordCard() {
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(final boolean skip) {
        this.skip = skip;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(final String pron) {
        this.pron = pron;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getTrans() {
        return trans;
    }

    public void setTrans(final Object trans) {
        this.trans = trans;
    }

    public String getUsphone() {
        return usphone;
    }

    public void setUsphone(String usphone) {
        this.usphone = usphone;
    }

    public String getUkphone() {
        return ukphone;
    }

    public void setUkphone(String ukphone) {
        this.ukphone = ukphone;
    }

    public String getExample() {
        return example;
    }

    public void setExample(final String example) {
        this.example = example;
    }

    public WordCard clone() {
        WordCard wordCard = new WordCard(name);
        wordCard.setTrans(trans);
        wordCard.setUsphone(usphone);
        wordCard.setUkphone(ukphone);
        wordCard.setPron(pron);
        wordCard.setExample(example);
        return wordCard;
    }

    @Override
    public String toString() {
        return "Entry{" +
            "name='" + name + '\'' +
            ", trans=" + trans +
            ", usphone='" + usphone + '\'' +
            ", ukphone='" + ukphone + '\'' +
            '}';
    }
}

