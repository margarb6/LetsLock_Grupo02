package es.upv.gnd.letslock.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import es.upv.gnd.letslock.ChatMessage;
import es.upv.gnd.letslock.DescargarFoto;
import es.upv.gnd.letslock.PopUpChat;
import es.upv.gnd.letslock.R;

import static android.view.View.VISIBLE;

public class ChatFragment extends Fragment {

    View vista;
    private FirebaseListAdapter<ChatMessage> adapter;
    FloatingActionButton fab;
    final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    private boolean anonimo = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (prefs.contains("anonimo")) anonimo = prefs.getBoolean("anonimo", false);

        if (!anonimo) {

            vista = inflater.inflate(R.layout.fragment_chat, container, false);

            fab = vista.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText input = vista.findViewById(R.id.input);
                    Log.d("Chat", input.getText().toString());
                    FirebaseDatabase.getInstance().getReference("chat").push().setValue(new ChatMessage(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                    input.setText("");
                }
            });

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                Snackbar.make(vista, "No user", Snackbar.LENGTH_SHORT).show();

            } else {

                displayChatMessage();

            }

        } else {

            vista = inflater.inflate(R.layout.fragment_anonimo, container, false);

        }

        return vista;

    }


    private void displayChatMessage() {


        ListView listOfMessage = vista.findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, R.layout.list_items, FirebaseDatabase.getInstance().getReference("chat")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                if(model.getMessageUser().equals(usuario.getUid())) {

                    TextView messageText;
                    messageText = v.findViewById(R.id.my_message_body);

                    messageText.setText(model.getMessageText());
                    messageText.setVisibility(VISIBLE);

                } else {


                    TextView messageText, messageUser, messageTime;
                    final String user = model.getMessageUser();

                    messageText = v.findViewById(R.id.message_body);
                    messageUser = v.findViewById(R.id.name);
                    //messageTime = v.findViewById(R.id.message_time);
                    //messageFoto = v.findViewById(R.id.avatar_pop);

                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getNameUser());
                    //messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

                    messageText.setVisibility(VISIBLE);
                    messageUser.setVisibility(VISIBLE);


                    messageUser.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            Intent intent = new Intent(getContext(), PopUpChat.class);
                            intent.putExtra("user_id", user);
                            startActivity(intent);
                        }
                    });

                }


            }
        };
        listOfMessage.setAdapter(adapter);
    }

}
