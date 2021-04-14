package com.jkob.lottery;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static TextView[] lotteryBalls;
    private Handler handler;
    private static int ballUpdateDelayMillis;
    private static int highestNumber;
    private static int ticketCost;
    private static float splitChance;
    private static float[][] prizes;
    private ArrayList<Integer> myBet;
    private ArrayList<Integer> winningNumbers;
    private float profit;
    private int currentIndex;
    private int numberOfRolls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extractLotteryBalls();
        setLotteryBallsColors();
        initializeConstants();
        setSeekBarListener();
        winningNumbers = generateDistinctRandomNumbers(lotteryBalls.length, highestNumber);
    }

    public void betMultipleTimes(final View caller) {

        String text = ((Button) caller).getText().toString();
        numberOfRolls = Integer.parseInt(text.substring(14, text.length() - 1));
        ballUpdateDelayMillis = 1000 / numberOfRolls;
        profit = 0;

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (numberOfRolls > 0) {
                    numberOfRolls--;
                    betOnce(caller);
                    handler.postDelayed(this, ballUpdateDelayMillis * (lotteryBalls.length + 5));
                }
            }
        });
    }

    public void betOnce(View caller) {
        currentIndex = -1;
        myBet = generateDistinctRandomNumbers(lotteryBalls.length, highestNumber);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentIndex < lotteryBalls.length - 1) {
                    currentIndex++;
                    lotteryBalls[currentIndex].setText(String.valueOf(myBet.get(currentIndex)));
                    if (numberMatch(lotteryBalls[currentIndex]))
                        lotteryBalls[currentIndex].setTextColor(getResources().getColor(R.color.numberMatch));
                    else
                        lotteryBalls[currentIndex].setTextColor(getResources().getColor(R.color.white));
                    lotteryBalls[currentIndex].postDelayed(this, ballUpdateDelayMillis);
                }
            }
        });
        determinePrize(numbersMatching(myBet));
        ((TextView) findViewById(R.id.prize_text_view)).setText(String.format("Balance: %.2f$", profit));
    }

    private ArrayList<Integer> generateDistinctRandomNumbers(int quantity, int highestNumber) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 1; i <= highestNumber; i++) {
            result.add(i);
        }

        Collections.shuffle(result);

        return new ArrayList<>(result.subList(0, quantity));
    }

    private void extractLotteryBalls() {
        lotteryBalls = new TextView[6];
        lotteryBalls[0] = findViewById(R.id.first_ball);
        lotteryBalls[1] = findViewById(R.id.second_ball);
        lotteryBalls[2] = findViewById(R.id.third_ball);
        lotteryBalls[3] = findViewById(R.id.fourth_ball);
        lotteryBalls[4] = findViewById(R.id.fifth_ball);
        lotteryBalls[5] = findViewById(R.id.sixth_ball);
    }

    private void setLotteryBallsColors() {
        ((GradientDrawable) lotteryBalls[0].getBackground()).setColor(getColor(R.color.firstBallColor));
        ((GradientDrawable) lotteryBalls[1].getBackground()).setColor(getColor(R.color.secondBallColor));
        ((GradientDrawable) lotteryBalls[2].getBackground()).setColor(getColor(R.color.thirdBallColor));
        ((GradientDrawable) lotteryBalls[3].getBackground()).setColor(getColor(R.color.fourthBallColor));
        ((GradientDrawable) lotteryBalls[4].getBackground()).setColor(getColor(R.color.fifthBallColor));
        ((GradientDrawable) lotteryBalls[5].getBackground()).setColor(getColor(R.color.sixthBallColor));
    }

    private void setSeekBarListener() {
        SeekBar seekBar = findViewById(R.id.tickets_quantity_seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int ticketsNumber = (int) (Math.pow(10, progress / 20.0));
                ((TextView) findViewById(R.id.multiple_rolls_button)).setText(String.format("multiple bets(%d)", ticketsNumber));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initializeConstants() {
        ballUpdateDelayMillis = 1000;
        handler = new Handler();
        highestNumber = 46;
        ticketCost = 2;
        splitChance = 0.987f;
        profit = 0;
        currentIndex = -1;
        setPrizes();
    }

    private void setPrizes() {
        prizes = new float[2][lotteryBalls.length + 1];
        prizes[0][0] = 0;
        prizes[0][1] = 0;
        prizes[0][2] = ticketCost;
        prizes[0][3] = 5;
        prizes[0][4] = 150;
        prizes[0][5] = 4000;
        prizes[0][6] = 2400000;
        prizes[1][0] = 0;
        prizes[1][1] = 0;
        prizes[1][2] = ticketCost;
        prizes[1][3] = 26.85f;
        prizes[1][4] = 807.52f;
        prizes[1][5] = 22096;
        prizes[1][6] = 0;
    }

    private void determinePrize(int numbersMatching) {
        profit -= ticketCost;

        if ((int) (Math.random() * 1000) >= 1000 * splitChance)
            profit += prizes[0][numbersMatching];
        else profit += prizes[1][numbersMatching];

        if (numbersMatching == 2) numberOfRolls++;
    }

    private int numbersMatching(ArrayList<Integer> myBet) {
        int result = 0;
        for (Integer i : myBet) {
            for (Integer j : winningNumbers) {
                if (i.equals(j)) result++;
            }
        }

        return result;
    }

    private boolean numberMatch(TextView lotteryBall) {
        int rolledNumber = Integer.parseInt(lotteryBall.getText().toString());
        return winningNumbers.contains(rolledNumber);
    }
}