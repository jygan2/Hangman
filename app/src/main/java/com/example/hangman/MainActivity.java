package com.example.hangman;

import android.app.Activity;
import android.hardware.SensorListener;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.tbouron.shakedetector.library.ShakeDetector;

import java.util.ArrayList;
import java.util.Random;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    /**
     * word bank
     */
    final String[] wordbank = {"pikachu", "geoff", "computer", "illinois", "chicago",
        "chuchu", "xyz", "fortnite", "word", "california", "university", "school", "finals",
        "avengers", "antidisestablishmentarianism", "lego", "science", "engineering", "java",
        "class", "string", "jimmy", "kevin", "josh", "nathan", "guess", "pass", "project",
        "polymorphism", "generics", "abstraction", "object", "calculus", "yeet", "fail", "lecture",
        "midterms", "quizzes", "cbtf", "laptop", "machine", "problem", "study", "cookie",
        "nigerian", "america", "tree", "recursion", "instantiation", "nodes", "leaf", "print"};
    /**
     * word to guess.
     */
    char[] word;
    /**
     * button for user to click to enter in char.
     */
    Button updateButton;
    /**
     * button for winners.
     */
    Button gameWinButton;
    /**
     * char that users input that is their guess.
     */
    String charGuess;
    /**
     * array that holds characters guessed correctly and characters to be guessed/
     */
    char[] charDisplay;
    /**
     * string of characters that user guessed incorrectly.
     */
    char[] charactersGuessed = new char[26];
    /**
     * box that displays invalid character in case if user input is invalid.
     */
    TextView errorDisplay;
    /**
     * box that displays charDisplay.
     */
    TextView wordGuess;
    /**
     * displays the characters that the user has already guessed.
     */
    TextView charactersGuessedDisplay;
    /**
     * box where user will type in guess.
     */
    EditText charInput;
    /**
     * hangman pic.
     */
    ImageView hangmanpic;
    /**
     * amount of tries user has left.
     */
    int guessesLeft = 6;
    /**
     * how many characters left for user to guess;
     */
    int charsLeftToGuess;
    /**
     * index of charactersguessed char array.
     */
    int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateButton = findViewById(R.id.enterButton);
        errorDisplay = findViewById(R.id.errorDisplay);
        wordGuess = findViewById(R.id.wordGuessDisplay);
        charactersGuessedDisplay = findViewById(R.id.guessedCharacters);
        hangmanpic = findViewById(R.id.updatedHangmanPic);
        hangmanpic.setImageResource(R.drawable.hangman0);
        gameWinButton = findViewById(R.id.winButton);

        gameStart();
        ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                //stuff
                gameStart();
            }
        });
        charInput = findViewById(R.id.textInputLayout);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDisplay.setText(" ");
                errorDisplay.setVisibility(View.INVISIBLE);
                charGuess = charInput.getText().toString();
                wordCheck(charGuess);
            }
        });
        gameWinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameStart();
            }
        });
    }

    /**
     * sets up game display for characters left to guess.
     */
    public void gameStart() {
        errorDisplay.setVisibility(View.INVISIBLE);
        Random random = new Random();
        word = wordbank[random.nextInt(wordbank.length)].toCharArray();
        hangmanpic.setImageResource(R.drawable.hangman0);
        guessesLeft = 6;
        charactersGuessed = new char[26];
        charDisplay = new char[word.length * 2];
        charsLeftToGuess = word.length;
        for (int i = 0; i < word.length * 2 ; i += 2) {
            charDisplay[i] = '_';
            charDisplay[i + 1] = ' ';
        }
        wordGuess.setText(new String(charDisplay));
        charactersGuessedDisplay.setText("");
        gameWinButton.setVisibility(View.INVISIBLE);
        updateButton.setEnabled(true);
    }

    /**
     * checks if user input is valid, and runs proper functions if valid or invalid.
     * @param input char that user inputted.
     */
    public void wordCheck(String input) {
        if (input.equals(new String(word))) {
            charsLeftToGuess = 0;
            errorDisplay.setVisibility(View.INVISIBLE);
            gameCheckWinOrLose();
        }
        if (input.trim().equals("")) {
            errorDisplay.setVisibility(View.VISIBLE);
            errorDisplay.setText("Please enter Guess");
            charInput.setText("");
            return;
        }
        if (input.trim().length() > 1) {
            errorDisplay.setVisibility(View.VISIBLE);
            errorDisplay.setText("Please Enter One Letter");
            charInput.setText("");
            return;
        }
        if (!Character.isLetter(input.charAt(0))) {
            errorDisplay.setVisibility(View.VISIBLE);
            errorDisplay.setText("Guess must be a letter");
            charInput.setText("");
            return;
        }
        String check = new String(charDisplay);
        if (new String(charactersGuessed).contains(input) || check.contains(input)) {
            errorDisplay.setVisibility(View.VISIBLE);
            errorDisplay.setText("Character Already Guessed");
            charInput.setText("");
            return;
        }

        char guess = Character.toLowerCase(input.charAt(0));
        boolean contains = false;
        for (char c : word) {
            if (c == guess) {
                contains = true;
                break;
            }
        }
        if (contains) {
            for (int i = 0; i < word.length; i++) {
                if (word[i] == guess) {
                    charDisplay[i * 2] = guess;
                    charsLeftToGuess--;
                }
            }
            wordGuess.setText(new String(charDisplay));
        } else {
            guessesLeft--;
            charactersGuessed[index] = guess;
            index++;
            charactersGuessedDisplay.setText(new String(charactersGuessed));
            refreshImage();
        }
        charInput.setText("");
        gameCheckWinOrLose();
    }

    /**
     * checks if the game is won.
     */
    public void gameCheckWinOrLose() {
        if (guessesLeft <= 0) {
            gameWinButton.setVisibility(View.VISIBLE);
            errorDisplay.setVisibility(View.INVISIBLE);
            wordGuess.setVisibility(View.INVISIBLE);
            gameWinButton.setText("You Lost, Click to Restart");
            updateButton.setEnabled(false);
        }
        if (charsLeftToGuess <= 0) {
            wordGuess.setVisibility(View.INVISIBLE);
            gameWinButton.setVisibility(View.VISIBLE);
            errorDisplay.setVisibility(View.INVISIBLE);
            gameWinButton.setText("You Won! Click to Restart");
            updateButton.setEnabled(false);
        }
    }

    /**
     * refreshes hangman image
     */
    public void refreshImage() {
        if (guessesLeft == 5) {
            hangmanpic.setImageResource(R.drawable.hangman1);
        } else if (guessesLeft == 4) {
            hangmanpic.setImageResource(R.drawable.hangman2);
        } else if (guessesLeft == 3) {
            hangmanpic.setImageResource(R.drawable.hangman3);
        } else if (guessesLeft == 2) {
            hangmanpic.setImageResource(R.drawable.hangman4);
        } else if (guessesLeft == 1) {
            hangmanpic.setImageResource(R.drawable.hangman5);
        } else if (guessesLeft == 0) {
            hangmanpic.setImageResource(R.drawable.hangman6);
        }
    }


}

