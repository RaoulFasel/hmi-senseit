package com.example.bobloos.coach;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.SelfReportModel;
import com.example.bobloos.model.UserModel;

/**
 * Created by bob.loos on 30/05/16.
 */
public class EditSelfReport extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton fab_add;
    FloatingActionButton fab_delete;
    EditText storyTextView;
    UserModel user;
    SelfReportModel selfReport;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_self_report);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        storyTextView = (EditText) findViewById(R.id.self_report_edit_text);
        fab_add = (FloatingActionButton) findViewById(R.id.add_story_button);
        fab_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveSelfReport();
            }
        });

        fab_delete = (FloatingActionButton) findViewById(R.id.delete_story_button);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteSelfReport();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Zelfrapportage aanpassen");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String self_report_id = intent.getStringExtra("SelfReportId");
        db = new DatabaseHandler(this);
        setUser();
        setSelfReport(self_report_id);
    }

    private void setUser(){
        user = db.getUser(1);
        if(user == null){
            db.addUser(new UserModel());
        }
    }

    private void setSelfReport(String self_report_id){
        selfReport = db.getSelfReport(Long.valueOf(self_report_id));
        storyTextView.setText(selfReport.getReportText());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveSelfReport(){
        String text = storyTextView.getText().toString();
        long timeInMs = System.currentTimeMillis();
        selfReport.setReportText(text);
        selfReport.setTimeStamp(String.valueOf(timeInMs));
        db.updateSelfReport(selfReport);
        Intent intent = new Intent(EditSelfReport.this, MainActivity.class);
        intent.putExtra("pageId", "1");
        EditSelfReport.this.startActivity(intent);
    }

    public void deleteSelfReport(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Weet je zeker dat je jouw rapportage wilt verwijderen?")
                .setPositiveButton("Ja",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("LOG", "GETTING DELETE YES");
                        db.deleteSelfReport(selfReport);
                        Intent intent = new Intent(EditSelfReport.this, MainActivity.class);
                        intent.putExtra("pageId", "1");

                        EditSelfReport.this.startActivity(intent);
                    }
                })
                .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        Log.d("LOG", "GETTING DELETE NO");

                        dialog.cancel();
                    }
                })
                .show();
    }
}
