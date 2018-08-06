package com.satellite.svgmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

public class CityItem {
    /**
     * 该城市的svg路径坐标。
     */
    private Path path;
    /**
     *
     */
    private int drawColor;
    private String title;

    public CityItem(Path path, String title) {
        this.path = path;
        this.title = title;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    public String getTitle() {
        return title;
    }

    public void drawItem(Canvas canvas, Paint paint, boolean isSelected){
        if (isSelected){
            // 选中时，绘制描边效果
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            // 填充
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(drawColor);
            canvas.drawPath(path, paint);
            // 描边
            paint.setStyle(Paint.Style.STROKE);
            int strokeColor = 0xFFD0E8F4;
            paint.setColor(strokeColor);
            canvas.drawPath(path, paint);
        } else {
            // 设置边界阴影效果
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8, 0, 0, 0xffffffff);
            canvas.drawPath(path, paint);
            // 填充
            paint.clearShadowLayer();
            paint.setColor(drawColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 返回(x,y)是否在path内部。
     * @param x
     * @param y
     * @return
     */
    public boolean isTouch(float x, float y){
        if (path != null){
            RectF rectF = new RectF();
            path.computeBounds(rectF, true);
            Log.d("@@@", "x = "+x+",y = "+y+" rect:"+rectF.left+","+rectF.top+","+rectF.right+","+rectF.bottom);

            Region clipRegion = new Region((int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom);
            // path转换成Region对象
            Region region = new Region();
            region.setPath(path, clipRegion);
            return region.contains((int)x, (int)y);
        }
        return false;
    }
}
