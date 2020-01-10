package es.upv.gnd.letslock;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.view.View.VISIBLE;

public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    private FloatingActionButton fab;
    private FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    private ListView listOfMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listOfMessage = findViewById(R.id.list_of_message);

        fab = findViewById(R.id.flecha);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.mensaje);
                final String texto = input.getText().toString().toLowerCase();
                FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                input.setText("");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        int entra = 0;

                        if (texto.contains("hola") || texto.contains("saludos")) {

                            FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage("Hola", FirebaseAuth.getInstance().getCurrentUser().getUid(), "Bot"));
                            entra++;
                        }
                        if (texto.contains("gracias")) {

                            FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage("De nada :)", FirebaseAuth.getInstance().getCurrentUser().getUid(), "Bot"));
                            entra++;
                        }
                        if (texto.contains("chiste")) {

                            FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage("Eran dos y se cayó el del medio", FirebaseAuth.getInstance().getCurrentUser().getUid(), "Bot"));
                            entra++;
                        }
                        if (texto.contains("no") && texto.contains("abr")) {

                            FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage("Contacta con el servicio técnico: 664410457", FirebaseAuth.getInstance().getCurrentUser().getUid(), "Bot"));
                            entra++;
                        }
                        if ((texto.contains("app") || texto.contains("aplicacion")) && (texto.contains("no") || texto.contains("error"))) {

                            FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage("Mandanos un correo a letslockgrupo02@gmail.com", FirebaseAuth.getInstance().getCurrentUser().getUid(), "Bot"));
                            entra++;
                        }

                        if (texto.contains("envi") && texto.contains("pin")) {

                            FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage("Para enviar un pin vaya a Inicio, pulse en enviar código y rellene los datos", FirebaseAuth.getInstance().getCurrentUser().getUid(), "Bot"));
                            entra++;
                        }
                        if (texto.contains("permis")) {

                            FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage("Solo los propietarios pueden cambiar permisos y acceder a más información", FirebaseAuth.getInstance().getCurrentUser().getUid(), "Bot"));
                            entra++;
                        }
                        if (entra == 0) {
                            FirebaseDatabase.getInstance().getReference("chatBot").push().setValue(new ChatMessage("No te he entendido, llama al servicio técnico 664410457", FirebaseAuth.getInstance().getCurrentUser().getUid(), "Bot"));
                        }
                    }
                }, 2000);
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            displayChatMessage();
        }
    }

    private void displayChatMessage() {

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_items, FirebaseDatabase.getInstance().getReference("chatBot")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                ImageView messageFoto;
                TextView messageText, messageUser, mymessage;

                messageText = v.findViewById(R.id.message_body);
                messageUser = v.findViewById(R.id.name);
                messageFoto = v.findViewById(R.id.imageView3);
                mymessage = v.findViewById(R.id.my_message_body);

                if (model.getMessageUser().equals(usuario.getUid()) && !model.getNameUser().equals("Bot")) {

                    mymessage.setText(model.getMessageText());
                    mymessage.setVisibility(VISIBLE);
                    messageFoto.setVisibility(View.GONE);
                    messageText.setVisibility(View.GONE);
                    messageUser.setVisibility(View.GONE);

                } else if (model.getNameUser().equals("Bot") && model.getMessageUser().equals(usuario.getUid())) {

                    messageText = v.findViewById(R.id.message_body);
                    messageUser = v.findViewById(R.id.name);

                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getNameUser());

                    messageText.setVisibility(VISIBLE);
                    messageUser.setVisibility(VISIBLE);
                    messageFoto.setVisibility(VISIBLE);
                    mymessage.setVisibility(View.GONE);
                }
            }
        };
        listOfMessage.setAdapter(adapter);
    }
}
