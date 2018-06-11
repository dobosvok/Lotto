package com.example.druo.lotto.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.druo.lotto.TicketType;
import com.example.druo.lotto.database.model.Field;
import com.example.druo.lotto.database.model.Ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_NAME = "tickets_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(Ticket.CREATE_TABLE);
        db.execSQL(Field.CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Ticket.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Field.TABLE_NAME);


        onCreate(db);
    }

    public long insertFields(String numbers, long ticketId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Field.COLUMN_NUMBERS, numbers);
        values.put(Field.COLUMN_TICKET_ID, ticketId);

        long id = db.insert(Field.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public long insertTicket(String ticketType, ArrayList<ArrayList<Integer>> numbers) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(Ticket.COLUMN_TICKETTYPE, ticketType);


        long id = db.insert(Ticket.TABLE_NAME, null, values);

        for (int i = 0; i < numbers.size(); i++) {
            String temp = TextUtils.join(", ", numbers.get(i));
            insertFields(temp, id);
        }


        db.close();


        return id;
    }

    public Ticket getTicket(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Ticket.TABLE_NAME,
                new String[]{Ticket.COLUMN_ID, Ticket.COLUMN_TICKETTYPE, Ticket.COLUMN_TIMESTAMP},
                Ticket.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();


        Ticket ticket = new Ticket(
                cursor.getInt(cursor.getColumnIndex(Ticket.COLUMN_ID)),
                TicketType.valueOf(cursor.getString(cursor.getColumnIndex(Ticket.COLUMN_TICKETTYPE))),
                cursor.getString(cursor.getColumnIndex(Ticket.COLUMN_TIMESTAMP)));


        cursor.close();

        return ticket;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        int ticketId;


        String selectQuery = "SELECT  * FROM " + Ticket.TABLE_NAME + " ORDER BY " +
                Ticket.COLUMN_TIMESTAMP + " DESC";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Ticket ticket = new Ticket();
                ticket.setId(cursor.getInt(cursor.getColumnIndex(Ticket.COLUMN_ID)));
                ticketId = cursor.getInt(cursor.getColumnIndex(Ticket.COLUMN_ID));

                ticket.setTicketType(TicketType.valueOf(cursor.getString(cursor.getColumnIndex(Ticket.COLUMN_TICKETTYPE))));
                ticket.setTimestamp(cursor.getString(cursor.getColumnIndex(Ticket.COLUMN_TIMESTAMP)));
                String getFieldsQuery = "SELECT * FROM " + Field.TABLE_NAME + " WHERE field.ticket_id = " +
                        ticketId;
                Cursor fieldCursor = db.rawQuery(getFieldsQuery, null);
                if (fieldCursor.moveToFirst()) {
                    do {
                        int id = fieldCursor.getInt(fieldCursor.getColumnIndex(Field.COLUMN_ID));
                        ArrayList<String> numbersString =new ArrayList<>(Arrays.asList(fieldCursor.getString(fieldCursor.getColumnIndex(Field.COLUMN_NUMBERS)).split(",")));

                        ArrayList<Integer> numbers = new ArrayList<Integer>();
                        for(String num:numbersString){
                            numbers.add(Integer.parseInt(num.trim()));
                        }
                        ticket.addFields(id, numbers, ticketId);
                    } while (fieldCursor.moveToNext());
                }



                tickets.add(ticket);
            } while (cursor.moveToNext());
        }


        db.close();


        return tickets;
    }

    public int getTicketCount() {
        String countQuery = "SELECT  * FROM " + Ticket.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();



        return count;
    }


    public void deleteTicket(Ticket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Ticket.TABLE_NAME, Ticket.COLUMN_ID + " = ?",
                new String[]{String.valueOf(ticket.getId())});
        db.close();
    }
}
