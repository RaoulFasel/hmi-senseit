package com.example.bobloos.coach;

import android.app.Activity;
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
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.SelfReportModel;
import com.example.bobloos.model.UserModel;

public class EditPhysStateContext extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton fab_add;
    FloatingActionButton fab_delete;
    EditText contextDescriptionTextView;
    UserModel user;
    PhysStateModel physState;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phys_state_context);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        contextDescriptionTextView = (EditText) findViewById(R.id.phys_context_description_edit_text);
        fab_add = (FloatingActionButton) findViewById(R.id.add_story_button);
        fab_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveContextDescription();
            }
        });

        fab_delete = (FloatingActionButton) findViewById(R.id.delete_story_button);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteContextDescription();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String phys_state_id = intent.getStringExtra("PhysStateId");

        db = new DatabaseHandler(this);
        setUser();
        setPhysState(phys_state_id);
    }

    private void setUser(){
        user = db.getUser(1);
        if(user == null){
            db.addUser(new UserModel());
        }
    }

    private void setPhysState(String phys_state_id){
        physState = db.getPhysState(Long.valueOf(phys_state_id));
        if (physState.getContextDescription() != null){
            contextDescriptionTextView.setText(physState.getContextDescription());
            fab_delete.setVisibility(View.VISIBLE);
        }
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

    public void saveContextDescription(){
        String text = contextDescriptionTextView.getText().toString();
        physState.setContextDescription(text);
        db.updatePhysState(physState);
        Intent intent = new Intent(EditPhysStateContext.this, MainActivity.class);
        intent.putExtra("pageId", "0");
        EditPhysStateContext.this.startActivity(intent);
    }

    public void deleteContextDescription(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Weet je zeker dat je jouw toelichting wilt verwijderen? Let op: de meting blijft bestaan!")
                .setPositiveButton("Ja",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        physState.setContextDescription(null);
                        db.updatePhysState(physState);
                        Intent intent = new Intent(EditPhysStateContext.this, MainActivity.class);
                        intent.putExtra("pageId", "0");
                        EditPhysStateContext.this.startActivity(intent);
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