package com.liangchao.camera2glcodec.filter;

import android.content.Context;

import com.liangchao.camera2glcodec.R;


public class ScreenFilter extends BaseFilter {
    public ScreenFilter(Context mContext) {
        super(mContext, R.raw.screen_vert, R.raw.screen_frag);
    }
}
