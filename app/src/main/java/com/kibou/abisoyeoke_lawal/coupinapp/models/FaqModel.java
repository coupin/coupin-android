package com.kibou.abisoyeoke_lawal.coupinapp.models;

import java.io.Serializable;

/**
 * Created by abisoyeoke-lawal on 11/18/17.
 */

public class FaqModel implements Serializable {
    public boolean isOpen = false;
    public String title;
    public String detail;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
