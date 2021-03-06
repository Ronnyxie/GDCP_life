package com.example.ronny_xie.gdcp.Fragment.jw2012SyetemFragment;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.ronny_xie.gdcp.Fragment.jw2012SyetemFragment.bean.NewsItem;
import com.example.ronny_xie.gdcp.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by WYF on 2017/7/18.
 */

public class LoadGDCPData {
    private static final String TAG = "LoadGDCPData";
    private Context ctx;
    private Handler handler = new Handler();
    private ArrayList<String> imageUrl = new ArrayList<>();
    private ArrayList<String> imageclickurl = new ArrayList<>();
    private int firstLength = 0;
    private ArrayList<String> urlList = new ArrayList<>();
    private ArrayList<NewsItem> newsItems = new ArrayList<>();
    private View headerView;

    public LoadGDCPData(Context ctx) {
        this.ctx = ctx;
    }

    public ListView initScriptViewPager(Document doc, final ListView listView, int eq, int endIndex, String header) {
        imageUrl = new ArrayList<>();
        newsItems = new ArrayList<>();
        Elements script = doc.getElementsByTag("script").eq(eq);
        String text = script.get(0).data().toString();
        int index = 0;
        for(int i = 0; i < endIndex; i++){
            int index1 = text.indexOf("\"");
            int index2 = text.indexOf("\"", index1+1);
            String result = text.substring(index1+1, index2);
            if (index%3 == 0){
                imageUrl.add(header + result);
            }
            text = text.substring(index2+1);
            index++;
        }
        return fillViewPager(listView);
    }

    public ListView initInputViewPager(Document doc, final ListView listView, int index, String header) {
        imageUrl = new ArrayList<>();
        newsItems = new ArrayList<>();
        Elements select = doc.select("input[type=hidden]");
        for (int i = 0; i < index; i++) {
            if (i % 3 == 0) {
                imageUrl.add(header + select.get(i).attr("value"));
            }
        }
        return fillViewPager(listView);
    }

