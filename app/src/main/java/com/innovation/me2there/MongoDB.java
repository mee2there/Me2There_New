package com.innovation.me2there;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by ashley on 2/10/15.
 */
public class MongoDB {

    private DBCollection eventCollection;
    private DB db;
    private GridFS fs;

    private Double defaultMaxDistance = 0.10;
    public MongoDB(){
        //final BasicDBObject[] seedData = createSeedData();
        connectDB();
        //eventCollection.insert(seedData);
        //addEvents();
    }

    private void  connectDB() {
        try {
            //mongodb://mee2thereservice:rva@2015@ds041831.mongolab.com:41831/mee2theredb
            //mongodb://<dbuser>:<dbpassword>@ds041831.mongolab.com:41831/mee2theredb
            //mongodb://mee2thereadmin:there2me@ds041831.mongolab.com:41831/mee2theredb
            // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
            MongoClientURI uri = new MongoClientURI("mongodb://mee2thereadmin:there2me@ds041831.mongolab.com:41831/mee2theredb");
            MongoClient client = new MongoClient(uri);
            DB db = client.getDB(uri.getDatabase());

            Log.i("connectDB","after getDB : "+db.toString());

            Set<String> collections = db.getCollectionNames();

            for (String collectionName : collections) {

                Log.i("connectDB","Collection Name : "+collectionName);
            }




        /*
         * First we'll add a few songs. Nothing is required to create the
         * songs collection; it is created automatically when we insert.
         */

            //eventCollection = db.getCollection("events");
            //eventCollection.createIndex(new BasicDBObject("loc", "2d"), "geospacialIdx");

            //Log.i("connectDB","after eventCollection : "+eventCollection.toString());

            //fs = new GridFS(db);
        }catch (UnknownHostException unknownHost) {
            Log.e("connectDB","Exception : "+unknownHost.getMessage());

        }
    }

    public boolean insertEvent(String eventName,
                               String eventDesc,
                               Date eventDate,
                               double[] location,
                               Object imageID
    ) {
        BasicDBObject newEvent = new BasicDBObject();
        newEvent.put("name", eventName);
        newEvent.put("description", eventDesc);
        newEvent.put("date", eventDate);
        newEvent.put("imageId", imageID);


        newEvent.put("loc", location);

        eventCollection.insert(newEvent);
        return true;
    }

    public List<EventDetailVO> getEvents(Double latitude, Double longitude) {
        BasicDBObject query = new BasicDBObject("i", new BasicDBObject("$ne", ""));
        List<EventDetailVO> eventVOList = new ArrayList<EventDetailVO>();
        //DBCursor cursor = eventCollection.find(query);
        final BasicDBObject filterNearMe = new BasicDBObject("$near", new double[] { latitude, longitude });
        filterNearMe.put("$maxDistance", defaultMaxDistance);
        List<DBObject> results = nearWithMaxDistance(filterNearMe);
        int index = 0;
        for(DBObject dbResult : results) {
            BasicDBObject dbObject= (BasicDBObject)dbResult;
            /*Double[] latNLongArray = new Double[2];
            latNLongArray = (Double[])((BasicDBList) dbResult.get("loc")).toArray(latNLongArray);
            LatLng latLngObj = new LatLng(latNLongArray[0],latNLongArray[1]);
            Log.i("MongoDB","resutl "+dbResult);

            Log.i("MongoDB","Coordinates "+latLngObj.toString());*/
            eventVOList.add(new EventDetailVO(index,
                                              dbObject.getString("name") ,
                                              dbObject.getString("description"),
                    dbObject.getDate("date"),
                    null,
                    dbObject.get("imageId").toString()
                                              ));
            index++;
        }
        return eventVOList;
    }


