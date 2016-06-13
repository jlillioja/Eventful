package io.github.jlillioja.eventful;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class AddEventTask extends AsyncTask<Void, Void, String> {
    private final TextView mOutputText;
    private com.google.api.services.calendar.Calendar mService = null;
    private String eventString;

    public AddEventTask(GoogleAccountCredential credential, String event, TextView output) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Eventful")
                .build();
        this.eventString = event;
        mOutputText = output;
    }

    /**
     * Background task to call Google Calendar API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            return addEvent();
        } catch (Exception e) {
            cancel(true);
            return null;
        }
    }

    private String addEvent() throws IOException {
        return mService.events().quickAdd("primary", eventString).execute().toPrettyString();
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     *
     * @return List of Strings describing returned events.
     * @throws IOException
     *//*
    private List<String> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                // All-day events don't have start times, so just use
                // the start date.
                start = event.getStart().getDate();
            }
            eventStrings.add(
                    String.format("%s (%s)", event.getSummary(), start));
        }
        return eventStrings;
    }*/


    @Override
    protected void onPreExecute() {
        mOutputText.setText("");
    }

    @Override
    protected void onPostExecute(String output) {
        if (output == null || output.isEmpty()) {
            mOutputText.setText("No results returned.");
        } else {
            mOutputText.setText(output);
        }
    }
}
