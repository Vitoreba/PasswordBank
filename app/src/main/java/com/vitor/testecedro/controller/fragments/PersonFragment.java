package com.vitor.testecedro.controller.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vitor.testecedro.R;
import com.vitor.testecedro.controller.LoginActivity;
import com.vitor.testecedro.util.Global;

public class PersonFragment extends Fragment {

    private TextView txtViewPersonEmail;
    private Button btnLogoff;

    private SharedPreferences sharedPreferences;

    public PersonFragment() {
        // Required empty public constructor
    }

    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        txtViewPersonEmail = view.findViewById(R.id.txtViewPersonEmail);
        btnLogoff = view.findViewById(R.id.btnLogoff);

        sharedPreferences = getActivity().getSharedPreferences(Global.PREFERENCES_FILE,
                Context.MODE_PRIVATE);

        txtViewPersonEmail.setText(
            sharedPreferences.getString(
                    Global.PREFERENCES_PERSON_EMAIL_KEY,
                ""
            )
        );

        btnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoff(view);
            }
        });
    }

    private void logoff(View view) {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
