package com.kibou.abisoyeoke_lawal.coupinapp.models;

import java.io.Serializable;

/**
 * Created by abisoyeoke-lawal on 9/10/17.
 */

public class Interest implements Serializable {
    public boolean selected;
    public int icon;
    public String label;
    public String value;

    public boolean isSelected() {
        return selected;
    }

    public int getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
