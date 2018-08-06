package com.satellite.svgmap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MapView extends View {

    private Context context;
    private float scale = 1.0f;
    private RectF totalRect;
    private int[] colorArray = new int[]{0xFF239BD7, 0xFF30A9E5, 0xFF80CBF1, 0xFFFFFFFF};

    private List<CityItem> cityItemList;

    private CityItem selectedCity;
    private Paint paint;
    private MyHandler handler;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handler = new MyHandler(this);
        // 开启线程
        loadThread.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //        获取到当前控件宽高值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // map 的宽度  和高度
        if (totalRect != null) { // TODO 由于线程没有执行完毕，此处不会进入
            double mapWidth = totalRect.width();
            scale= (float) (width / mapWidth);
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height , MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (cityItemList != null) {
            canvas.save();
            canvas.scale(scale,scale);
            for (CityItem cityItem : cityItemList) {
                if (cityItem != selectedCity) {
                    cityItem.drawItem(canvas, paint, false);
                }
            }
            if (selectedCity != null) {
                selectedCity.drawItem(canvas, paint, true);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    private void handleTouch(float x, float y) {
        if (cityItemList == null) {
            return;
        }
        CityItem selectItem = null;
        for (CityItem cityItem : cityItemList) {
            if(cityItem.isTouch(x/scale,y/scale)){
                selectItem = cityItem;
            }
        }
        if (selectItem != null) {
            selectedCity = selectItem;
            Log.d("@@@", "你选中了" + selectedCity.getTitle());
            postInvalidate();
        }
    }

    private Thread loadThread = new Thread(new Runnable() {
        @Override
        public void run() {
            parseSvg();
        }
    });

    private void parseSvg(){
        InputStream inputStream = context.getResources().openRawResource(R.raw.taiwanhigh);
        List<CityItem> cityItems = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            Element rootElement = document.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("path");
            // 地图的矩形范围
            float left = -1;
            float right = -1;
            float top = -1;
            float bottom = -1;
            for (int i = 0; i < items.getLength(); i++) {
                Element element = (Element) items.item(i);
                String pathData = element.getAttribute("d");
                Path path = PathParser.createPathFromPathData(pathData);
                String title = element.getAttribute("title");
                CityItem cityItem = new CityItem(path, title);
                cityItems.add(cityItem);
                // 获取宽高
                RectF rect = new RectF();
                path.computeBounds(rect, true);
                left = left == -1 ? rect.left : Math.min(left, rect.left);
                right = right == -1 ? rect.right : Math.max(right, rect.right);
                top = top == -1 ? rect.top : Math.min(top, rect.top);
                bottom = bottom == -1 ? rect.bottom : Math.max(bottom, rect.bottom);
            }
            totalRect = new RectF(left, top, right, bottom);

            cityItemList = cityItems;
            handler.sendEmptyMessage(1);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class MyHandler extends Handler{

        private WeakReference<MapView> mapView;

        public MyHandler(MapView reference) {
            this.mapView = new WeakReference<MapView>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mapView.get() == null) {
                return;
            }
            if (mapView.get().cityItemList == null) {
                return;
            }

            if (mapView.get().totalRect != null) {
                int width = mapView.get().getMeasuredWidth();
                double mapWidth = mapView.get().totalRect.width();
                float wScale = (float) (width / mapWidth);

                int height = mapView.get().getMeasuredHeight();
                double mapHeight = mapView.get().totalRect.height();
                float hScale = (float) (height / mapHeight);

                mapView.get().scale = Math.min(wScale, hScale);
            }

            int totalNumber = mapView.get().cityItemList.size();
            for (int i = 0; i < totalNumber; i++) {
                int color = Color.WHITE;
                int flag = i % 4;
                switch (flag) {
                    case 1:
                        color = mapView.get().colorArray[0];
                        break;
                    case 2:
                        color = mapView.get().colorArray[1];
                        break;
                    case 3:
                        color = mapView.get().colorArray[2];
                        break;
                    default:
                        color = Color.CYAN;
                        break;
                }
                mapView.get().cityItemList.get(i).setDrawColor(color);
            }
            mapView.get().postInvalidate();
        }
    }


}
