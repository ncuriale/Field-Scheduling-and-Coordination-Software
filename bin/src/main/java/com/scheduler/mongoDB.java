package com.scheduler;

import java.net.UnknownHostException;
import java.util.List;
import java.util.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.client.model.DBCollectionUpdateOptions;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;


public class mongoDB {

    MongoClient mongoClient;
    DB db;
    DBCollection collection;

    //Sets the connection
    public void setConnection(String database, String collection){
         
        try {   

            // Creating a Mongo client 
            this.mongoClient = new MongoClient( "localhost" , 27017 ); 
            this.db = mongoClient.getDB(database);
            this.collection = db.getCollection(collection);

        } catch (MongoException e) {
            e.printStackTrace();

        }

    }

    public void addGame(DBCollection collection, String team, String field,
                            String date, String time){

        //Create team document
        BasicDBObject teamDoc = new BasicDBObject();
        teamDoc.put("Team", team);
        teamDoc.put("Field", field);
        teamDoc.put("Date", date);
        teamDoc.put("Time", time);

        //Create update document for upsert
        BasicDBObject updateDoc = new BasicDBObject();
        updateDoc.put("$set", teamDoc );

        //Update options -- upsert=true
        DBCollectionUpdateOptions options = new DBCollectionUpdateOptions().upsert(true);

        //Insert doc into collection but perform upsert to ensure doc doesn't exist
        collection.update(teamDoc,updateDoc,options);
        //collection.insert(teamDoc);
    }

    public void updateGame(DBCollection collection, String team, String field,
                            String date, String time, String update_team, String update_field,
                            String update_date, String update_time){

        //Create team document
        BasicDBObject teamDoc = new BasicDBObject();
        teamDoc.put("Team", team);
        teamDoc.put("Field", field);
        teamDoc.put("Date", date);
        teamDoc.put("Time", time);

        //Create update document for upsert
        BasicDBObject updateDoc = new BasicDBObject();
        BasicDBObject updateVals = new BasicDBObject();
        updateVals.put("Team", update_team);
        updateVals.put("Field", update_field);
        updateVals.put("Date", update_date);
        updateVals.put("Time", update_time);
        updateDoc.put("$set", updateVals );

        //Insert doc into collection but perform upsert to ensure doc doesn't exist
        collection.update(teamDoc,updateDoc);
        //addGame(collection, team, field, date, time);
        //removeGame(collection, update_team, update_field, update_date, update_time);

    }

    public void removeGame(DBCollection collection, String team, String field,
                            String date, String time){

        //Create team document
        BasicDBObject teamDoc = new BasicDBObject();
        teamDoc.put("Team", team);
        teamDoc.put("Field", field);
        teamDoc.put("Date", date);
        teamDoc.put("Time", time);

        //Remove doc from collection
        collection.remove(teamDoc);
    }


    public void removeTeam(DBCollection collection, String team){
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("Team", team);
        collection.remove(searchQuery);
    }

    public List searchDate(DBCollection collection, String date){
        //Set date to query
        BasicDBObject query = new BasicDBObject();
        query.put("Date", date);
        //Perform query to get how many results for this date
        DBCursor cur = collection.find(query);
        //Count number of results
        int ires = 0;
        while (cur.hasNext()) {
            ires++;
            cur.next();
        }

        //Perform query with field constraints
        BasicDBObject field;
        int ind;
        List result = new ArrayList();
        String[] fields = { "Team", "Field", "Time" };
        //Go thru all results
        for (int i=0; i<ires; i++){
            //Filter for 3 different fields with above date
            for (int j=0; j<3; j++){

                //Add field to retrieve
                field = new BasicDBObject();
                field.put( fields[j], 1);
                field.put( "_id", 0);

                //Perform query
                cur = collection.find(query,field);

                //Go thru results and record when indexes are equal
                //This will order the results properly
                ind = 0;
                while (cur.hasNext()) {
                    BasicDBObject obj = (BasicDBObject) cur.next();
                    if(i==ind){
                        result.add(obj.getString(fields[j]));
                    }
                    ind++;
                }
            }
        }

        return result;
    }

    public String[] queryFieldAll(DBCollection collection, String str){        
        List<String> distinctVals = collection.distinct(str);
        String[] distinctStrings = new String[distinctVals.size()]; 
        // ArrayList to Array Conversion
        for (int i =0; i < distinctVals.size(); i++)
            distinctStrings[i] = distinctVals.get(i);  

        return distinctStrings;
    }

}


    

