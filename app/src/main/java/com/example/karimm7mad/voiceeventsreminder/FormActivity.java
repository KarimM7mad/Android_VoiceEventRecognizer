package com.example.karimm7mad.voiceeventsreminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.provider.CalendarContract.*;

public class FormActivity extends AppCompatActivity {
    public Button savebtn = null;
    public EditText eventNameEditTxt = null;
    public EditText datePickerEditTxt = null;
    public EditText timePickerEditTxt = null;
    public Intent saveInCalendar = null;
    public String eventName, eventTimeRecieved, eventDate, eventDay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        //get THE XML components to use
        this.savebtn = this.findViewById(R.id.saveBtn);
        this.datePickerEditTxt = this.findViewById(R.id.datePickerEditTxt);
        this.timePickerEditTxt = this.findViewById(R.id.timePickerEditTxt);
        this.eventNameEditTxt = findViewById(R.id.eventNameEditTxt);
        //get the event name and set it in the eventName Edit Txt

        this.eventName = this.getIntent().getStringExtra("eventName");
        this.eventNameEditTxt.setText(this.eventName);
        displayTimeRecieved();
        displayDateRecieved();



//
//        this.saveInCalendar = new Intent(Intent.ACTION_EDIT);
//        this.saveInCalendar.setType("vnd.android.cursor.item/event");
//
//        this.saveInCalendar.putExtra(Events.TITLE, this.eventName);


        // Setting dates
//        GregorianCalendar calDate = new GregorianCalendar(2012, 10, 02);
//
//
//        this.saveInCalendar.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,calDate.getTimeInMillis());
//        this.saveInCalendar.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,calDate.getTimeInMillis());
//


//        this.saveInCalendar.setData(Events.CONTENT_URI);


        this.savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deal with calender API
                getAllCalendarss();

                int [] timeComponents = getTimeComponents(timePickerEditTxt.getText().toString());
                for (int x : timeComponents){
                    Log.d("asdasd","lastT: time->|"+ x +"|");
                }


                int [] dateComponents = getDateComponentsForSaving(datePickerEditTxt.getText().toString());

                for (int x : dateComponents){
                    Log.d("asdasd","lastT: date->|"+ x +"|");
                }


                Log.d("asdasd","lastT: eventNameAddedis->|"+ eventNameEditTxt.getText().toString() +"|");

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(dateComponents[0], dateComponents[1]-1, dateComponents[2], timeComponents[0], timeComponents[1]);



                Calendar endTime = Calendar.getInstance();
                endTime.set(dateComponents[0], dateComponents[1]-1, dateComponents[2], 11, 59);

                Log.d("asdasd","lastT: beginTime->|"+ beginTime.getTime()+"|");
                Log.d("asdasd","lastT: beginTime->|"+ endTime.getTime()+"|");


                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(Events.DTSTART, beginTime.getTimeInMillis());
                values.put(Events.DTEND, endTime.getTimeInMillis());
                values.put(EXTRA_EVENT_ALL_DAY,true);
                values.put(Events.TITLE, eventNameEditTxt.getText().toString() );
                values.put(Events.DESCRIPTION, "THIS IS SUCH A GREAT APP WITH A GREAT EXPERIENCE TO HAVE");
                // id 2 is for the google account calendar if exists
                values.put(Events.CALENDAR_ID,2) ;
                values.put(Events.EVENT_TIMEZONE,beginTime.getTimeZone().getDisplayName());
                Uri uri = cr.insert(Events.CONTENT_URI, values);


