package com.example.druo.lotto.database.model;

import com.example.druo.lotto.TicketType;

import java.util.ArrayList;

public class WinningTickets {
    private ArrayList<Integer> numbers;
    private int year;
    private int week;
    private TicketType ticketType;
    private ArrayList<Integer> prizes;

    public WinningTickets(ArrayList<Integer> numbers, ArrayList<Integer> prizes, int week, TicketType ticketType, int year) {
        this.numbers = numbers;
        this.prizes = prizes;
        this.year = year;
        this.week = week;
        this.ticketType = ticketType;
    }


    public ArrayList<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(ArrayList<Integer> numbers) {
        this.numbers = numbers;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public ArrayList<Integer> getNumbersByWeek(int year, int week) {
        if (week == this.week && year == this.year) {
            return numbers;
        } else return null;

    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public ArrayList<Integer> getPrizes() {
        return prizes;
    }

    public void setPrizes(ArrayList<Integer> prizes) {
        this.prizes = prizes;
    }
}
