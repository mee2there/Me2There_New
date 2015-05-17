package com.innovation.me2there;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ashley on 1/25/15.
 */
public class DataStore {

    static public List<EventDetailVO> _allEvents;
    static public MongoDB activityDB;

    static public List<EventDetailVO> getActivities(){
        return _allEvents;
    }

    public String[] eventNameList() {

        List<String> eventNames = new ArrayList<String>();
        for (EventDetailVO event : _allEvents) {
            eventNames.add(event.getEventName());
        }
        return eventNames.toArray(new String[eventNames.size()]);

    }

    public List<EventDetailVO> getAllEvents() {
        return _allEvents;
    }

    public EventDetailVO getEventForID(long id) {

      int index = (int)id;
      EventDetailVO[] eventArray = _allEvents.toArray(new EventDetailVO[_allEvents.size()]);
      return eventArray[index];
    }

    DataStore(final Double latitude, final Double longitude) {

        AsyncTask<Void, Void, MongoDB> task = new AsyncTask<Void, Void, MongoDB>() {

            @Override
            protected MongoDB doInBackground(Void... params) {
                // Put in code over here that does the network related operations.
                MongoDB db = new MongoDB();
                _allEvents = db.getEvents(latitude, longitude);

                return db;
            }

        };
        task.execute();
        try {
            activityDB = task.get();
        }catch(ExecutionException e){
            Log.i("DataStore", "Init" + e.getMessage());
        }catch(InterruptedException i){
            Log.i("DataStore", "Init" + i.getMessage());
        } catch (Exception e0) {
            Log.i("DataStore", "Init" + e0.getMessage());
        }
    }

     public static void insertActivity(EventDetailVO toCreate){
        AsyncTask<EventDetailVO, Void, List<EventDetailVO>> task = new AsyncTask<EventDetailVO, Void, List<EventDetailVO>>() {

            @Override
            protected List<EventDetailVO> doInBackground(EventDetailVO... events) {
                EventDetailVO eventToCreate = events[0];
                // Put in code over here that does the network related operations.
                boolean insertResult = activityDB.insertEvent(eventToCreate.getEventName(),
                                                              eventToCreate.getEventDesc(),
                                                              eventToCreate.getEventTime(),
                        eventToCreate.getEventLocAsArray(),
                        eventToCreate.getEventImageID()
                                                              );
                return activityDB.getEvents(eventToCreate.getEventLocAsArray()[0], eventToCreate.getEventLocAsArray()[1]);
            }

        };
        task.execute(toCreate);
        try {
            _allEvents = task.get();
        }catch(ExecutionException e){
            Log.i("DataStore", "insertActivity" + e.getMessage());
        } catch (InterruptedException i) {
            Log.i("DataStore", "insertActivity" + i.getMessage());
        } catch (Exception e0) {
            Log.i("DataStore", "insertActivity" + e0.getMessage());
        }
     }

    public static String uploadImage(Bitmap toUpdate, String infileName) {
        final String fileName = infileName;
        AsyncTask<Bitmap, Void, String> task = new AsyncTask<Bitmap, Void, String>() {

            @Override
            protected String doInBackground(Bitmap... images) {
                Bitmap imageToUpload = images[0];
                // Put in code over here that does the network related operations.
                return activityDB.storeImage(imageToUpload, fileName);

            }

        };
        task.execute(toUpdate);
        try {
            return task.get();
        } catch (ExecutionException e) {
            Log.i("DataStore ", "uploadImage" + e.getMessage());
        }catch(InterruptedException i){
            Log.i("DataStore", "uploadImage" + i.getMessage());
        } catch (Exception e0) {
            Log.i("DataStore", "uploadImage" + e0.getMessage());
        }
        return null;
    }

    public static Bitmap downloadImage(String imageToGet) {
        Bitmap downloadedImage = null;
        AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... imagesToGet) {
                Log.i("DataStore", "Download image" + imagesToGet[0]);
                // Put in code over here that does the network related operations.
                return activityDB.getImage(imagesToGet[0]);

            }

        };
        task.execute(imageToGet);
        try {
            downloadedImage = task.get();
        } catch (ExecutionException e) {
            Log.i("DataStore", "downloadImage" + e.getMessage());
        } catch (InterruptedException i) {
            Log.i("DataStore", "downloadImage" + i.getMessage());
        } catch (Exception e0) {
            Log.i("DataStore", "downloadImage" + e0.getMessage());
        }
        return downloadedImage;
    }
}



