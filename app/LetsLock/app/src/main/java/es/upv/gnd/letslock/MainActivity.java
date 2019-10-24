import android.widget.TextView;
package es.upv.gnd.letslock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
public class MainActivity extends AppCompatActivity {


    public static final String user = "names";
    TextView txtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* txtUser = (TextView) findViewById(R.id.textUser);
        String user = getIntent().getStringExtra("names");
        txtUser.setText("¡Bienvenido "+ user +"!");*/

    }
}