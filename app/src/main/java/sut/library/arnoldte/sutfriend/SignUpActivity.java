package sut.library.arnoldte.sutfriend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jibble.simpleftp.SimpleFTP;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    // Explicit การประกาศตัวแปร
    private EditText nameEditText, addressEditText, phoneEditText, userEditText, passwordEditText;
    private  String nameString, addressString, phoneString, userString, passwordString, genderString, imageString, imagePathString, imageNameString;
    private RadioButton maleRadioButton, femaleRadioButton;
    private ImageView imageView;
    private boolean statusABoolean = true;
    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Bind Widget
        nameEditText = (EditText) findViewById(R.id.editText);
        addressEditText = (EditText) findViewById(R.id.editText2);
        phoneEditText = (EditText) findViewById(R.id.editText3);
        userEditText = (EditText) findViewById(R.id.editText4);
        passwordEditText = (EditText) findViewById(R.id.editText5);
        maleRadioButton = (RadioButton) findViewById(R.id.radioButton);
        femaleRadioButton = (RadioButton) findViewById(R.id.radioButton2);
        imageView = (ImageView) findViewById(R.id.imageView);
        radioGroup = (RadioGroup) findViewById(R.id.ragGender);


        // ImageView Controller
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "โปรดเลือกรูปภาพ"),1);

            } // onClick
        });

        // Radio Controller
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton:
                        genderString = "Male";
                        break;
                    case R.id.radioButton2:
                        genderString = "Female";
                        break;
                }
            }
        });


    } // Main Method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == 1) && (resultCode == RESULT_OK)){
            Log.d("SutFriendV1", "Result ==> Success");

            //Find Path of Image
            Uri uri = data.getData();
            imagePathString = myFindPath(uri);
            Log.d("SutFriendV1", "imagePathString ==> " + imagePathString);

            // setup ImageView
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            statusABoolean = false;

            imageNameString = imagePathString.substring(imagePathString.lastIndexOf("/"));
            Log.d("SutFriendV1", "imageNameString ==> " + imageNameString);

        }
    } // onActivityResult

    private  String myFindPath(Uri uri) {
        String strResult = null;

        String[] string = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, string, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            strResult = cursor.getString(index);
        }else{
            strResult = uri.getPath();
        }


        return strResult;
    } // myFindPath



    public void clickSignUpSign(View view) {
        // Get Value From Edit Text
        nameString = nameEditText.getText().toString().trim();
        addressString = addressEditText.getText().toString().trim();
        phoneString = phoneEditText.getText().toString().trim();
        userString = userEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();

        // check space
        if (nameString.equals("") || addressString.equals("") || userString.equals("") || passwordString.equals("")) {
            // Have Space
            MyAlert myAlert = new MyAlert(this, R.drawable.doremon48, "Message", "กรุณากรอกข้อมูลให้ครบถ้วน");
            myAlert.myDialog();
        } else if (!(maleRadioButton.isChecked() || femaleRadioButton.isChecked())) {
            MyAlert myAlert = new MyAlert(this, R.drawable.nobita48, "Message", "กรุณาเลือก Gender");
            myAlert.myDialog();

        } else if (statusABoolean) {
            // ยังไม่เลือกภาพ
            MyAlert myAlert = new MyAlert(this,R.drawable.bird48, "Message", "กรุณาเลือกรูปภาพ");
            myAlert.myDialog();
        } else {
            confirmData();
        }

    } // clickSignUp

    private void confirmData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.kon48);
        builder.setTitle("ยืนยันการลงทะเบียน");
        builder.setMessage("ชื่อ = " + nameString + "\n" +
                "ที่อยู่ = " + addressString + "\n" +
                "phone = " + phoneString + "\n" +
                "เพศ = " + genderString + "\n" +
                "image = " + imageNameString );
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                upLoadImageToServer();
                upLoadStringToServer();
                dialog.dismiss();
            }
        });
        builder.show();

    } // confirmData

    private void upLoadStringToServer() {
        SaveUserToServer saveUserToServer = new SaveUserToServer(this);
        saveUserToServer.execute();
    } // upLoadStringToServer

    private class SaveUserToServer extends AsyncTask<Void, Void, String> {
        // Explicit
        private Context context;
        private static final String urlPHP = "http://swiftcodingthai.com/Sut/add_user.php";

        public SaveUserToServer(Context context) {
            this.context = context;
        } // Constructor

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("Name", nameString)
                        .add("Image", "http://swiftcodingthai.com/Sut/Image" + imageNameString)
                        .add("Gender", genderString)
                        .add("Address", addressString)
                        .add("Phone", phoneString)
                        .add("User", userString)
                        .add("Password", passwordString)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlPHP).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                return null;
            }
        } // doInBackground

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("SutFriendV2", "Result ==> " + s);

            if (Boolean.parseBoolean(s)) {
                Toast.makeText(context, "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                MyAlert myAlert = new MyAlert(context, R.drawable.rat48,
                        "Message", "Save Error !!!");
            }
        } // onPost
    } // SaveUserToServer

    private void upLoadImageToServer() {
        // setup new policy
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        //upLoadImage by FTP
        try {
            SimpleFTP simpleFTP = new SimpleFTP();
            simpleFTP.connect("ftp.swiftcodingthai.com", 21, "Sut@swiftcodingthai.com", "Abc12345");
            simpleFTP.bin();
            simpleFTP.cwd("Image");
            simpleFTP.stor(new File(imagePathString));
            simpleFTP.disconnect();

            Log.d("SutFriendV1", "Upload Finish");
        } catch (Exception e) {
            e.printStackTrace();
        }


    } // upLoadImageToServer


} // Main Class
