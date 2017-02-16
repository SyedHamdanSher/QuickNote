package com.shertech.quicknote;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView tvTime;
    EditText etNote;
    SharedPreference msp;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    private static final String TAG = "MainActivity";
    Button btClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTime= (TextView) findViewById(R.id.tvTime);
        etNote=(EditText) findViewById(R.id.etNote);
        btClear=(Button) findViewById(R.id.btClear);
        msp = new SharedPreference();
        etNote.post(new Runnable() {
            @Override
            public void run() {
                etNote.setSelection(etNote.getText().length());
            }
        });

        msp=new SharedPreference();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onResume() {
        super.onResume();

        msp = loadFile();  // Load the JSON containing the product data - if it exists
        if (msp != null) { // null means no file was loaded
            if(msp.getDescription()!=null && !msp.getDescription().matches("") || !etNote.getText().toString().matches("")) {
                tvTime.setText(msp.getName());
                etNote.setText(msp.getDescription());
            }else {
                calendar = Calendar.getInstance();
                tvTime.setText(simpleDateFormat.format(calendar.getTime()));
            }

        }
        Log.d(TAG, "onResume: ");
    }

    public void clear(View view){
        etNote.setText("");
        msp.setDescription(etNote.getText().toString());
        msp.setCompare("");
        saveProduct();
    }

    private SharedPreference loadFile() {

        Log.d(TAG, "loadFile: Loading JSON File");
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("tvTime")) {
                    msp.setName(reader.nextString());
                } else if (name.equals("etNode")) {
                    msp.setDescription(reader.nextString());
                } else if (name.equals("compare")) {
                    msp.setCompare(reader.nextString());
                }else{
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msp;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (msp.getDescription()!=null && !msp.getDescription().matches("")&&!etNote.getText().toString().matches("")){
            if(!(msp.getDescription().matches(msp.getCompare())) || !etNote.getText().toString().matches(msp.getDescription())){
                calendar = Calendar.getInstance();
                msp.setName(simpleDateFormat.format(calendar.getTime()));
                msp.setDescription(etNote.getText().toString());
                msp.setCompare(etNote.getText().toString());
                saveProduct();
            }
            else{
                Log.d(TAG, "onPause: ");
            }
        }
        else{
            if (!etNote.getText().toString().matches("") || !etNote.getText().toString().matches(msp.getDescription())){
                calendar = Calendar.getInstance();
                msp.setName(simpleDateFormat.format(calendar.getTime()));
                msp.setDescription(etNote.getText().toString());
                msp.setCompare(etNote.getText().toString());
                saveProduct();
            }else {
                Log.d(TAG, "onPause: aa");
                }
            }
        }
    private void saveProduct() {

        Log.d(TAG, "saveProduct: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent(" ");
            writer.beginObject();
            writer.name("tvTime").value(msp.getName());
            writer.name("etNode").value(msp.getDescription());
            writer.name("compare").value(msp.getCompare());
            writer.endObject();
            writer.close();

            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
