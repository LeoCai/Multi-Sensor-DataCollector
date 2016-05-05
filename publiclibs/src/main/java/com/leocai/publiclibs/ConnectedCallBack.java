package com.leocai.publiclibs;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by leocai on 16-1-10.
 */
public interface ConnectedCallBack {
    void onConnected(InputStream out);
}
