package com.example.behindu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ListUsers extends Activity implements OnItemClickListener, AdapterView.OnItemLongClickListener
{
    private ListView                listView;
    private List<String>              arrayList;
    TextView textv;
    EditText editView;
    ArrayAdapter<String> adaptr;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_users);
        editView = (EditText) findViewById(R.id.searchET);
        listView = (ListView) findViewById(R.id.list_view);
        arrayList = new ArrayList<String>();
        arrayList.add("Nati");
        arrayList.add("Maor");
        arrayList.add("Aviv");

        adaptr = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adaptr);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        TextView textview = new TextView(this);
        textview.setTextSize(18);

        textview.setText("Press Menu Button for Help\n");
        TextView textview1 = new TextView(this);
        textview1.setText("Insert Text for New Item:");
        LinearLayout ll1 = (LinearLayout)findViewById(R.id.linear1);
        LinearLayout ll2 = (LinearLayout)findViewById(R.id.linear2);
        ll2.addView(textview);
        ll1.addView(textview1);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Object object = adapterView.getItemAtPosition(position);
        String oldText = object.toString();
        String newText = editView.getText().toString();
        arrayList.add(newText);
        StringBuilder sb = new StringBuilder();
        String appendText = sb.append(oldText).append(" ").append(newText).toString();
        adaptr.insert(appendText, (int)id);
        adaptr.notifyDataSetChanged();
    }

    @Override
    public  boolean  onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
        arrayList.remove((int)id);
        adaptr.notifyDataSetChanged();
        return(true);
    }

    static final int help = Menu.FIRST;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, help, Menu.NONE, "Help");
        return(super.onCreateOptionsMenu(menu));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case help:
                showDialog(0);
                return(true);
        }
        return(false);
    }
    @Override
    protected  Dialog onCreateDialog(int id, Bundle args){
        AlertDialog alert = null;
        switch(id){
            case 0:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Short Clik - Inserts new item\n" +
                        " Long Click - Deletes Item" +
                        "\nNew Item's text: old text + new text");
                builder.setTitle("Instructions");
                builder.setCancelable(false);
                builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                alert = builder.create();
                break;
        }
        return(alert);

    }

}

