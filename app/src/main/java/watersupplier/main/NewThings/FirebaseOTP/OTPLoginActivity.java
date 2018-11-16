package watersupplier.main.NewThings.FirebaseOTP;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import watersupplier.main.Configuration.Printer_Connection_Activity;
import watersupplier.main.Main.Common_ActionBar_Abstract;
import watersupplier.main.R;

public class OTPLoginActivity extends Common_ActionBar_Abstract implements View.OnClickListener {

    @BindView(R.id.login_title)
    TextView login_title;

    @BindView(R.id.user_id)
    EditText user_id;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.signin_btn)
    Button signin_btn;

    @BindView(R.id.signup_text)
    TextView signup_text;

    String phoneNumber, otp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth auth;
    private String verificationCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        generateEvents();
    }

    private void generateEvents() {
        login_title.setText("OTP Login");
        signup_text.setVisibility(View.GONE);
//        user_id.setHint("Phone Number");
//        password.setHint("Enter OTP");
//        password.setInputType(2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signin_btn:
                phoneNumber=user_id.getText().toString().trim();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,                     // Phone number to verify
                        60,                           // Timeout duration
                        TimeUnit.SECONDS,                // Unit of timeout
                        this,        // Activity (for callback binding)
                        mCallback);

                break;
        }

    }
}
