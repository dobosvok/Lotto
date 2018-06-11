package com.example.druo.lotto.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.druo.lotto.R;
import com.example.druo.lotto.SmsBroadcastReceiver;
import com.example.druo.lotto.TicketType;
import com.example.druo.lotto.database.DatabaseHelper;
import com.example.druo.lotto.views.SelectedNumbersGridView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.druo.lotto.utils.Utils.verifyPermissions;

public class SendSms extends AppCompatActivity {

    private static final int REQUEST_SMS = 1;
    private String OK_PHONE_NUMBER = "";
    private static final String[] PERMISSIONS_SMS = {Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
    };
    private static final String[] PERMISSIONS_PHONE_STATE_AND_SMS =
            {Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_PHONE_STATE
            };

    public static final String TAG = SendSms.class.getCanonicalName();
    @BindView(R.id.tv_numbers)
    TextView textView;
    @BindView(R.id.ll_selected_numbers)
    LinearLayout linearLayout;
    @BindView(R.id.ll_sms_body)
    LinearLayout smsBody;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fab_send_ok)
    FloatingActionButton fabOk;
    @BindView(R.id.fab_go_to_sent_tickets)
    FloatingActionButton fabSentTickets;

    private ArrayList<ArrayList<Integer>> allNumbers;
    private TicketType ticketType;
    private static final String PHONE_NUMBER = "1756";
    private static final String OK_PHONE_NUMBER_TELEKOM = "+36308100000";
    private static final String OK_PHONE_NUMBER_TELENOR = "+36207630000";
    private SmsManager sms = SmsManager.getDefault();
    private DatabaseHelper db;
    private boolean getResponse = false;
    private SmsBroadcastReceiver smsListener;
    final SmsBroadcastReceiver smsListenerFeedback = new SmsBroadcastReceiver(PHONE_NUMBER, "Elfogadva");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Összegzés");
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DatabaseHelper(getApplicationContext());

        TelephonyManager telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        assert telephonyManager != null;
        String carrierName = telephonyManager.getNetworkOperatorName();
        switch (carrierName) {
            case "Telekom HU":
                OK_PHONE_NUMBER = OK_PHONE_NUMBER_TELEKOM;
                smsListener = new SmsBroadcastReceiver(OK_PHONE_NUMBER_TELEKOM, "Küldjön");
                break;
            case "Telenor HU":
                OK_PHONE_NUMBER = OK_PHONE_NUMBER_TELENOR;
                smsListener = new SmsBroadcastReceiver(OK_PHONE_NUMBER_TELENOR, "Rendben");
                break;
            case "":
                break;
            default:
                OK_PHONE_NUMBER = OK_PHONE_NUMBER_TELEKOM;
                smsListener = new SmsBroadcastReceiver(OK_PHONE_NUMBER_TELEKOM, "Rendben");
                break;
        }

        registerReceiver(smsListener, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        registerReceiver(smsListenerFeedback, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

        Intent intent = getIntent();

        allNumbers = (ArrayList<ArrayList<Integer>>) intent.getSerializableExtra("allNumbers");
        ticketType = (TicketType) intent.getSerializableExtra("type");

        SelectedNumbersGridView selectedNumbersGridView = new SelectedNumbersGridView(this);
        int column = allNumbers.get(0).size();
        int row = allNumbers.size();

        selectedNumbersGridView.setNumColumns(column);
        selectedNumbersGridView.setNumRows(row);
        selectedNumbersGridView.setNumbers(allNumbers);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(metrics.widthPixels, metrics.widthPixels / column * row + 1));
        linearLayout.addView(selectedNumbersGridView);


        //String operatorName = telephonyManager.getSimOperatorName();
        //Log.d(TAG, carrierName + "   " + operatorName);


        smsListener.setListener(new SmsBroadcastReceiver.Listener() {
            @Override
            public void onTextReceived(String text) {
                Snackbar.make(smsBody, "A válasz SMS megérkezett erősítsd meg", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                getResponse = true;
                fab.setVisibility(View.INVISIBLE);
                fabOk.setVisibility(View.VISIBLE);

            }
        });


    }

    @OnClick(R.id.fab_send_ok)
    public void setSendOk() {
        sms.sendTextMessage(OK_PHONE_NUMBER, null, "ok", null, null);


        db.insertTicket(ticketType.toString(), allNumbers);

        Snackbar.make(smsBody, "Megerősítés elküldve", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        fabOk.setVisibility(View.INVISIBLE);

        smsListenerFeedback.setListener(new SmsBroadcastReceiver.Listener() {
            @Override
            public void onTextReceived(String text) {
                Snackbar.make(smsBody, "Szelvény elfogadva", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fabSentTickets.setVisibility(View.VISIBLE);
            }
        });
    }

    @OnClick(R.id.fab_go_to_sent_tickets)
    public void goToSentTickets() {
        Intent intent = new Intent(this, SentTickets.class);
        startActivity(intent);
    }


    public void buildSms() {
        if (fab.isEnabled()) {
            StringBuilder stringBuilder = new StringBuilder();

            switch (ticketType) {
                case FIVE:
                    stringBuilder.append("L5,");
                    break;
                case SIX:
                    stringBuilder.append("L6,");
                    break;
                case SKANDINAV:
                    stringBuilder.append("LS,");
                    break;
            }

            for (int i = 0; i < allNumbers.size(); i++) {
                ArrayList<Integer> numberLine = allNumbers.get(i);
                for (int j = 0; j < numberLine.size(); j++) {
                    int number = numberLine.get(j);
                    stringBuilder.append(number);
                    if (j < numberLine.size() - 1)
                        stringBuilder.append(",");
                }
                if (i != allNumbers.size() - 1)
                    stringBuilder.append(",,");
            }


            sms.sendTextMessage(PHONE_NUMBER, null, stringBuilder.toString(), null, null);


            Snackbar.make(smsBody, "SMS elküldve", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            fab.setVisibility(View.INVISIBLE);

            new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (!getResponse) {
                        fab.setVisibility(View.VISIBLE);
                        Snackbar.make(smsBody, "Az SMS-re nem jött válasz megpróbálhatod újra", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }.start();

        }


    }


    private void requestSmsPermissions() {

        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.SEND_SMS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECEIVE_SMS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {


            Snackbar.make(smsBody, R.string.permission_sms_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.permission_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(SendSms.this,
                                            Build.VERSION.SDK_INT <= Build.VERSION_CODES.O ? PERMISSIONS_PHONE_STATE_AND_SMS : PERMISSIONS_SMS,
                                            REQUEST_SMS);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this,
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.O ? PERMISSIONS_PHONE_STATE_AND_SMS : PERMISSIONS_SMS,
                    REQUEST_SMS);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    @OnClick(R.id.fab)
    public void sendSms() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {


            requestSmsPermissions();

        } else {

            buildSms();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        if (requestCode == REQUEST_SMS) {

            if (verifyPermissions(grantResults)) {
                buildSms();

            } else {
                Snackbar.make(smsBody, R.string.permission_no,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(smsListener);
            unregisterReceiver(smsListenerFeedback);
        } catch (IllegalArgumentException ignored) {
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
