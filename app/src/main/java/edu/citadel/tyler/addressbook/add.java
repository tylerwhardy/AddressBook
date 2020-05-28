package edu.citadel.tyler.addressbook;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.Intent;
import android.content.CursorLoader;
import android.widget.*;
import android.view.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ContentUris;
import android.util.Log;

import java.util.zip.Inflater;

public class add extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static add newInstance(String param1, String param2, String param3) {
        String name = "Name";
        String number = "Phone";
        String type = "Null";

        add fragment = new add();
        Bundle args = new Bundle();
        args.putString(name, param1);
        args.putString(number, param2);
        args.putString(type, param3);
        fragment.setArguments(args);
        return fragment;
    }

    public add() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = "Name";
        String number = "Phone";
        final String[] type = {"Null"};

        View rootView;
        Inflater inflater = new Inflater();
        rootView = inflater.inflate(R.layout.activity_main, container, false);
        Button submit;
        submit = (Button)rootView.findViewById(R.id.buttonSubmit);

        EditText editName;
        editName = (EditText)rootView.findViewById(R.id.editName);

        EditText editPhone;
        editPhone = (EditText)rootView.findViewById(R.id.editName);

        RadioGroup radioType;
        radioType = (RadioGroup)rootView.findViewById(R.id.radioType);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int selection) { // Radio Button Functionality
                switch (selection) {
                    case R.id.radioHome:
                        type[0] = "Home";
                        break;
                    case R.id.radioMobile:
                        type[0] = "@string/phone";
                        break;
                    case R.id.radioOther:
                        type[0] = "string/";
                        break;
                    case R.id.radioWork:
                        type[0] = " ";
                        break;
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
