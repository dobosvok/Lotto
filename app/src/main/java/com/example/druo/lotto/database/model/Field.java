package com.example.druo.lotto.database.model;

import android.text.TextUtils;

import java.util.ArrayList;

public class Field {
    public static final String TABLE_NAME = "field";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NUMBERS = "numbers";
    public static final String COLUMN_TICKET_ID = "ticket_id";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NUMBERS + " TEXT,"
                    + COLUMN_TICKET_ID + " INTEGER"
                    + ")";


    private int id;
    private ArrayList<Integer> numbers;
    private int ticketId;
    private ArrayList<Boolean> matchedNumbers;


    public Field() {
    }

    public Field(int id, ArrayList<Integer> numbers, int ticketId) {
        this.id = id;
        this.numbers = numbers;
        this.ticketId = ticketId;
        matchedNumbers = new ArrayList<>();
        for(int i=0;i<numbers.size();i++){
            matchedNumbers.add(false);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(ArrayList<Integer> numbers) {
        this.numbers = numbers;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public ArrayList<Boolean> getMatchedNumbers() {
        return matchedNumbers;
    }

    public void setMatchedNumbers(ArrayList<Boolean> matchedNumbers) {
        this.matchedNumbers = matchedNumbers;
    }

    @Override
    public String toString() {

        return TextUtils.join(", ", numbers);

    }

}
