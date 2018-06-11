package com.example.druo.lotto.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.druo.lotto.R;

import java.util.ArrayList;

public class SelectedNumbersGridView extends View {
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint primaryDarkPaint = new Paint();
    private Paint textPaint = new Paint();

    public void setNumbers(ArrayList<ArrayList<Integer>> numbers) {
        this.numbers = numbers;
    }

    private ArrayList<ArrayList<Integer>> numbers = new ArrayList<>();

    public SelectedNumbersGridView(Context context) {
        this(context, null);
    }

    public SelectedNumbersGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint blackPaint = new Paint();
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        int accentColor = ContextCompat.getColor(context, R.color.colorAccent);
        int primaryDarkColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        primaryDarkPaint.setColor(primaryDarkColor);
        Paint accentPaint = new Paint();
        accentPaint.setColor(accentColor);
        primaryDarkPaint.setAntiAlias(true);

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
        cellWidth = getWidth() / numColumns / 2;
        cellHeight = getHeight() / numRows / 2;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        if (numColumns == 0 || numRows == 0) {
            return;
        }



        float y = cellWidth - (textPaint.ascent() + textPaint.descent()) * 2;
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(cellHeight * 0.7f);
        textPaint.setAntiAlias(true);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        for (int i = 0; i < numRows; i++) {
            ArrayList<Integer> numberLine = numbers.get(i);
            for (int j = 0; j < numColumns; j++) {
                int number = numberLine.get(j);
                canvas.drawText(Integer.toString(number), j * cellWidth * 2 + cellWidth, i * cellWidth * 2 + y, textPaint);
            }
        }
    }
}