    public List<DBObject> nearWithMaxDistance(BasicDBObject filterNearMe ) {
        //final BasicDBObject filter = new BasicDBObject("$near", new double[] { 43.7252504, -79.4578886 });
        //filter.put("$maxDistance", 0.01);
        //filter.put("$maxDistance", maxDistance);
        final BasicDBObject query = new BasicDBObject("loc", filterNearMe);
        return eventCollection.find(query).limit(10).toArray();


    }

    public void withinBoxExample() {
        final LinkedList<double[]> box = new LinkedList<double[]>();

        // Set the lower left point - Washington square park
        box.addLast(new double[] {  -73.99756, 40.73083 });

        // Set the upper right point - Flatiron Building
        box.addLast(new double[] { -73.988135, 40.741404 });

        final BasicDBObject query
                = new BasicDBObject("loc", new BasicDBObject("$within", new BasicDBObject("$box", box)));

        int count = 0;
        for (final DBObject venue : eventCollection.find(query).toArray()) {
            //System.out.println("---- near venue: " + venue.get("name"));
            count++;
        }

    }

    public void withinPolygonExample() {

        final LinkedList<double[]> polygon = new LinkedList<double[]>();

        // Long then lat

        // Create the shape.
        polygon.addLast(new double[] {  -73.99756, 40.73083 });
        polygon.addLast(new double[] { -73.988135, 40.741404 });
        polygon.addLast(new double[] { -73.99171, 40.738868  });

        final BasicDBObject query
                = new BasicDBObject("loc", new BasicDBObject("$within", new BasicDBObject("$polygon", polygon)));

        int count = 0;
        for (final DBObject venue : eventCollection.find(query).toArray()) {
            //System.out.println("---- near venue: " + venue.get("name"));
            count++;
        }

    }

    public void withinCircleExample() {
        final LinkedList circle = new LinkedList();

        // Set the center - 10gen Office
        circle.addLast(new double[] { -73.99171, 40.738868 });

        // Set the radius
        circle.addLast(0.01);

        final BasicDBObject query
                = new BasicDBObject("loc", new BasicDBObject("$within", new BasicDBObject("$center", circle)));

        int count = 0;
        for (final DBObject venue : eventCollection.find(query).toArray()) {
            //System.out.println("---- near venue: " + venue.get("name"));
            count++;
        }

    }

    public void fast() {
        for (int idx=0; idx < 10000; idx++) nearSphereWIthMaxDistance();
    }


    public void nearSphereWIthMaxDistance() {
        final BasicDBObject filter = new BasicDBObject("$nearSphere", new double[] { -73.99171, 40.738868 });
        //filter.put("$maxDistance", 0.002572851730235);
        //filter.put("$maxDistance", 0.0036998565637149016);
        filter.put("$maxDistance", 0.003712240453784);

        // Radius of the earth: 3959.8728
        // Distance to Maplewood, NJ (in radians): 0.0036998565637149016
        // Distance to Maplewood, NJ (in miles 0.0036998565637149016  * 3959.8728): 14.65

        //db.example.find( { loc: { $nearSphere: [ -73.99171, 40.738868 ], $maxDistance: 0.0036998565637149016 }});

        // To get a list of all places (with distance in radians):
        //db.runCommand( { geoNear : "example" , near : [-73.99171,40.738868], spherical: true } );
        //db.runCommand( { geoNear : "example" , near : [-73.99171,40.738868], maxDistance : 0.0036998565637149016, spherical: true } );

        final BasicDBObject query = new BasicDBObject("loc", filter);

        int count = 0;
        for (final DBObject venue : eventCollection.find(query).toArray()) {
            //System.out.println("---- near venue: " + venue.get("name"));
            count++;
        }

    }
    // Extra helper code

