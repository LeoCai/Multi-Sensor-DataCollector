package com.leocai.detecdtion.core;

import java.util.List;

/**
 * Created by leocai on 16-1-6.
 */
public interface ReconcilationEndCallBack {
    void onReconcilationEnd(List<Byte> bitsList, double mismatchRate);
}
