package com.example.druo.lotto.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.binaryfork.spanny.Spanny;
import com.example.druo.lotto.R;
import com.example.druo.lotto.database.model.Ticket;

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.druo.lotto.utils.Utils.buildString;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private ArrayList<Ticket> dataList;
    private ArrayList<Integer> prizes;
    private int lastPosition = -1;

    private Context context;

    public TicketAdapter(ArrayList<Ticket> dataList, ArrayList<Integer> prizes, Context context) {
        this.dataList = dataList;
        this.prizes = prizes;
        this.context = context;
    }


    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {

        Locale hungarian = new Locale("hu", "HU");

        switch (dataList.get(position).getTicketType()) {
            case FIVE:
                holder.imageViewTicketType.setImageResource(R.drawable.ic_five);
                break;
            case SIX:
                holder.imageViewTicketType.setImageResource(R.drawable.ic_six);
                break;
            case SKANDINAV:
                holder.imageViewTicketType.setImageResource(R.drawable.ic_skandi);
                break;
        }


        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",hungarian).parse(dataList.get(position).getTimestamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.textViewTimestamp.setText(new SimpleDateFormat("yyyy MMMM dd. hh:mm",hungarian).format(date));
        Spanny spanny = buildString(context, dataList.get(position));
        holder.textViewNumbers.setText(spanny);
        String prize;
        if (dataList.get(position).isChecked()) {

            NumberFormat format = NumberFormat.getCurrencyInstance(hungarian);

            prize = format.format(prizes.get(position)).replace(",00", "");
        } else {
            prize = "Még nem huzták ki a nyerőszámokat";
        }
        holder.textViewPrize.setText(prize);
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTicketType, textViewTimestamp, textViewNumbers, textViewPrize;
        ImageView imageViewTicketType;

        TicketViewHolder(View itemView) {
            super(itemView);
            imageViewTicketType = itemView.findViewById(R.id.iv_tickettype);
            // textViewTicketType = itemView.findViewById(R.id.tv_ticket_type);
            textViewTimestamp = itemView.findViewById(R.id.tv_timestamp);
            textViewNumbers = itemView.findViewById(R.id.tv_numbers);
            textViewPrize = itemView.findViewById(R.id.tv_prize);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


}