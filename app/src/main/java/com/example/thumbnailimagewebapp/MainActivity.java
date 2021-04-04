package com.example.thumbnailimagewebapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GridView gridView;


    List<Bitmap> list_image = new ArrayList<Bitmap>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        thumbnail_image_view = (RecyclerView) findViewById(R.id.list_thumbnail_image);
//        src = findViewById(R.id.src);
//        status  = findViewById(R.id.status);
//        btn_clear = findViewById(R.id.btn_clear);
//        btn_getImage = findViewById(R.id.btn_get_url);

//
//        btn_clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                src.setText("");
//            }
//        });

//        btn_getImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String url = src.getText().toString();
//                if (url != ""){
//                    getListImageFromWeb(url, status);
//                }
//                else {
//                    status.setText("You must be enter src field");
//                }
//            }
//        });
        String url = "https://www.pinterest.com/hochstetetter/dogo-memes/";
        try {
            getListImageFromWeb(url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void getListImageFromWeb(String url) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Document document = (Document) Jsoup.connect(url).get();
                    Elements images = document.select("img");
                    System.out.println("image size:" + images.size());
                    for (Element image : images){
                        Bitmap bmget = getBitmapFromSrc(image.attr("src"));
                        if (bmget != null){
                            list_image.add(bmget);
                        }
                    }
                    System.out.println("gengerate list image complete");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        thread.join();
        ShowImage();
    }
    protected Bitmap getBitmapFromSrc(String src){
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap mybitmap = BitmapFactory.decodeStream(inputStream);
            return mybitmap;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    // this function show some images in to screen
    protected void ShowImage(){

        gridView = findViewById(R.id.gridView);

        CustomAdapter customAdapter = new CustomAdapter();
        gridView.setAdapter(customAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ImageDetail.class);
                intent.putExtra("image", list_image.get(i));
                startActivity(intent);
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return  list_image.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.image_view, null);
            ImageView image = view1.findViewById(R.id.image_view);
            image.setImageBitmap(list_image.get(i));
            return view1;
        }
    }
}