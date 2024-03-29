package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RVFaqAdapter;
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

        String[] faqTitles = getResources().getStringArray(R.array.faqs_title);
        String[] faqDetails = getResources().getStringArray(R.array.faqs_details);

        for(int x = 0; x < faqTitles.length; x++) {
            FaqModel model = new FaqModel();
            model.setTitle(faqTitles[x]);
            model.setDetail(faqDetails[x]);
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
