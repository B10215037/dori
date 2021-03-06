package com.ntust.dori;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE = 1, CAMERA_REQUEST_CODE = 2, SPEECHSHOW_REQUEST_CODE = 3, FILE_REQUEST_CODE = 2;
    boolean doriShow = false;
    boolean isSleep = false;
    Button speak;
    ListView wordList;
    EditText editText;
    ImageView imageView;
    public AnimationDrawable frameAnimation;
    public static SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speak = (Button)findViewById(R.id.button);
        wordList = (ListView)findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.editText);
        imageView = (ImageView)findViewById(R.id.imageView);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                String lang = Locale.TRADITIONAL_CHINESE.toString();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "OK Dori");
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        });

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() == 0) {
            speak.setEnabled(false);
            speak.setText("不支援語音識別");
        }

        findViewById(R.id.test_button_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                String lang = Locale.TRADITIONAL_CHINESE.toString();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "OK Dori");
                startActivityForResult(intent, SPEECHSHOW_REQUEST_CODE);

            }
        });

        findViewById(R.id.button_run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> matches = new ArrayList<>();
                matches.add(((EditText) findViewById(R.id.editText)).getText().toString());
                try {
                    exec(analyze(matches));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        db = openOrCreateDatabase("instruction", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS HotKey(_id INTEGER PRIMARY KEY, alias TEXT UNIQUE, instruction TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS OldKey(_id INTEGER PRIMARY KEY, instruction TEXT UNIQUE)");
        databaseInit();

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS}, 100);
//        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 100);
//        }
//        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
//        }
//        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
//        }
    }

    void databaseInit() {
        Cursor cursor = db.rawQuery("SELECT * FROM OldKey", null);
        if (cursor != null) {
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '新增指令' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='新增指令')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '打開' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='打開')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '寄信' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='寄信')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '撥給' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='撥給')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '撥出' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='撥出')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '連到' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='連到')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '執行' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='執行')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '選擇' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='選擇')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '檔案' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='檔案')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '搜尋' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='搜尋')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '位置' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='位置')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '出來' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='出來')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '回去' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='回去')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '跳舞' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='跳舞')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '睡覺' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='睡覺')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '站好' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='站好')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '名字' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='名字')");
            db.execSQL("INSERT INTO OldKey(instruction) SELECT '跳跳' WHERE NOT EXISTS (SELECT * FROM OldKey WHERE instruction='跳跳')");

            cursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            wordList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matches));
            try {
                exec(analyze(matches));
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == SPEECHSHOW_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            wordList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matches));
            editText.setText(editText.getText() + matches.get(0));
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            Toast.makeText(this, "The photo is put in:\n" + photoUri, Toast.LENGTH_LONG).show();
        }
        else if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Toast.makeText(this, "The file is put in:\n" + uri, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    ArrayList<String> analyze(ArrayList<String> matches) throws Exception {
        ArrayList<String> instruction = new ArrayList<>();

        Cursor cursor = null;

        for (int index = 0; index < matches.size(); index++) {
            cursor = db.rawQuery("SELECT * FROM HotKey", null);
            if (cursor != null) {
                cursor.moveToFirst();
                for (int i=0; i<cursor.getCount(); ++i) {
                    if (matches.get(index).length() >= 2 && cursor.getString(1).equals(matches.get(index).substring(0, 2))) { //前2個
                        matches.set(index, cursor.getString(2) + matches.get(index).substring(2));
                        break;
                    }
                    cursor.moveToNext();
                }
            }

            cursor = db.rawQuery("SELECT * FROM OldKey", null);
            if (cursor != null) {
                cursor.moveToFirst();
                for (int i=0; i<cursor.getCount(); ++i) {
                    if (matches.get(index).length() >= 2 && cursor.getString(1).equals(matches.get(index).substring(0, 2))) { //前2個
                        instruction.add(cursor.getString(1)); //add ins
                        instruction.add(matches.get(index).substring(2));
                        cursor.close();
                        return instruction;
                    }
                    cursor.moveToNext();
                }

                cursor.moveToFirst();
                for (int i=0; i<cursor.getCount(); ++i) {
                    if (matches.get(index).length() >= 4 && cursor.getString(1).equals(matches.get(index).substring(0, 4))) { //前4個
                        instruction.add(cursor.getString(1)); //add ins
                        instruction.add(matches.get(index).substring(4));
                        cursor.close();
                        return instruction;
                    }
                    cursor.moveToNext();
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        instruction.add("");
        instruction.add(matches.get(0));
        return instruction;
    }

    void exec(ArrayList<String> instruction) throws Exception {
        if (doriShow && (int)(Math.random() * 4) == 0) {
            imageView.setBackgroundResource(R.drawable.sleep);
            Toast.makeText(this, "管你的我要睡了", Toast.LENGTH_LONG).show();
            isSleep = true;
            return;
        }
        if (instruction.get(0).equals("新增指令")) {
            String hotKey = instruction.get(1).substring(0, 2), oldKey = instruction.get(1).substring(2);

            db.execSQL("INSERT INTO HotKey(alias,instruction) VALUES('" + hotKey + "','" + oldKey + "')");
        }
        else if (instruction.get(0).equals("打開")) {
            if (instruction.get(1).equals("相機")) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
            else if (instruction.get(1).equals("聯絡人")) {
                Uri contacts = Uri.parse("content://contacts/people");
                Intent showContacts = new Intent(Intent.ACTION_VIEW, contacts);
                startActivity(showContacts);
            }
            else if (instruction.get(1).equals("YouTube")) {
                Intent launchApp = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                if (launchApp != null) {
                    launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchApp);
                } else {
                    Uri uri = Uri.parse("market://details?id=" + "com.google.android.youtube");
                    launchApp = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(launchApp);
                }
            }
        }
        else if (instruction.get(0).equals("寄信")) {
            if (instruction.get(1).contains("@")) {
                Uri mail = Uri.parse("mailto:" + instruction.get(1));
                Intent sendEmail = new Intent(Intent.ACTION_SENDTO, mail);
                startActivity(sendEmail);
            }
            else {
                Uri uri = Uri.parse("smsto:" + instruction.get(1));
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//            intent.putExtra("sms_body", "The SMS text");
                startActivity(intent);
            }
        }
        else if (instruction.get(0).equals("撥給")) {
            Uri uri = Uri.parse("tel:" + instruction.get(1));
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
        }
        else if (instruction.get(0).equals("撥出")) {
            Uri uri = Uri.parse("tel:" + instruction.get(1));
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            startActivity(intent);
        }
        else if (instruction.get(0).equals("連到")) {
            Uri uri = Uri.parse("http://" + instruction.get(1));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else if (instruction.get(0).equals("執行")) {
            Intent launchApp = getPackageManager().getLaunchIntentForPackage(instruction.get(1));
            if (launchApp != null) {
                launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchApp);
            } else {
                Uri uri = Uri.parse("market://details?id=" + instruction.get(1));
                launchApp = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(launchApp);
            }
        }
        else if (instruction.get(0).equals("選擇")) {
            Intent selApp = new Intent(Intent.ACTION_MAIN);
            selApp.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(Intent.createChooser(selApp, "Choose an App to launch"));
        }
        else if (instruction.get(0).equals("檔案")) {
            Intent pickFile = new Intent(Intent.ACTION_GET_CONTENT);
            pickFile.setType("*/*");
            pickFile.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(pickFile, "selecta File from content provider"), FILE_REQUEST_CODE);
        }
        else if (instruction.get(0).equals("搜尋")) {
            Uri uri = Uri.parse("http://www.google.com/search?q=" + instruction.get(1));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else if (instruction.get(0).equals("位置")) {
            Uri uri = Uri.parse("geo:0,0?q=" + instruction.get(1));
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        }
        else if (instruction.get(0).equals("出來") && !doriShow) {
            imageView.setBackgroundResource(R.drawable.come);
            frameAnimation = (AnimationDrawable)imageView.getBackground();
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            });
            doriShow = true;
            isSleep = false;
        }
        else if (instruction.get(0).equals("回去") && doriShow) {
            imageView.setBackgroundResource(R.drawable.back);
            frameAnimation = (AnimationDrawable)imageView.getBackground();
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            });
            doriShow = false;
            isSleep = false;
        }
        else if (instruction.get(0).equals("跳舞") && doriShow) {
            imageView.setBackgroundResource(R.drawable.dance);
            frameAnimation = (AnimationDrawable)imageView.getBackground();
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            });
            isSleep = false;
        }
        else if (instruction.get(0).equals("睡覺") && doriShow) {
            if (isSleep) {
                Toast.makeText(this, "我在睡了", Toast.LENGTH_LONG).show();
            }
            else {
                if ((int)(Math.random() * 2) == 0) {
                    imageView.setBackgroundResource(R.drawable.sleep);
                    isSleep = true;
                }
                else
                    Toast.makeText(this, "我不想睡", Toast.LENGTH_LONG).show();
            }
        }
        else if (instruction.get(0).equals("站好") && doriShow) {
            if ((int)(Math.random() * 2) == 0) {
                imageView.setBackgroundResource(R.drawable.idle);
            }
            else {
                imageView.setBackgroundResource(R.drawable.dance);
                frameAnimation = (AnimationDrawable)imageView.getBackground();
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        frameAnimation.start();
                    }
                });
                Toast.makeText(this, "不要", Toast.LENGTH_LONG).show();
            }
            isSleep = false;
        }
        else if (instruction.get(0).equals("名字") && doriShow && !isSleep) {
            Toast.makeText(this, "Dori", Toast.LENGTH_LONG).show();
        }
        else if (instruction.get(0).equals("跳跳") && doriShow) {
            imageView.setBackgroundResource(R.drawable.idle);
            imageView.setBackgroundResource(R.drawable.jump);
            frameAnimation = (AnimationDrawable)imageView.getBackground();
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            });
            isSleep = false;
        }
        else if (instruction.get(0).equals("")) {
            if (doriShow)
                Toast.makeText(this, instruction.get(1) + "是什麼?我聽不懂", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "無法解析" + instruction.get(1), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ins_list:
                Intent intent = new Intent();
                intent.setClass(this, InsListActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}