package com.example.druo.lotto.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.druo.lotto.R;
import com.example.druo.lotto.TicketType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.example.druo.lotto.utils.Utils.getTrues;

public class NumbersPickerGridView extends View {
    private int maxNumber;
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint primaryDarkPaint = new Paint();
    private Paint whitePaint = new Paint();
    private Paint textPaint = new Paint();
    private int primaryColor;
    private boolean[][] cellChecked;
    private int[][] numbers;
    private TicketType ticketType;


    public NumbersPickerGridView(Context context) {
        this(context, null);

    }

    public NumbersPickerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        primaryColor = ContextCompat.getColor(context, R.color.colorPrimary);
        int accentColor = ContextCompat.getColor(context, R.color.colorAccent);
        int primaryDarkColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        Paint blackPaint = new Paint();
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        Paint accentPaint = new Paint();
        accentPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
        primaryDarkPaint.setColor(primaryDarkColor);
        accentPaint.setColor(accentColor);

    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    public int getNumRows() {
        return numRows;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }
        cellHeight = cellWidth = getWidth() / numColumns;
        cellChecked = new boolean[numColumns][numRows];
        numbers = new int[numColumns][numRows];
        setNumbers();
        invalidate();
    }

    private void setNumbers() {

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                numbers[i][j] = (numColumns * j) + (i + 1);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();

        canvas.drawColor(primaryColor);

        setMaxNumber();

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(cellHeight * 0.45f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        float x = cellWidth / 2;
        float y = cellHeight / 2 - (textPaint.ascent() + textPaint.descent()) / 2;

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (cellChecked[i][j]) {
                    canvas.drawRect(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight,
                            primaryDarkPaint);
                }
            }
        }

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, cellHeight * numRows, whitePaint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, whitePaint);
        }

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                canvas.drawText(Integer.toString(numbers[i][j]), i * cellWidth + x, j * cellHeight + y, textPaint);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int column = (int) (event.getX() / cellWidth);
        int row = (int) (event.getY() / cellHeight);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (row < numRows && column < numColumns) {
                if (getTrues(cellChecked) < maxNumber)
                    cellChecked[column][row] = !cellChecked[column][row];
                else if (getTrues(cellChecked) == maxNumber && cellChecked[column][row])
                    cellChecked[column][row] = !cellChecked[column][row];
            }
            invalidate();
        }

        return true;
    }

    public void doRandom() {

        for (boolean row[] : cellChecked)
            Arrays.fill(row, false);

        for (int i = 0; i < maxNumber; i++) {
            Random rand = new Random();
            int row = rand.nextInt(numRows);
            int col = rand.nextInt(numColumns);

            while (cellChecked[col][row]) {
                row = rand.nextInt(numRows);
                col = rand.nextInt(numColumns);
            }
            cellChecked[col][row] = true;
        }
        invalidate();

    }

    public void doDelete() {
        for (boolean row[] : cellChecked)
            Arrays.fill(row, false);
        invalidate();
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    private void setMaxNumber() {
        switch (getTicketType()) {
            case FIVE:
                maxNumber = 5;
                break;
            case SIX:
                maxNumber = 6;
                break;
            case SKANDINAV:
                maxNumber = 7;
                break;
        }
    }

    public ArrayList<Integer> getNumbers() {
        ArrayList<Integer> numbers = new ArrayList<>();
        if (getTrues(cellChecked) == maxNumber)
            for (int i = 0; i < numColumns; i++) {
                for (int j = 0; j < numRows; j++) {
                    if (cellChecked[i][j])
                        numbers.add((numColumns * j) + (i + 1));
                }
            }

        return numbers;
    }

}