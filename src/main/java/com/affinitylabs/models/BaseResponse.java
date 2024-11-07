package com.affinitylabs.models;

import java.util.ArrayList;


public class BaseResponse {
    public String title;
    public ArrayList<String> options;
    public Actions actions;
    boolean backOption;


    public BaseResponse(String title, Actions actions) {
        this.title = title;
        this.actions = actions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public boolean isBackOption() {
        return backOption;
    }

    public void setBackOption(boolean backOption) {
        this.backOption = backOption;
    }

}

