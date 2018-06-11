package com.example.druo.lotto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.druo.lotto.R;
import com.example.druo.lotto.TicketType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_five)
    CardView btnFive;
    @BindView(R.id.btn_six)
    CardView btnSix;
    @BindView(R.id.btn_skandi)
    CardView btnSkandi;
    @BindView(R.id.btn_sent_tickets)
    CardView btnSentTickets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



    }


    @OnClick({R.id.btn_five, R.id.btn_six, R.id.btn_skandi})
    public void startPickActivity(View view) {
        Intent intent = new Intent(this, PickActivity.class);
        switch (view.getId()) {
            case R.id.btn_five:
                intent.putExtra("type", TicketType.FIVE);
                break;
            case R.id.btn_six:
                intent.putExtra("type", TicketType.SIX);
                break;
            case R.id.btn_skandi:
                intent.putExtra("type", TicketType.SKANDINAV);
                break;
        }
        startActivity(intent);
    }


    @OnClick(R.id.btn_sent_tickets)
    public void startSentTicketsActivity(View view) {
        Intent intent = new Intent(this, SentTickets.class);
        startActivity(intent);

    }

}
