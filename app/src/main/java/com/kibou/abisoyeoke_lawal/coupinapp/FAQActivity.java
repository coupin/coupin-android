package com.kibou.abisoyeoke_lawal.coupinapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.RVFaqAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.models.FaqModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FAQActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.faq_back)
    public ImageView faqBack;
    @BindView(R.id.faq_list)
    public RecyclerView rvFaq;

    public ArrayList<FaqModel> faqs = new ArrayList<>();
    public RVFaqAdapter rvFaqAdapter;

    String faqTitles[] = new String[]{"How do I find?", "My Coupin is not answering me"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFaqAdapter = new RVFaqAdapter(faqs, this);
        rvFaq.setLayoutManager(linearLayoutManager);
        rvFaq.setHasFixedSize(true);
        rvFaq.setAdapter(rvFaqAdapter);

        for(int x = 0; x < faqTitles.length; x++) {
            FaqModel model = new FaqModel();
            model.setTitle(faqTitles[x]);
            model.setDetail(getResources().getString(R.string.example_description));
            faqs.add(model);
        }

        rvFaqAdapter.notifyDataSetChanged();

        faqBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.faq_back:
                onBackPressed();
                break;
        }
    }
}
