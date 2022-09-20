package net.sushiclient.client.modules;

import com.google.gson.annotations.SerializedName;

import java.awt.*;

public class GsonCategory implements Category {

    @SerializedName("name")
    private String name;

    public GsonCategory() {
    }

    public GsonCategory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getIcon() {
        return null;
    }
}
