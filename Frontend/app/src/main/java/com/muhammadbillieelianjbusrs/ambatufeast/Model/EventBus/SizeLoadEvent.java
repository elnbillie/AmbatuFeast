package com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus;

import com.muhammadbillieelianjbusrs.ambatufeast.Model.Size;

import java.util.List;

public class SizeLoadEvent {
    private boolean succces;
    private List<Size> sizeList;

    public SizeLoadEvent(boolean succces, List<Size> sizeList) {
        this.succces = succces;
        this.sizeList = sizeList;
    }

    public boolean isSuccces() {
        return succces;
    }

    public void setSuccces(boolean succces) {
        this.succces = succces;
    }

    public List<Size> getSizeList() {
        return sizeList;
    }

    public void setSizeList(List<Size> sizeList) {
        this.sizeList = sizeList;
    }
}
