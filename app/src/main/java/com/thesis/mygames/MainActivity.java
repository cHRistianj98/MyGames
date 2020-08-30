package com.thesis.mygames;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public static ImageButton[] b = new ImageButton[64];

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Chessboard(getApplicationContext());
        setContentView(R.layout.activity_main);

        GridLayout layout = (GridLayout) findViewById(R.id.buttonContainerGridLayout);
        layout.setColumnCount(8);
        layout.setRowCount(8);

        for (int i = 0; i < 64 ; i++) {
            b[i] = new ImageButton(this);
            b[i].setId(i);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 125;
            params.height = 125;
            b[i].setLayoutParams(params);
            b[i].setBackground( getResources().getDrawable( Chessboard.getSquareColor(i)));
            layout.addView(b[i]);
        }

        Chessboard.init();
    }

}
