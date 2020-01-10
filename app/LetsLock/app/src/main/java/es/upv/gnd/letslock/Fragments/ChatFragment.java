package es.upv.gnd.letslock.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import es.upv.gnd.letslock.bbdd.ChatMessage;
import es.upv.gnd.letslock.PopUpChat;
import es.upv.gnd.letslock.R;

import static android.view.View.VISIBLE;

public class ChatFragment extends Fragment {

    View vista;
    private FirebaseListAdapter<ChatMessage> adapter;
    FloatingActionButton fab;
    final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
    private boolean anonimo = false;
    private ListView listOfMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (prefs.contains("anonimo")) anonimo = prefs.getBoolean("anonimo", false);

        if (!anonimo) {

            vista = inflater.inflate(R.layout.fragment_chat, container, false);

            listOfMessage = vista.findViewById(R.id.list_of_message);

            fab = vista.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText input = vista.findViewById(R.id.input);
                    Log.d("Chat", input.getText().toString());
                    FirebaseDatabase.getInstance().getReference("chat").push().setValue(new ChatMessage(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                    input.setText("");
                    listOfMessage.post(new Runnable() {
                        @Override
                        public void run() {
                            listOfMessage.setSelection(adapter.getCount() - 1);
                        }
                    });
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


        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, R.layout.list_items, FirebaseDatabase.getInstance().getReference("chat")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                ImageView messageFoto;
                TextView messageText, messageUser, myMessage;

                myMessage = v.findViewById(R.id.my_message_body);
                messageText = v.findViewById(R.id.message_body);
                messageUser = v.findViewById(R.id.name);
                messageFoto = v.findViewById(R.id.imageView3);

                messageFoto.setVisibility(View.GONE);


                if(model.getMessageUser().equals(usuario.getUid())) {

                    myMessage.setText(model.getMessageText());
                    myMessage.setVisibility(VISIBLE);
                    messageText.setVisibility(View.GONE);
                    messageUser.setVisibility(View.GONE);

                } else {


                    final String user = model.getMessageUser();


                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getNameUser());

                    messageText.setVisibility(VISIBLE);
                    messageUser.setVisibility(VISIBLE);
                    myMessage.setVisibility(View.GONE);


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
