package com.example.druo.lotto.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.example.druo.lotto.R;
import com.example.druo.lotto.TicketType;
import com.example.druo.lotto.database.model.WinningTickets;
import com.example.druo.lotto.adapter.TicketAdapter;
import com.example.druo.lotto.database.DatabaseHelper;
import com.example.druo.lotto.database.model.Ticket;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

import static com.example.druo.lotto.utils.Utils.downloadFiles;
import static com.example.druo.lotto.utils.Utils.getPrizes;
import static com.example.druo.lotto.utils.Utils.setMatchedNumbers;

public class SentTickets extends AppCompatActivity {

    private static final String TAG = SentTickets.class.getCanonicalName();
    protected static ProgressDialog mProgressDialog;
    String urls[] = {"https://bet.szerencsejatek.hu/cmsfiles/otos.csv", "https://bet.szerencsejatek.hu/cmsfiles/hatos.csv", "https://bet.szerencsejatek.hu/cmsfiles/skandi.csv"};

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_tickets);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        AndroidNetworking.initialize(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Kérem várjon");
        mProgressDialog.setMessage("Adatok letöltése");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (isNetworkAvailable())
            new DownloadFiles(this).execute(urls);

        else
            Toast.makeText(this, "Nincs internet", Toast.LENGTH_SHORT).show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    private static class DownloadFiles extends AsyncTask<String, Integer, Long> {
        private final WeakReference<Activity> activityWeakReference;

        DownloadFiles(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Long doInBackground(String... strings) {
            Context context = activityWeakReference.get();
            downloadFiles(context, strings);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Long aLong) {
            LoadFiles loadFiles = new LoadFiles(activityWeakReference.get());
            loadFiles.execute("otos.csv", "hatos.csv", "skandi.csv");

            super.onPostExecute(aLong);
        }
    }

    static class LoadFiles extends AsyncTask<String, Integer, Long> {

        private final WeakReference<Activity> activityWeakReference;
        private ArrayList<WinningTickets> winningNumbers = new ArrayList<>();
        private ArrayList<Ticket> ticketArrayList;


        LoadFiles(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            ButterKnife.bind(activityWeakReference.get());
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... strings) {
            DatabaseHelper databaseHelper = new DatabaseHelper(activityWeakReference.get());
            ticketArrayList = (ArrayList<Ticket>) databaseHelper.getAllTickets();

            for (String string : strings) {
                File file = new File(activityWeakReference.get().getFilesDir().getPath() + "/" + string);
                CsvReader csvReader = new CsvReader();
                csvReader.setFieldSeparator(';');
                int min, max;
                switch (string) {
                    case "otos.csv":
                        min = 11;
                        max = 16;
                        break;
                    case "hatos.csv":
                        min = 13;
                        max = 19;
                        break;
                    case "skandi.csv":
                        min = 11;
                        max = 25;
                        break;
                    default:
                        min = 0;
                        max = 0;
                        break;
                }


                try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
                    CsvRow row;
                    ArrayList<Integer> numbers;
                    int year, week;
                    ArrayList<Integer> prize;
                    int maxRow = 0;
                    while (maxRow < 12) {
                        row = csvParser.nextRow();
                        numbers = new ArrayList<>();
                        year = Integer.parseInt(row.getField(0));
                        week = Integer.parseInt(row.getField(1));
                        if (!string.equals("skandi.csv")) {
                            for (int j = min; j < max; j++) {
                                numbers.add(Integer.parseInt(row.getField(j)));
                            }
                            if (string.equals("otos.csv")) {
                                prize = new ArrayList<>();
                                for (int i = 4; i < 11; i += 2) {
                                    prize.add(Integer.parseInt(row.getField(i).replace(" Ft", "").replace(" ", "")));
                                }
                                //Log.d(TAG,"ötös lottó nyeremények: "+prize.toString());

                                //prizes.add(new Prize(TicketType.FIVE, year, week, prize));
                                winningNumbers.add(new WinningTickets(numbers, prize, week, TicketType.FIVE, year));
                            } else if (string.equals("hatos.csv")) {
                                prize = new ArrayList<>();
                                prize.add(Integer.parseInt((row.getField(4).replace(" Ft", "").replace(" ", ""))));
                                for (int i = 8; i < 13; i += 2) {
                                    prize.add(Integer.parseInt(row.getField(i).replace(" Ft", "").replace(" ", "")));
                                }
                                //Log.d(TAG,"hatos lottó nyeremények: "+prize.toString());
                                //prizes.add(new Prize(TicketType.SIX, year, week, prize));
                                winningNumbers.add(new WinningTickets(numbers, prize, week, TicketType.SIX, year));
                            }
                        } else {
                            prize = new ArrayList<>();
                            for (int i = 4; i < 11; i += 2) {
                                prize.add(Integer.parseInt(row.getField(i).replace(" Ft", "").replace(" ", "")));
                            }
                            //Log.d(TAG,"skandináv lottó nyeremények: "+prize.toString());
                            //prizes.add(new Prize(TicketType.SKANDINAV, year, week, prize));
                            for (int j = min; j < max; j++) {
                                numbers.add(Integer.parseInt(row.getField(j)));
                            }
                            winningNumbers.add(new WinningTickets(numbers, prize, week, TicketType.SKANDINAV, year));
                           /* for (int k = halfMax; k < max; k++) {
                                numbers2.add(Integer.parseInt(row.getField(k)));
                            }

                            winningNumbers.add(new WinningTickets(numbers2, year, week, TicketType.SKANDINAV));*/

                        }
                        maxRow++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            ArrayList<Ticket> newTickets = setMatchedNumbers(ticketArrayList, winningNumbers);
            ArrayList<Integer> allPrizes = getPrizes(newTickets, winningNumbers);
            TicketAdapter adapter = new TicketAdapter(newTickets, allPrizes, activityWeakReference.get());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityWeakReference.get());
            RecyclerView recyclerView = activityWeakReference.get().findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }
}
