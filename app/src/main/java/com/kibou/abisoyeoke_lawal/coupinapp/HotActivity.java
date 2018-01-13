package com.kibou.abisoyeoke_lawal.coupinapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HotActivity extends AppCompatActivity {
    @BindView(R.id.hot_carousel)
    public CarouselView hotCarousel;
    @BindView(R.id.hot_recyclerview)
    public RecyclerView hotRecyclerView;

    public RequestQueue requestQueue;

    public int[] images = new int[]{R.drawable.food1, R.drawable.food2, R.drawable.food3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);
        ButterKnife.bind(this);

        requestQueue = Volley.newRequestQueue(this);

        hotCarousel.setPageCount(images.length);
        hotCarousel.setImageListener(imageListener);

        getHotList();
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(images[position]);
        }
    };

    public void getHotList() {
        String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_get_hot);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("VolleyResponse", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
            }
        });

        requestQueue.add(stringRequest);
    }
}
