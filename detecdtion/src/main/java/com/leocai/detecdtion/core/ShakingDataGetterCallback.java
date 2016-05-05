package com.leocai.detecdtion.core;

import com.leocai.publiclibs.ShakingData;

import java.util.List;

/**
 * Created by leocai on 16-1-11.
 */
public interface ShakingDataGetterCallback {
    void onGetShakingDatas(List<ShakingData> shakingDatas);
}
