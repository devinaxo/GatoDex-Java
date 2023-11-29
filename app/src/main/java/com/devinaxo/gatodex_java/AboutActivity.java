package com.devinaxo.gatodex_java;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setSupportActionBar(findViewById(R.id.aboutToolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("About GatoDex");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ImageView githubProf = findViewById(R.id.githubIcon);
        githubProf.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openWebPage("https://github.com/devinaxo");
            }
        });
        ImageView twitterProf = findViewById(R.id.twitterIcon);
        twitterProf.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openWebPage("https://twitter.com/devinachoes");
            }
        });
        findViewById(R.id.iconLink).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){openWebPage("https://www.flaticon.com/free-icons/pokemon");}
        });

    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}