    public static BasicDBObject[] createSeedData(){

        BasicDBObject sampleEvent1 = new BasicDBObject();
        sampleEvent1.put("name", "Hiking");
        sampleEvent1.put("description", "Hiking on mount everest");
        sampleEvent1.put("date", new Date());
        BasicDBObject location1 = new BasicDBObject();
        location1.put("x", 1);
        location1.put("y", 1);
        sampleEvent1.put("loc", location1);

        BasicDBObject sampleEvent2 = new BasicDBObject();
        sampleEvent2.put("name", "Golfing");
        sampleEvent2.put("description", "Golfing in California");
        sampleEvent2.put("date", new Date());
        BasicDBObject location2 = new BasicDBObject();
        location2.put("x", 1);
        location2.put("y", 1);
        sampleEvent2.put("loc", location2);

        BasicDBObject sampleEvent3 = new BasicDBObject();
        sampleEvent3.put("name", "Biking");
        sampleEvent3.put("description", "Biking in Hawai");
        sampleEvent3.put("date", new Date());
        BasicDBObject location3 = new BasicDBObject();
        location3.put("x", 1);
        location3.put("y", 1);
        sampleEvent3.put("loc", location3);

        final BasicDBObject[] seedData = {sampleEvent1, sampleEvent2, sampleEvent3};

        return seedData;
    }

    /*private void addEvents() {
        insertEvent("Hiking","Big time hiking",new Date(),new double[] {43.7048247, -79.3497419});
        insertEvent("Skiing","Big time hiking",new Date(),new double[] {43.7148218, -79.3580521});
        insertEvent("Bowling","Big time hiking",new Date(),new double[] {43.7466070, -79.4105959});
        insertEvent("Cycling","Big time hiking",new Date(),new double[] {43.7252504, -79.4578886});
        insertEvent("Kayaking","Big time hiking",new Date(),new double[] {43.7252504, -79.4578886});
        insertEvent("Boating","Big time hiking",new Date(),new double[] {43.6916570, -79.4554498});
        insertEvent("Biking","Big time hiking",new Date(),new double[] {43.7123746, -79.4546753});
        insertEvent("Football","Big time hiking",new Date(),new double[] {43.7106239, -79.4285506});
        insertEvent("Baseball","Big time hiking",new Date(),new double[] {43.7117130, -79.4217445});
        insertEvent("Tennis","Big time hiking",new Date(),new double[] {43.7356264, -79.3998228});
        insertEvent("Fishing","Big time hiking",new Date(),new double[] {43.7337591, -79.4042867});
    }*/

    private void addVenue(  final String pName,
                            final double [] pLocation)
    {
        final BasicDBObject loc = new BasicDBObject("name", pName);
        loc.put("loc", pLocation);
        eventCollection.update(new BasicDBObject("name", pName), loc, true, false);
    }


    public boolean insertUser(String userId,String userName, String locationDetails,String preference,double lat, double lng) {

       Log.d("MongoDB","Trying to Insert User");
        DBCollection userCollection = db.getCollection("users");
        Log.d("MongoDB","Trying to Insert User 1");

        BasicDBObject newUser = new BasicDBObject();
        newUser.put("userId", userId);
        newUser.put("userName", userName);
        newUser.put("locationDetails", locationDetails);
        newUser.put("preference", preference);
        newUser.put("lattitude",lat);
        newUser.put("longitude",lng);
        userCollection.insert(newUser);
        return true;
    }


    public String storeImage(Bitmap inBitmap, String fileName) {

        //Load our image
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        inBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        byte[] imageBytes = bos.toByteArray();
        //Save image into database
        GridFSInputFile in = fs.createFile( imageBytes );
        in.setFilename(fileName);
        in.save();
        Log.i("MongoDb", "Stored Image and generated Id " + in.getId());
        return in.getId().toString();
    }

    public Bitmap getImage(String forID) {
        try {
            //Find saved image
            GridFSDBFile out = fs.findOne(forID);
            Log.i("MongoDb", "Retrieved Image and with Id " + out.getId());
            Bitmap imageBitMap = BitmapFactory.decodeStream(out.getInputStream());
            return imageBitMap;
        } catch (Exception e) {

        }
        return null;
    }


}