    public ArrayList<NewsItem> fillItemImg(String header){
        Document doc;
        for (NewsItem ni : newsItems){
            try {
                doc = Jsoup.parse(new URL(ni.getUrl()), 3000);
                Element element = doc.getElementsByTag("img").last();
                if (element != null){
                    ni.setImg(header + element.attr("src"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newsItems;
    }

    public void initNewsData(Document doc, String tag, int fromIndex, int endIndex){
        Elements elements = doc.select(tag);
        for (int i = fromIndex; i < endIndex; i++) {
            NewsItem newsItem = new NewsItem();
            String title = elements.get(i).attr("title");
            String url = elements.get(i).attr("href");
            newsItem.setTitle(title);
            newsItem.setUrl(url);
            newsItems.add(newsItem);
        }
        for (NewsItem ni : newsItems){
            try {
                doc = Jsoup.parse(new URL(ni.getUrl()), 3000);
                String text = doc.body().text();
                int index1 = text.indexOf("日期");
                String date = text.substring(index1+3, index1 + 11).replace("】", "");
                int index2 = text.indexOf("【作者");
                int index3 = text.indexOf("】", index2);
                String source = text.substring(index2+7, index3);
                ni.setDate(date);
                ni.setSource(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ListView fillViewPager(final ListView listView){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (listView.getHeaderViewsCount() > 0){
                    return;
                }
                headerView = LayoutInflater.from(ctx).inflate(R.layout.list_view_header, listView, false);
                SliderLayout sliderLayout = (SliderLayout) headerView.findViewById(R.id.slider_layout);
                for (int i = 0; i < imageUrl.size(); i++) {
                    TextSliderView textSliderView = new TextSliderView(ctx);
                    textSliderView.image(imageUrl.get(i));
                    sliderLayout.addSlider(textSliderView);
                }
                listView.addHeaderView(headerView);
            }
        });
        return listView;
    }

    // 拿到首页vp图片和点击地址
    public void initHomeImage(Document doc) {
        if (doc != null) {
            imageUrl = new ArrayList<>();
            newsItems = new ArrayList<>();
            Element elementById2 = doc.getElementById("header");
            Elements elementsByTag2 = elementById2.getElementsByTag("img");
            String src = elementsByTag2.get(0).attr("src");
            Elements elementsByTag = elementById2.getElementsByTag("a");
            String href = elementsByTag.get(0).attr("href");
            imageUrl.add(src + "\"");
            imageclickurl.add(href + "\"");
            // 拿到script里面的图片和点击地址
            Element elementById = doc.getElementById("header");
            Elements TagScript = elementById.getElementsByTag("script").eq(1);
            Log.e(TAG, "initHomeImage: TagScript"+TagScript.toString() );
            for (Element element : TagScript) {
                String[] data = element.data().toString().split("var");
                String[] dataTem = new String[2];
                dataTem = getHomeData(data[1]);
                imageUrl.add(dataTem[0]);
                dataTem = getHomeData(dataTem[1]);
                imageclickurl.add(dataTem[0]);
                dataTem = getHomeData(dataTem[1]);
                imageUrl.add(dataTem[0]);
                dataTem = getHomeData(dataTem[1]);
                imageclickurl.add(dataTem[0]);
                dataTem = getHomeData(dataTem[1]);
                imageUrl.add(dataTem[0]);
                dataTem = getHomeData(dataTem[1]);
                imageclickurl.add(dataTem[0]);
                dataTem = getHomeData(dataTem[1]);
                imageUrl.add(dataTem[0]);
                dataTem = getHomeData(dataTem[1]);
                imageclickurl.add(dataTem[0]);
                dataTem = getHomeData(dataTem[1]);
                imageUrl.add(dataTem[0]);
                Log.e(TAG, "initHomeImage: dataTem"+dataTem[1]);
                dataTem = getHomeData(dataTem[1]);
                imageclickurl.add(dataTem[0]);
            }
        }
    }

    // 拿到script里面的网址
    public String[] getHomeData(String data) {
        String[] arr = new String[2];
        String sub_attr_last = data.substring(data.indexOf("attr"));
        Log.e(TAG, "getHomeData:   sub_attr_last"+sub_attr_last);
        String getData = sub_attr_last.substring(sub_attr_last.indexOf("/"),
                sub_attr_last.indexOf(")"));
        Log.e(TAG, "getHomeData:getData +"+getData);
        String sub_fenhao_last = sub_attr_last.substring(sub_attr_last
                .indexOf(";"));
        arr[0] = getData;
        arr[1] = sub_fenhao_last;
        return arr;
    }

    public void initHomeViewPager(final ListView listView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (listView.getHeaderViewsCount() > 0){
                    return;
                }
                headerView = LayoutInflater.from(ctx).inflate(R.layout.list_view_header, listView, false);
                SliderLayout sliderLayout = (SliderLayout) headerView.findViewById(R.id.slider_layout);
                for (int i = 0; i < imageUrl.size(); i++) {
                    if (i == 0) {
                        TextSliderView textSliderView = new TextSliderView(ctx);
                        textSliderView.image(R.drawable.qhgj);
                        sliderLayout.addSlider(textSliderView);
                        continue;
                    }
                    String substring = imageUrl.get(i).substring(0,
                            imageUrl.get(i).length() - 1);
                    TextSliderView textSliderView = new TextSliderView(ctx);
                    textSliderView.image("http://a1.gdcp.cn" + substring);
                    sliderLayout.addSlider(textSliderView);
                }
                listView.addHeaderView(headerView);
            }
        });
    }

    // 获取广交官网首页的网页新闻信息
    public ArrayList<NewsItem> initHomeNewsData(Document doc) {
        // --------------------------拿到广交新闻里面的标题和地址
        firstLength = parseHomeData("jyxw_wz", 0, doc);

        // --------------------------拿到媒体聚焦里面的标题和地址
        parseHomeData("mtjj_wz", firstLength, doc);
        for (int i = 0; i < urlList.size(); i++) {
            newsItems.get(i).setUrl(urlList.get(i));
        }
        // 拿到item图片地址
        initHomeItemData();
        return newsItems;
    }

    //解析广交官网首页的网页新闻数据封装
    private int parseHomeData(String name, int index, Document doc) {
        Elements elementsByClass = doc.getElementsByClass(name);
        Element element = elementsByClass.get(0);
        Elements elementsByTag = element.getElementsByTag("td");
        for (Element elementtd : elementsByTag) {
            if (elementtd.text() != null) {
                if (!elementtd.text().toString().equals("")) {
                    NewsItem newsItem = new NewsItem();
                    newsItem.setTitle(elementtd.text().toString().trim());
                    newsItems.add(newsItem);
                }
            }
            if (elementtd.getElementsByTag("a") != null) {
                if (!elementtd.getElementsByTag("a").attr("href").equals("")) {
                    urlList.add(elementtd.getElementsByTag("a").attr("href"));
                }
            }
        }
        return newsItems.size();
    }

    private void initHomeItemData() {
        for (NewsItem ni : newsItems) {
            URL url = null;
            try {
                url = new URL(ni.getUrl());
                Document doc = Jsoup.parse(url, 3000);
                Elements wzbjxx = doc.getElementsByClass("wzbjxx");
                Elements select = doc.select("img[onClick]");
                if (select.size() > 0) {
                    ni.setImg("http://a1.gdcp.cn" + select.get(0).attr("src"));
                }
                String data = wzbjxx.get(0).text().toString().trim();
                splitHomeData(ni, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //对新闻日期和来源进行封装
    private void splitHomeData(NewsItem ni, String s) {
        String[] split = s.split("    ");
        String s1;
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                s1 = split[i];
                ni.setDate(s1.substring(5));
            } else if (i == 1) {
                s1 = split[i];
                ni.setSource(s1.substring(4));
            }
        }
    }
}
