package com.vhiefa.disasteralert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vhiefa.disasteralert.pref.SessionManager;

public class LoginActivity extends ActionBarActivity {

    EditText namaEdtTxt, nopeEdtTxt, alamatEdtTxt;
    SessionManager sessionManager;
    CheckBox checkboxagree;
    TextView syaratketentuanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setLogo(R.drawable.logo);
        bar.setDisplayUseLogoEnabled(true);
        bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbarcolor));
        bar.setTitle("") ;

        sessionManager = new SessionManager(LoginActivity.this);
        if (sessionManager.isLoggedIn() == true){
            Intent i = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(i);
            finish();
        }

        checkboxagree = (CheckBox) findViewById(R.id.checkboxagree);
        syaratketentuanBtn = (TextView) findViewById(R.id.syaratketentuanBtn);

        String styleText ="Saya menyetujui <u><font color='blue'>Syarat dan Ketentuan</font></u> yang berlaku";
        syaratketentuanBtn.setText(Html.fromHtml(styleText), TextView.BufferType.SPANNABLE);

        namaEdtTxt = (EditText) findViewById(R.id.namaEdtTxt);
        nopeEdtTxt = (EditText) findViewById(R.id.nopeEdtTxt);
        alamatEdtTxt = (EditText) findViewById(R.id.alamatEdtTxt);

        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        syaratketentuanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LoginActivity.this, TermAndConditionActivity.class);
                startActivity(in);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = namaEdtTxt.getText().toString();
                String nope = nopeEdtTxt.getText().toString();
                String alamat = alamatEdtTxt.getText().toString();

                if (nama.isEmpty() || nope.isEmpty() || alamat.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please complete the form!", Toast.LENGTH_LONG).show();
                } else if (!checkboxagree.isChecked()){
                    Toast.makeText(LoginActivity.this, "You should agree the term and condition!", Toast.LENGTH_LONG).show();
                }
                else {
                    sessionManager.createLoginSession(nama,nope,alamat);
                    Intent in = new Intent(LoginActivity.this, Main2Activity.class);
                    startActivity(in);
                    finish();
                }
            }
        });
    }

}
