package edu.citadel.tyler.addressbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

public class MainActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final boolean DEBUG = false;
    private static final String  LOG_TAG = "MainActivity";

    private static final String[] VIEW_COLUMNS = { "name",    "phone_num" };
    private static final int[]    VIEWS        = { R.id.name, R.id.phoneNum };


    private SimpleCursorAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, add.class);
                startActivity(intent);
                updateContactList();
            }
        });

        // Create an empty adapter for displaying the loaded data
        adapter = new SimpleCursorAdapter(this, R.layout.contact_item, null, VIEW_COLUMNS, VIEWS, 0);
        setListAdapter(adapter);

        ListView lv = getListView();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.confirmDelete)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int buttonId)
                            {
                                Uri itemUri = ContentUris.withAppendedId(EmergencyContract.CONTENT_URI, id);
                                getContentResolver().delete(itemUri, null, null);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int buttonId)
                            {
                                // nothing to do - user cancelled the dialog
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });

        // Initialize the loader manager.
        getLoaderManager().initLoader(0, null, this);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id)
            {
                ViewGroup vg = (ViewGroup) view;
                TextView nameView = (TextView) vg.getChildAt(0);
                String name = nameView.getText().toString();
                TextView phoneView = (TextView) vg.getChildAt(1);
                String phoneNum = phoneView.getText().toString();
                TextView phoneTypeView = (TextView) vg.getChildAt(2);
                String phoneType = phoneTypeView.getText().toString();

                String message
                        = "position = "  + position + ",\n"
                        + "id = "        +  id      + ",\n"
                        + "name = "      + name     + ",\n"
                        + "phoneNum = "  + phoneNum + ",\n"
                        + "phoneType = " + phoneType;
                Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
                toast.show();

                return true;
            }
        });

        // Create an empty adapter for displaying the loaded data
        adapter = new SimpleCursorAdapter(this, R.layout.contact_item, null, VIEW_COLUMNS, VIEWS, 0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder()
        {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex)
            {
                if (view.getId() == R.id.phoneType)
                {
                    TextView phoneTypeView = (TextView) view;
                    int phoneType = cursor.getInt(columnIndex);

                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                        phoneTypeView.setText(R.string.phoneTypeHome);
                    else if ((phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE))
                        phoneTypeView.setText(R.string.phoneTypeMobile);
                    else if ((phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_WORK))
                        phoneTypeView.setText(R.string.phoneTypeWork);
                    else
                        phoneTypeView.setText(R.string.phoneTypeOther);

                    return true;
                }
                return false;
            }
        });

        setListAdapter(adapter);

        // Initialize the loader manager.
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        ViewGroup vg = (ViewGroup) v;
        TextView nameView = (TextView) vg.getChildAt(0);
        String name = nameView.getText().toString();
        TextView phoneView = (TextView) vg.getChildAt(1);
        String phoneNum = phoneView.getText().toString();

        callContact(name, phoneNum); // Calls selected phone number
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Uri uri = EmergencyContract.CONTENT_URI;
        return new CursorLoader(this, uri, EmergencyContract.COLUMNS, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (DEBUG)
            toastDb(data);

        // swap in the cursor
        adapter.swapCursor(data);
    }

    /**
     * Initiates a telephone call to the specified phone number.
     */
    private void callContact(String name, String phoneNum)
    {
        Uri uri = Uri.parse("tel:" + phoneNum);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        startActivity(intent);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        adapter.swapCursor(null);
    }
    /**
     * Updates the emergency contacts on the user screen.
     */
    private void updateContactList()
    {
        try
        {
            SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
            adapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            String errorMsg = "Error updating emergency contacts";
            Log.e(LOG_TAG, errorMsg, ex);
        }
    }

    public void toastDb(Cursor cursor)
    {
        while (cursor.moveToNext())
        {
            int    id       = cursor.getInt(0);
            String name     = cursor.getString(1);
            String phoneNum = cursor.getString(2);

            String message = "ID: " + id + ", Name: " + name
                    + ", Phone Number: " + phoneNum;
            Toast toast = Toast.makeText(MainActivity.this,
                    message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static final class EmergencyContract
    {
        public static final String DB_NAME    = "emergency.db";
        public static final int    DB_VERSION = 1;

        public static final String TABLE_NAME = "emergency_contacts";
        public static final String[]  COLUMNS = { "_id", "name", "phone_num" };

        public static final String AUTHORITY = "edu.citadel.tyler.addressbook";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);


        private EmergencyContract() {}   // prevent instantiation
    }

    public class EmergencyDbOpenHelper extends SQLiteOpenHelper {
        /**
         * Construct a SQLiteOpenHelper object for the
         * emergency database.
         */
        public EmergencyDbOpenHelper(Context context) {
            super(context, EmergencyContract.DB_NAME, null, EmergencyContract.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createSql =
                    "create table " + EmergencyContract.TABLE_NAME
                            + "("
                            + "  _id integer primary key autoincrement,"
                            + "  name      text not null,"
                            + "  phone_num text not null"
                            + ")";
            db.execSQL(createSql);
            insertContacts(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // This is version 1 so no actions are required.
            // Possible actions include dropping/recreating
            // tables, saving/restoring data in tables, etc.
        }

        private void insertContacts(SQLiteDatabase db) {
            // perform inserts to initialize the database
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}