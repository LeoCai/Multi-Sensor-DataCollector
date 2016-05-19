package com.leocai.detecdtion.core;

import java.util.List;

/**
 * Created by leocai on 16-5-18.
 */
public class ExcurtionsWithIndexes {

    private int[] indexes;
    private int[] excurtions;

    public ExcurtionsWithIndexes(List<Byte> excutions, List<Integer> indexes) {
        int len = indexes.size();
        this.indexes = new int[len];
        for (int i = 0; i < len; i++) {
            this.indexes[i] = indexes.get(i);
        }
    }

    public int[] getIndexes() {
        return indexes;
    }

    public void setIndexes(int[] indexes) {
        this.indexes = indexes;
    }

    public int[] getExcurtions() {
        return excurtions;
    }

    public void setExcurtions(int[] excurtions) {
        this.excurtions = excurtions;
    }
}
