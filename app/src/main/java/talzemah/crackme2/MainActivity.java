package talzemah.crackme2;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mLogInUserNameField;
    private TextView mLogInPasswordField;
    private Button mLogInBtn;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogInUserNameField = (TextView) findViewById(R.id.LogInUserNameField);
        mLogInPasswordField = (TextView) findViewById(R.id.LogInPasswordField);
        mLogInBtn = (Button) findViewById(R.id.btnLogIn);

        mLogInUserNameField.setEnabled(false);
        mLogInPasswordField.setEnabled(false);
        mLogInBtn.setEnabled(false);

        mLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckAuthentication();
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

                if (count >= 5) {
                    enableLogInForm();
                }
            }

        });

    }

    private void enableLogInForm() {

        mLogInUserNameField.setEnabled(true);
        mLogInPasswordField.setEnabled(true);
        mLogInBtn.setEnabled(true);
    }

    private void CheckAuthentication() {

        String userName = mLogInUserNameField.getText().toString().trim();
        String password = mLogInPasswordField.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {

            Toast.makeText(MainActivity.this, "Field/s are empty", Toast.LENGTH_SHORT).show();
        }

        else {

            // get imei number from device
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();

            if (password.equals(imei)) {

                Intent SuccessIntent = new Intent(MainActivity.this, SuccessActivity.class);
                startActivity(SuccessIntent);
            }

            else {

                Toast.makeText(MainActivity.this, "Failed, try again"  , Toast.LENGTH_LONG).show();
                mLogInUserNameField.setText("");
                mLogInPasswordField.setText("");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
