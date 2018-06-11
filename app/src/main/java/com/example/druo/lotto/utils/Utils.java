package com.example.druo.lotto.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.webkit.URLUtil;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.binaryfork.spanny.Spanny;
import com.example.druo.lotto.R;
import com.example.druo.lotto.TicketType;
import com.example.druo.lotto.database.model.WinningTickets;
import com.example.druo.lotto.database.model.Field;
import com.example.druo.lotto.database.model.Ticket;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public abstract class Utils {
    private static final String TAG = Utils.class.getCanonicalName();

    public static int getTrues(boolean cellChecked[][]) {
        int count = 0;
        for (boolean x[] : cellChecked) {
            for (boolean y : x) {
                count += (y ? 1 : 0);
            }
        }
        return count;
    }

    public static int getTrues(ArrayList<Boolean> matchedNumbers) {
        int count = 0;
        for (Boolean b : matchedNumbers) {
            count += (b ? 1 : 0);
        }

        return count;
    }

    public static ArrayList<Integer> getWeekNumbers(int year, int week, ArrayList<WinningTickets> winningNumbers) {
        ArrayList<Integer> numbers = new ArrayList<>();

        for (int i = 0; i < winningNumbers.size(); i++) {
            if (week == winningNumbers.get(i).getWeek() && year == winningNumbers.get(i).getYear()) {
                numbers = winningNumbers.get(i).getNumbers();
            }
        }
        Log.d("számok: ", numbers.toString());
        return numbers;
    }

    public static int getWeekFromTimestamp(String timestamp) {
        int week;
        Timestamp timeStamp = Timestamp.valueOf(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp.getTime());
        week = calendar.get(Calendar.WEEK_OF_YEAR);

        return week;
    }

    public static ArrayList<Ticket> setMatchedNumbers(ArrayList<Ticket> tickets, ArrayList<WinningTickets> winningNumbers) {
        ArrayList<Boolean> matchedNumbers;

        for (Ticket t : tickets) {
            for (WinningTickets w : winningNumbers) {
                if (t.getTicketType() == w.getTicketType()) {
                    if (t.getWeek() == w.getWeek() && t.getYear() == w.getYear()) {
                        t.setChecked(true);

                        for (Field f : t.getFields()) {
                            matchedNumbers = new ArrayList<>();
                            if (t.getTicketType() == TicketType.SKANDINAV) {
                                for (int i = 0; i < f.getNumbers().size(); i++) {
                                    matchedNumbers.add(w.getNumbers().subList(0, 7).contains(f.getNumbers().get(i)));
                                }
                                for (int i = 0; i < f.getNumbers().size(); i++) {
                                    matchedNumbers.add(w.getNumbers().subList(7, 14).contains(f.getNumbers().get(i)));
                                }

                            } else {
                                // matchedNumbers = new ArrayList<>()
                                for (int i = 0; i < w.getNumbers().size(); i++) {
                                    matchedNumbers.add(f.getNumbers().contains(w.getNumbers().get(i)));

                                }
                            }
                            Log.d(TAG, "matchedNumbers: " + matchedNumbers.toString());
                            f.setMatchedNumbers(matchedNumbers);
                        }

                    }
                }
            }
        }

        return tickets;
    }

    public static Spanny buildString(Context context, Ticket ticket) {
        Spanny spanny = new Spanny();
        if (ticket.isChecked())
            for (Field f : ticket.getFields()) {
                if (ticket.getTicketType() == TicketType.SKANDINAV) {
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < f.getNumbers().size(); j++) {
                            int k = i == 0 ? j : j + 7;
                            if (f.getMatchedNumbers().get(k)) {
                                spanny.append(f.getNumbers().get(j).toString(), new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)));
                                if (j != f.getNumbers().size() - 1)
                                    spanny.append(", ");
                            } else {
                                spanny.append(f.getNumbers().get(j).toString(), new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorRed)));
                                if (j != f.getNumbers().size() - 1)
                                    spanny.append(", ");
                            }
                        }
                        spanny.append("\n");

                    }

                } else {
                    for (int i = 0; i < f.getNumbers().size(); i++) {
                        if (f.getMatchedNumbers().get(i)) {
                            spanny.append(f.getNumbers().get(i).toString(), new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)));
                            if (i != f.getMatchedNumbers().size() - 1)
                                spanny.append(", ");
                        } else {
                            spanny.append(f.getNumbers().get(i).toString(), new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorRed)));
                            if (i != f.getMatchedNumbers().size() - 1)
                                spanny.append(", ");
                        }
                    }
                    spanny.append("\n");

                }
            }
        else
            for (Field f : ticket.getFields()) {
                for (int i = 0; i < f.getNumbers().size(); i++) {
                    spanny.append(f.getNumbers().get(i).toString());
                    if (i != f.getNumbers().size() - 1)
                        spanny.append(", ");
                }
                spanny.append("\n");
            }


        return spanny;
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Integer> getPrizes(ArrayList<Ticket> tickets, ArrayList<WinningTickets> winningNumbers) {
        ArrayList<Integer> wonPrizes = new ArrayList<>();
        int prize;
        for (Ticket t : tickets) {
            if (t.isChecked()) {
                for (WinningTickets w : winningNumbers) {
                    if (t.getYear() == w.getYear() && t.getWeek() == w.getWeek() && t.getTicketType() == w.getTicketType()) {
                        if (t.getTicketType() == TicketType.FIVE) {
                            prize = 0;
                            for (Field f : t.getFields()) {
                                switch (getTrues(f.getMatchedNumbers())) {
                                    case 2:
                                        prize += w.getPrizes().get(3);
                                        break;
                                    case 3:
                                        prize += w.getPrizes().get(2);
                                        break;
                                    case 4:
                                        prize += w.getPrizes().get(1);
                                        break;
                                    case 5:
                                        prize += w.getPrizes().get(0);
                                        break;

                                }
                            }

                            Log.d(TAG, "prize: " + Integer.toString(prize));
                            wonPrizes.add(prize);

                        } else if (t.getTicketType() == TicketType.SIX) {
                            prize = 0;

                            for (Field f : t.getFields()) {
                                switch (getTrues(f.getMatchedNumbers())) {
                                    case 3:
                                        prize += w.getPrizes().get(3);
                                        break;
                                    case 4:
                                        prize += w.getPrizes().get(2);
                                        break;
                                    case 5:
                                        prize += w.getPrizes().get(1);
                                        break;
                                    case 6:
                                        prize += w.getPrizes().get(0);
                                        break;
                                }
                            }

                            Log.d(TAG, "prize: " + Integer.toString(prize));
                            wonPrizes.add(prize);
                        } else if (t.getTicketType() == TicketType.SKANDINAV) {
                            prize = 0;
                            for (Field f : t.getFields()) {
                                ArrayList<Boolean> machineDraw = new ArrayList<>(f.getMatchedNumbers().subList(0, 7));
                                switch (getTrues(machineDraw)) {
                                    case 4:
                                        prize += w.getPrizes().get(3);
                                        break;
                                    case 5:
                                        prize += w.getPrizes().get(2);
                                        break;
                                    case 6:
                                        prize += w.getPrizes().get(1);
                                        break;
                                    case 7:
                                        prize += w.getPrizes().get(0);
                                        break;
                                }
                                ArrayList<Boolean> handDraw = new ArrayList<>(f.getMatchedNumbers().subList(7, 14));
                                switch (getTrues(handDraw)) {
                                    case 4:
                                        prize += w.getPrizes().get(3);
                                        break;
                                    case 5:
                                        prize += w.getPrizes().get(2);
                                        break;
                                    case 6:
                                        prize += w.getPrizes().get(1);
                                        break;
                                    case 7:
                                        prize += w.getPrizes().get(0);
                                        break;
                                }
                            }
                            Log.d(TAG, "prize: " + Integer.toString(prize));
                            wonPrizes.add(prize);
                        }

                    }
                }
            }else{
                wonPrizes.add(0);
            }
        }
        Log.d(TAG, wonPrizes.toString());
        return wonPrizes;
    }

    public static void downloadFiles(Context context,String [] urls){
        String path = context.getFilesDir().getPath() + "/";

        for (String string : urls) {
            final String filename = URLUtil.guessFileName(string, null, null);

            AndroidNetworking.download(string, path, filename)
                    .setTag("Download Data")
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            final int progress = (int) (bytesDownloaded * 100 / totalBytes);

                        }
                    }).startDownload(new DownloadListener() {
                @Override
                public void onDownloadComplete() {

                }

                @Override
                public void onError(ANError anError) {


                }
            });

        }

    }

}
