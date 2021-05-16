package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsActivity extends AppCompatActivity {
    @BindView(R.id.terms_back)
    public ImageView termsBack;
    @BindView(R.id.myWebView)
    public WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);

        String url = "https://coupinapp.com/terms.html";
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(url);

        termsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
