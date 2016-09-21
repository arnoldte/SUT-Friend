package sut.library.arnoldte.sutfriend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class SignUpActivity extends AppCompatActivity {

    // Explicit การประกาศตัวแปร
    private EditText nameEditText, addressEditText, phoneEditText, userEditText, passwordEditText;
    private  String nameString, addressString, phoneString, userString, passwordString, genderString, imageString;
    private RadioButton maleRadioButton, femaleRadioButton;


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


    } // Main Method

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

        }

    } // clickSignUp


} // Main Class
