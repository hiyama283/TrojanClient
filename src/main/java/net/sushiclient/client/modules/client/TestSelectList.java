package net.sushiclient.client.modules.client;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.Named;

public enum TestSelectList implements Named {
    @SerializedName("X")
    X("X"),
    @SerializedName("Y")
    Y("Y"),
    @SerializedName("Z")
    Z("Z");
    private final String name;
    TestSelectList(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