                Toast.makeText(getBaseContext(),"Event Saved Successfully in Google Calender, check it out",Toast.LENGTH_SHORT).show();


            }
        });
        this.timePickerEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.isFocused()) {
                    showTimePickerDialog(v);
                    v.clearFocus();
                }
            }
        });
        this.datePickerEditTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.isFocused()) {
                    showDatePickerDialog(v);
                    v.clearFocus();
                }
            }
        });


    }

    //TIME ISSUES HANDLING
    //---------------------
    //formats supported are "hr:min am/pm" , "hr am/pm"
    public int[] getTimeComponents(String timeStr) {
        int hour = -11;
        int minute = -11;
        String hrMinSplit = timeStr.split(" ")[0];
        String amORpm = timeStr.split(" ")[1];
        //contain Minutes
        if (hrMinSplit.contains(":")) {
            hour = Integer.parseInt(hrMinSplit.split(":")[0]);
            minute = Integer.parseInt(hrMinSplit.split(":")[1]);
        }
        //only hours
        else {
            hour = Integer.parseInt(hrMinSplit);
            minute = 0;
        }
        if (amORpm.equalsIgnoreCase("pm"))
            hour += 12;

        return new int[]{hour, minute};
    }

    public void displayTimeRecieved() {
        //get the event Time and set it in the eventTimeRecieved edit text
        this.eventTimeRecieved = this.getIntent().getStringExtra("time");
        this.eventTimeRecieved = this.eventTimeRecieved.replace(".", "");
        String amORpm = "";
        int[] timeee = this.getTimeComponents(this.eventTimeRecieved);
        this.viewTimeOnEditBox(timeee[0], timeee[1]);
    }

    //the view on a edit text fn to display
    public void viewTimeOnEditBox(int hourOfDay, int minute) {
        String txtTodisplay = "";
        String minuteStr = minute < 10 ? "0" + minute : minute + "";
        //how to view it to the user
        if (hourOfDay == 0)
            txtTodisplay = "12:" + minuteStr + " am";
        else if (hourOfDay == 12)
            txtTodisplay = "12:" + minuteStr + " pm";
        else
            txtTodisplay = (hourOfDay > 12) ? (hourOfDay - 12) + ":" + minuteStr + " pm" : hourOfDay + ":" + minuteStr + " am";
        this.timePickerEditTxt.setText(txtTodisplay);
    }

    //time picker code made by Google
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, false);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ((FormActivity) this.getActivity()).viewTimeOnEditBox(hourOfDay, minute);
        }
    }

    //DATE ISSUES HANDLING
    //---------------------
    //format supported is month day(with th or without) year
    public void displayDateRecieved() {
        this.eventDay = this.getIntent().getStringExtra("eventDay");
        this.eventDate = this.getIntent().getStringExtra("date");
        this.eventDate = this.eventDate.replaceAll("(th|rd|st|nd)", "");
        //date format entered is "month day year"
        String[] dateComponents = this.eventDate.split(" ");
        int month = this.getMonthInCalendarLibraries(dateComponents[0]);
        int day = Integer.parseInt(dateComponents[1]);
        int year = Integer.parseInt(dateComponents[2]);
        this.viewDateOnEditBox(year, month, day);
    }

    public int[] getDateComponentsForSaving(String datee) {
        String[] dateComponents = datee.split("/");
        int year = Integer.parseInt(dateComponents[2]);
        int month = Integer.parseInt(dateComponents[1]);
        int day = Integer.parseInt(dateComponents[0]);
        return new int[]{year, month, day};
    }

    //the view on a edit text fn to display
    public void viewDateOnEditBox(int year, int month, int day) {
        this.datePickerEditTxt.setText(day + "/" + (month + 1) + "/" + year);
    }

    //get month according to Google libraries
    public int getMonthInCalendarLibraries(String month) {
        month = month.toUpperCase();
        switch (month) {
            case "JANUARY":
                return Calendar.JANUARY;
            case "FEBRUARY":
                return Calendar.FEBRUARY;
            case "MARCH":
                return Calendar.MARCH;
            case "APRIL":
                return Calendar.APRIL;
            case "MAY":
                return Calendar.MAY;
            case "JUNE":
                return Calendar.JUNE;
            case "JULY":
                return Calendar.JULY;
            case "AUGUST":
                return Calendar.AUGUST;
            case "SEPTEMBER":
                return Calendar.SEPTEMBER;
            case "OCTOBER":
                return Calendar.OCTOBER;
            case "NOVEMBER":
                return Calendar.NOVEMBER;
            case "DECEMBER":
                return Calendar.DECEMBER;
        }
        return -5000;

    }

    //date picker code made by Google
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            ((FormActivity) this.getActivity()).viewDateOnEditBox(year, month, day);


        }

    }


    public void getAllCalendarss() {
        String[] projection = new String[] { "_id", "name" };
        Uri calendars = Calendars.CONTENT_URI;
        Cursor managedCursor = this.getContentResolver().query(calendars, projection, null, null, null);
        if (managedCursor.moveToFirst()) {
            String calName;
            String calId;
            int nameColumn = managedCursor.getColumnIndex("name");
            int idColumn = managedCursor.getColumnIndex("_id");
            do {
                calName = managedCursor.getString(nameColumn);
                calId = managedCursor.getString(idColumn);
                Log.d("asdasd", "lastT: Name:"+calName);
                Log.d("asdasd", "lastT: id:"+calId);
            } while (managedCursor.moveToNext());
        }
        else Toast.makeText(this.getBaseContext(),"sth wrong with event Adition :check get all calendars",Toast.LENGTH_SHORT).show();
    }


}


