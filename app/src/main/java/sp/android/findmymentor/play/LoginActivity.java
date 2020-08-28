package sp.android.findmymentor.play;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sp.android.findmymentor.R;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.input_email)
    protected EditText emailAddressEditText;
    @BindView(R.id.input_password)
    protected EditText passwordEditText;
    @BindView(R.id.sign_in_button_id)
    protected Button signInButton;
    @BindView(R.id.link_signup)
    protected TextView signUp;

    @BindView(R.id.menteeRegisterId)
    protected TextView menteeRegistartion;
    @BindView(R.id.mentorRegisterID)
    protected TextView mentorRegistartion;

    @BindView(R.id.forgot_password_fl_id)
    protected FrameLayout frameLayout;

    @BindView(R.id.relativeLayoutId)
    protected RelativeLayout relativeLayout;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabaseReference;
    private static final String USERS = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initiateLoginActivity();
    }

    private void initiateLoginActivity() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child(USERS);
    }

    @OnClick(R.id.link_signup)
    public void openSignUpPage() {
    }

    @OnClick(R.id.menteeRegisterId)
    public void registerMentee(View view) {
    }

    @OnClick(R.id.mentorRegisterID)
    public void registerMentor() {
    }
}