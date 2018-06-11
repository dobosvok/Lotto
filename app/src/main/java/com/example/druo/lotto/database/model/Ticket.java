package com.example.druo.lotto.database.model;

import com.example.druo.lotto.TicketType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class Ticket {
    public static final String TABLE_NAME = "tickets";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TICKETTYPE = "ticketType";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TICKETTYPE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    private int id;
    private TicketType ticketType;
    private String timestamp;
    private ArrayList<Field> fields = new ArrayList<>();
    private boolean checked;


    public Ticket() {
    }

    public Ticket(int id, TicketType ticketType, String timestamp) {
        this.id = id;
        this.ticketType = ticketType;
        this.timestamp = timestamp;
        checked = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public String getFieldsString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            stringBuilder.append(field);
            if (i < fields.size() - 1)
                stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public void addFields(int id, ArrayList<Integer> numbers, int ticket_id) {

        Field field = new Field(id, numbers, ticket_id);
        fields.add(field);

    }

    public int getWeek() {
        Timestamp timestamp = Timestamp.valueOf(getTimestamp());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
    public int getYear() {
        Timestamp timestamp = Timestamp.valueOf(getTimestamp());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return calendar.get(Calendar.YEAR);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
