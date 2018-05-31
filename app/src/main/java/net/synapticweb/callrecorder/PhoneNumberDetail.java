package net.synapticweb.callrecorder;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.synapticweb.callrecorder.databases.ListenedContract;
import net.synapticweb.callrecorder.databases.RecordingsDbHelper;

public class PhoneNumberDetail extends AppCompatActivity {
    TextView syncContact;
    Intent intent;
    boolean hasSyncedContact;
    String lookupKey = null;
    String typePhone = null;
    String contactName = null;
    String photoUri = null;
    TextView contactNameView;
    TextView typePhoneView;
    TextView phoneNumberView;
    ImageView contactPhotoView;
    static final int FOUND_CONTACT = 1;
    static final int NOTFOUND_CONTACT = 0;
    static final int ERROR = -1;

    private int getFieldsFromSyncedContact(String phoneNumber) {
        Uri phoneLookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber) );
        Cursor cursor = getContentResolver()
                .query(phoneLookupUri, new String[]{ContactsContract.PhoneLookup.TYPE, ContactsContract.PhoneLookup.LOOKUP_KEY},
                        null, null, null);
        if(cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.LOOKUP_KEY));
                int typeCode = cursor.getInt(cursor.getColumnIndex(ContactsContract.PhoneLookup.TYPE));

                switch (typeCode) {
                    case 1:
                        typePhone = "Home: ";
                        break;
                    case 2:
                        typePhone = "Mobile: ";
                        break;
                    default:
                        typePhone = "Other: ";
                }
                cursor.close();

                cursor = getContentResolver().
                        query(ContactsContract.Contacts.CONTENT_URI,
                                new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_URI},
                                ContactsContract.Contacts.LOOKUP_KEY + "='" + lookupKey + "'", null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                    cursor.close();
                }
                return FOUND_CONTACT;
            }
            else
                return NOTFOUND_CONTACT;
        }

        return ERROR;
    }

    private void updateDBRepopulateViews() {
        getFieldsFromSyncedContact(intent.getStringExtra("phone_number"));

        RecordingsDbHelper mDbHelper = new RecordingsDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ListenedContract.Listened.COLUMN_NAME_UNKNOWN_PHONE, 0);
        values.put(ListenedContract.Listened.COLUMN_NAME_CONTACT_PHOTO_URI, photoUri);
        values.put(ListenedContract.Listened.COLUMN_NAME_PHONE_TYPE, typePhone);
        values.put(ListenedContract.Listened.COLUMN_NAME_CONTACT_NAME, contactName);

        db.update(ListenedContract.Listened.TABLE_NAME,
                values, "phone_number='" + intent.getStringExtra("phone_number") + "'", null);

        syncContact.setVisibility(View.GONE);
        if(photoUri != null)
            contactPhotoView.setImageURI(Uri.parse(photoUri));
        else
            contactPhotoView.setImageResource(R.drawable.user_contact_blue);

        contactNameView.setText(getSpannedText(String.format(getResources().getString(
                R.string.detail_contactname_intro), contactName)));
        typePhoneView.setText(getSpannedText(String.format(getResources().getString(
                R.string.detail_phonetype_intro), typePhone)));
        phoneNumberView.setText(getSpannedText(String.format(getResources().getString(
                R.string.detail_phonenumber_intro), intent.getStringExtra("phone_number"))));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(hasSyncedContact)
        {
           updateDBRepopulateViews();
            hasSyncedContact = false;
            }
        }

    private Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else
            return Html.fromHtml(text);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonenumber_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        syncContact = findViewById(R.id.sync_contact);
        if(intent.getBooleanExtra("unknown_phone", false))
            syncContact.setVisibility(View.VISIBLE);

        contactNameView = findViewById(R.id.contact_name_detail);
        contactNameView.setText(getSpannedText(String.format(getResources().getString(
                R.string.detail_contactname_intro), intent.getStringExtra("contact_name"))));
        typePhoneView = findViewById(R.id.phone_type_detail);
        typePhoneView.setText(getSpannedText(String.format(getResources().getString(
                R.string.detail_phonetype_intro), intent.getStringExtra("phone_type"))));
        phoneNumberView = findViewById(R.id.phone_number_detail);
        phoneNumberView.setText(getSpannedText(String.format(getResources().getString(
                R.string.detail_phonenumber_intro), intent.getStringExtra("phone_number"))));
        contactPhotoView = findViewById(R.id.contact_photo_detail);
        photoUri = intent.getStringExtra("contact_photo_uri");

        if(photoUri != null)
            contactPhotoView.setImageURI(Uri.parse(photoUri));
        else {
            if(intent.getBooleanExtra("unknown_phone", false))
                contactPhotoView.setImageResource(R.drawable.user_contact_red);
            else
                contactPhotoView.setImageResource(R.drawable.user_contact_blue);
        }

        syncContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = intent.getStringExtra("phone_number");

                if(getFieldsFromSyncedContact(phoneNumber) == NOTFOUND_CONTACT)
                {
                        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
                        hasSyncedContact = true;
                        startActivity(intent);
                } else
                    updateDBRepopulateViews();
                //comm

            }

        });
    }


}
