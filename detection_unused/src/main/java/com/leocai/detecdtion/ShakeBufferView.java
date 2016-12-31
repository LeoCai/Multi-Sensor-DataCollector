package com.leocai.detecdtion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.leocai.publiclibs.ShakingData;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by leocai on 15-12-22.
 */
public class ShakeBufferView extends View implements Observer {


    private MySensorListener mySensorListener;
    private Paint paint = new Paint();
    private int index;
    private List<Double> buffer = new ArrayList<>();

    public ShakeBufferView(Context context) {
        super(context);
    }

    public ShakeBufferView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShakeBufferView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMySensorListener(MySensorListener mySensorListener) {
        this.mySensorListener = mySensorListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float totalHeight = getHeight();
        float totalWidth = getWidth();

        if(buffer==null) return;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (double b : buffer) {
            if (max < b) max = b;
            if (min > b) min = b;
        }

        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
//        List<Double> buffer = new ArrayList<>();
//        buffer.add(3d);
//        buffer.add(5d);
//        buffer.add(0.2d);
//        canvas.drawPoint(20, 100, paint);
        float scaleWidth = 0.9f * totalWidth / buffer.size();
        float scaleHeight = (float) (0.5 * totalHeight / (max));
        float offY = totalHeight / 1.5f;
        for (int i = 1; i < buffer.size(); i++) {
            float startX = (i - 1) * scaleWidth,
                    startY = offY - (float) (buffer.get(i - 1) * scaleHeight),
                    endX = i * scaleWidth,
                    endY = offY - (float) (buffer.get(i) * scaleHeight);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
//        for (double point : buffer) {
//            canvas.drawPoint(index * 20, (float) point * 100, paint);
//            index++;
//        }
    }

    @Override
    public void update(Observable observable, Object data) {
        List<ShakingData> datas = (List<ShakingData>) data;
        if(datas==null) return;
        for(ShakingData shakingData:datas){
            buffer.add(shakingData.getResultantAccData());
        }
        postInvalidate();
    }
}
