package com.example.user.navigation_calendar;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class PersonalSettings extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private PersonalSettingsAdapter adapter;
    ImageButton ps_back;
    ImageButton ps_save;
    ImageButton ps_clean1;
    ImageButton ps_clean2;
    ImageButton change_pic;
    ImageView personal_pic;

    EditText email;
    EditText birthday;

    String person_email;
    String person_birthday;

    //存放要Get的訊息
    private String getUrl = "https://sd.jezrien.one/user/profile";
    Http_Get HPSG;
    SharedPreferences NsharedPreferences;
    private String resultJSON;

    //personal setting patch
    private String patchUrl = "https://sd.jezrien.one/user/profile";

    //存放要Patch的訊息
    private String Sbirthday = null;
    private String Semail = null;
    SharedPreferences sharedPreferences;
    private String token;

    Http_PersonalSettingPatch HPSP;
    static Handler handler; //宣告成static讓service可以直接使用


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);

        HPSP = new Http_PersonalSettingPatch();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString("TOKEN", "");

        //post
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 16:
                        String ss = (String) msg.obj;
                        Log.d("Message",ss);
                        Toast.makeText(PersonalSettings.this, ss, Toast.LENGTH_LONG).show();
                        Intent it = new Intent(PersonalSettings.this,MainActivity.class);
                        startActivity(it);
                        break;
                }
            }

        };

        recyclerView = findViewById(R.id.ps_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<settingCard> psc = new ArrayList<>();

        psc.add(new settingCard("Notification","Accept schedule nitifications",R.id.toggleButton));
        adapter = new PersonalSettingsAdapter(psc);
        recyclerView.setAdapter(adapter);

        ps_back=findViewById(R.id.ps_back);
        ps_back.setOnClickListener(this);
        ps_save=findViewById(R.id.ps_save);
        ps_save.setOnClickListener(this);
        ps_clean1=findViewById(R.id.psbtn_clean);
        ps_clean1.setOnClickListener(this);
        ps_clean2=findViewById(R.id.psbtn_clean2);
        ps_clean2.setOnClickListener(this);

        //找Change Picture Button按鈕
        change_pic=findViewById(R.id.btn_changePP1);
        personal_pic=findViewById(R.id.per_pic);
        //設定按鈕監聽
        change_pic.setOnClickListener(this);

        //set token
        NsharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = NsharedPreferences.getString("TOKEN", "");

        //get personal information : email、birthday

        List<PerSetCard> trans = new ArrayList<>();
        HPSG = new Http_Get();
        HPSG.Get(getUrl, token);
        resultJSON = HPSG.getTt();
        parseJSON(resultJSON, trans);
        getUserInfo();



    }

    public void parseJSON(String result, List<PerSetCard> trans) {
        try {
            JSONObject array = new JSONObject(result);
            person_birthday = array.getString("Birthday");
            person_email = array.getString("Email");

            Log.d("JSON:", person_birthday + "/" + person_email);
            trans.add(new PerSetCard(person_birthday, person_email));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfo() {
        email = findViewById(R.id.ps_email);
        birthday = findViewById(R.id.ps_bir);

        birthday.setText(person_birthday);
        email.setText(person_email);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ps_back:
                finish();
                break;
            case R.id.ps_save:
                //post
                Sbirthday= birthday.getEditableText().toString();
                Semail= email.getEditableText().toString();
                HPSP.Patch(Sbirthday,Semail,patchUrl, token);
                finish();
                break;
            case R.id.psbtn_clean:
                birthday=findViewById(R.id.ps_bir);
                birthday.setText("");
                break;
            case R.id.psbtn_clean2:
                email=findViewById(R.id.ps_email);
                email.setText("");
                break;

            case R.id.btn_changePP1:
                Intent intent = new Intent();
                //開啟Pictures畫面Type設定為image
                intent.setType("image/*");
                //使用Intent.ACTION_GET_CONTENT這個Action                                            //會開啟選取圖檔視窗讓您選取手機內圖檔
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //取得相片後返回本畫面
                startActivityForResult(intent, 2);
                break;
        }
    }

    //取得相片後返回的監聽式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //當使用者按下確定後
        if (resultCode == RESULT_OK){
            //取得圖檔的路徑位置
            Uri uri = data.getData();
            //寫log
            Log.e("uri", uri.toString());
            //抽象資料的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //由抽象資料接口轉換圖檔路徑為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //取得圖片控制項ImageView
                ImageView imageView = (ImageView) findViewById(R.id.per_pic);
                // 將Bitmap設定到ImageView
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void doCropPhoto(Uri uri){
        //進行照片裁剪
        Intent intent = getCropImageIntent(uri);
        startActivityForResult(intent, 2);
    }
    //裁剪圖片的Intent設定
    public static Intent getCropImageIntent(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop", "true");// crop=true 有這句才能叫出裁剪頁面.
        intent.putExtra("scale", true); //讓裁剪框支援縮放
        intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 500);//回傳照片比例X
        intent.putExtra("outputY", 500);//回傳照片比例Y
        intent.putExtra("return-data", true);
        return intent;
    }


}
//JSON-->data
class PerSetCard {
    private String birthday;
    private String email;

    public PerSetCard(String username, String email) {
        this.birthday = username;
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String title) {
        this.birthday = birthday;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

class settingCard{
    String title;
    String word;
    int button;

    public settingCard(String title, String word, int button) {
        this.title = title;
        this.word = word;
        this.button = button;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle() {
        return title;
    }
    public void setButton(int button) {
        this.button = button;
    }
    public int getButton() {
        return button;
    }
    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}

class PersonalSettingsAdapter extends RecyclerView.Adapter<PersonalSettingsAdapter.ViewHolder> {
    private List<settingCard> data;

    public PersonalSettingsAdapter(List<settingCard> data) {
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView PStitle;
        public TextView PSword;
        public ToggleButton PStoggleButton;


        public ViewHolder(View v) {
            super(v);
            PStitle=v.findViewById(R.id.ps_title);
            PSword=v.findViewById(R.id.ps_word);
            PStoggleButton=v.findViewById(R.id.toggleButton);

        }
    }

    @Override
    public PersonalSettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.per_set_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.PStitle.setText(data.get(position).getTitle());
        holder.PSword.setText(data.get(position).getWord());
        holder.PStoggleButton.setTextOff(" ");
        holder.PStoggleButton.setTextOn(" ");
        holder.PStoggleButton.setChecked(false);
        holder.PStoggleButton.setBackgroundResource(R.drawable.group15);
        holder.PStoggleButton.setOnCheckedChangeListener(new toggleButton_OnCheckedChangeListener());


    }
    private class toggleButton_OnCheckedChangeListener
            implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                compoundButton.setBackgroundResource(R.drawable.group14);
            }else{
                compoundButton.setBackgroundResource(R.drawable.group15);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

