package com.example.druo.lotto.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.druo.lotto.R;
import com.example.druo.lotto.TicketType;
import com.example.druo.lotto.views.NumbersPickerGridView;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TicketType ticketType;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Intent intent = getIntent();
        ticketType = (TicketType) intent.getSerializableExtra("type");

        switch (ticketType) {
            case FIVE:
                toolbar.setTitle("Ötöslottó");
                break;
            case SIX:
                toolbar.setTitle("Hatoslottó");
                break;
            case SKANDINAV:
                toolbar.setTitle("Skandináv lottó");
                break;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    @OnClick(R.id.fab)
    void sendSms() {
        ArrayList<ArrayList<Integer>> allNumbers = new ArrayList<>();
        Intent intent = new Intent(this, SendSms.class);
        ArrayList<PlaceholderFragment> fragments = mSectionsPagerAdapter.getFragments();
        int pages = 0;
        for (PlaceholderFragment fragment : fragments) {
            if (!fragment.getNumbers().isEmpty()) {
                ArrayList<Integer> numbers = fragment.getNumbers();
                Collections.sort(numbers);
                allNumbers.add(numbers);
                pages++;
                Log.d("ddd", numbers.toString());
            }
        }
        if (pages > 0) {
            intent.putExtra("allNumbers", allNumbers);
            intent.putExtra("type", ticketType);
            startActivity(intent);
        } else {
            Snackbar.make(mViewPager, "Nincs egy mező sem teljesen kitöltve", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    public static class PlaceholderFragment extends Fragment implements ShakeDetector.Listener {

        @BindView(R.id.ll_numbers)
        LinearLayout linearLayout;
        NumbersPickerGridView pixelGrid;
        private TicketType ticketType;
        private Vibrator v;
        private ShakeDetector sd;
        private SensorManager sensorManager;
        @BindView(R.id.btn_random)
        ImageButton btn_random;

        ArrayList<Integer> prevNumbers = new ArrayList<>();


        public PlaceholderFragment() {

        }


        public static PlaceholderFragment newInstance() {
            return new PlaceholderFragment();
        }


        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pick, container, false);
            ButterKnife.bind(this, rootView);
            pixelGrid = new NumbersPickerGridView(getContext());
            sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);
            sd = new ShakeDetector(this);
            sd.start(sensorManager);
            v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            Intent intent = getActivity().getIntent();
            ticketType = (TicketType) intent.getSerializableExtra("type");
            prevNumbers.add(5);

            createPixelGrid();
            linearLayout.addView(pixelGrid);
            return rootView;
        }

        private void createPixelGrid() {
            int column = 0, row = 0;
            switch (ticketType) {
                case FIVE:
                    column = 10;
                    row = 9;
                    break;
                case SIX:
                    column = 9;
                    row = 5;
                    break;
                case SKANDINAV:
                    column = 7;
                    row = 5;
                    break;
            }
            pixelGrid.setNumColumns(column);
            pixelGrid.setNumRows(row);
            pixelGrid.setTicketType(ticketType);
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(metrics.widthPixels, metrics.widthPixels / column * row + 1));

        }


        @OnClick(R.id.btn_random)
        void getRandom() {

            pixelGrid.doRandom();
            buttonShake();

        }

        void buttonShake() {
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            btn_random.startAnimation(shake);
            Random rand = new Random();
            if (prevNumbers.size() == 6)
                prevNumbers.clear();

            int randomNumber = rand.nextInt(6);
            while (prevNumbers.contains(randomNumber))
                randomNumber = rand.nextInt(6);
            prevNumbers.add(randomNumber);

            int dices[] = {R.drawable.ic_dice_1, R.drawable.ic_dice_2, R.drawable.ic_dice_3,
                    R.drawable.ic_dice_4, R.drawable.ic_dice_5, R.drawable.ic_dice_6};
            btn_random.setImageResource(dices[randomNumber]);
        }


        @OnClick(R.id.btn_delete)
        void doDelete() {
            pixelGrid.doDelete();
        }


        @Override
        public void hearShake() {
            if (getUserVisibleHint()) {
                pixelGrid.doRandom();
                buttonShake();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(200);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            sd.start(sensorManager);
        }

        @Override
        public void onStop() {
            super.onStop();
            sd.stop();
        }

        public ArrayList<Integer> getNumbers() {
            return pixelGrid.getNumbers();
        }

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        private ArrayList<PlaceholderFragment> fragments = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            PlaceholderFragment placeholderFragment = PlaceholderFragment.newInstance();
            fragments.add(placeholderFragment);

            return placeholderFragment;
        }

        ArrayList<PlaceholderFragment> getFragments() {
            return fragments;
        }

        @Override
        public int getCount() {

            return 6;
        }

    }
}
