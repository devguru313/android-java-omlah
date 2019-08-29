package com.omlah.customer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.omlah.customer.model.CartDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class DatabaseManager
{
    private static Database database;
    public static DatabaseManager databaseManager=null;
    SQLiteDatabase db;
    Context context;
    Cursor c;

    public static DatabaseManager getInstance(Context context)
    {
        if(databaseManager ==null)
        {
            databaseManager = new DatabaseManager(context);
        }
        return  databaseManager;
    }

    public DatabaseManager(Context context)
    {
        super();
        this.context = context;
        database = new Database(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION);
    }


    public void openDatabase()
    {
        db = database.getWritableDatabase();
    }

    public void closeDatabase()
    {
        database.close();
    }

    // insert into table
    public void insert(CartDetails cartItems)
    {

        ContentValues cv = new ContentValues();
        cv.put(Database.TICKET_ID, cartItems.getTicketID());               //1
        cv.put(Database.TICKET_TITLE, cartItems.getTicketTitle());         //2
        cv.put(Database.TICKET_PRICE, cartItems.getTicketPrice());         //3
        cv.put(Database.TICKET_DES, cartItems.getTicketDes());             //4
        cv.put(Database.TICKET_QUANTITY, cartItems.getTicketQuantity());   //5
        cv.put(Database.TOTAL_PRICE, cartItems.getTotalPrice());           //6
        cv.put(Database.TICKET_TYPE, cartItems.getTicketType());           //7

        Log.e("getTicketID",cartItems.getTicketID());
        Log.e("getTicketTitle",cartItems.getTicketTitle());
        Log.e("getTicketPrice",cartItems.getTicketPrice());
        Log.e("getTicketDes", cartItems.getTicketDes());
        Log.e("getTicketQuantity", cartItems.getTicketQuantity());
        Log.e("getTotalPrice", cartItems.getTotalPrice());

        //Menu Quantity Update method
        String rowID = null;
        String totalQuantity=null;
        String oldQuantity=null;


        Log.e("getCount",""+getCount());

        if (getCount() > 0)
        {
            //Check Same menu and same addons in table
            Cursor c = db.rawQuery("SELECT * FROM " + Database.DATABASE_TABLE + " WHERE " +
                    Database.TICKET_ID + "=? AND " + Database.TICKET_TITLE + "=?", new String[]{cartItems.getTicketID(),cartItems.getTicketTitle()});

            if (c.moveToFirst())
            {
                rowID = c.getString(0);
                oldQuantity = c.getString(5);

                int Totalquantity;

                if(cartItems.getTicketAddRemove().equalsIgnoreCase("ADD")){

                    Totalquantity = Integer.valueOf(oldQuantity) + Integer.valueOf(cartItems.getTicketQuantity());

                }else{

                    Totalquantity = Integer.valueOf(oldQuantity) - Integer.valueOf(cartItems.getTicketQuantity());
                }

                totalQuantity = String.valueOf(Totalquantity);

                //call update quantity method for update new quantity
                updateQuantity(rowID, totalQuantity);
            }
            else
            {
                try
                {
                    db.insert(Database.DATABASE_TABLE, null, cv);
                    Log.e("cv","cv"+cv);
                    Log.e("Table ", "Values inserted");
                    db.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    Log.e("Insert problem", "");
                }
            }

        }
        else
        {
            try
            {
                db.insert(Database.DATABASE_TABLE, null, cv);
                Log.e("cv","cv"+cv);
                db.close();
                Log.e("Inserted ", "Values inserted");
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                Log.e("Insert problem", "");
            }
        }
    }

    public Cursor getAll()
    {
        Cursor cursor = null;
        String query = "select * from " + Database.DATABASE_TABLE;
        db = database.getReadableDatabase();
        cursor = db.rawQuery(query, null);
        return cursor;
    }

    //get count
    public int getCount()
    {
        String query = "select * from " + Database.DATABASE_TABLE;
        db = database.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        return c.getCount();
    }


    //get SubTotal
    public String getSubTotal()
    {
        double SUB_TOTAL=0;

        Cursor cursor = null;

        String query = "select * from " + Database.DATABASE_TABLE;

        openDatabase();

        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do {

                double sub_TOTAL =Double.parseDouble(cursor.getString(6));

                SUB_TOTAL += sub_TOTAL;

            } while (cursor.moveToNext());
        }

        cursor.close();

        closeDatabase();

        return String.format(Locale.ENGLISH,"%.2f", SUB_TOTAL);
    }


    //Clear table
    public void clearTable()
    {
        db = database.getWritableDatabase();
        db.delete(Database.DATABASE_TABLE, null, null);
    }

    //update quantity
    public void updateQuantity(String id, String quantity)
    {

        Log.e("UpdateID"," UpdateId "+id+" Quantity "+quantity);

        db = database.getWritableDatabase();

        int match = Integer.parseInt(quantity);

        if (match == 0)
        {
            deleteMenu(id);
        }
        else if (match > 0)
        {
            ContentValues cv = new ContentValues();
            cv.put(Database.TICKET_QUANTITY, quantity);
            db.update(Database.DATABASE_TABLE, cv, Database.PRIMARY_KEY + "=" + id, null);

            //call update method to update total price
            updatePrice(id);
        }

    }

    //Delete Menu Items
    public void deleteMenu(String id)
    {
        Log.d("Items", "Item Deleted" + id);

        db = database.getWritableDatabase();
        db.delete(Database.DATABASE_TABLE, Database.PRIMARY_KEY + "=" + id, null);
        db.close();

    }

    //get quantity and menu price and addon price to update
    public void updatePrice(String id)
    {
        String menuPrice=null;
        String addonPrice=null;
        String quantity=null;

        Log.e("updatePrice"," updatePrice "+id);

        SQLiteDatabase DB = database.getReadableDatabase();

        String query = "select * from " + Database.DATABASE_TABLE + " where id= " + "'" + id + "'";

        Cursor cursor = DB.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
           menuPrice = cursor.getString(3);
           addonPrice = "0";
           quantity = cursor.getString(5);

        }

        /*String  split[] = menuPrice.split(" ");*/

        double menu_Price = Double.parseDouble(menuPrice);
        double addon_Price = Double.parseDouble(addonPrice);
        double qty = Integer.parseInt(quantity);

        //Add Total price
        double total = (menu_Price + addon_Price) * qty;

        db = database.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Database.TOTAL_PRICE, String.valueOf(total));
        db.update(Database.DATABASE_TABLE, cv, Database.PRIMARY_KEY + "=" + id, null);
        db.close();
    }


    public JSONObject getCart()
    {

        JSONObject parent = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        Cursor cursor = null;

        String query = "select * from " + Database.DATABASE_TABLE;

        db = database.getReadableDatabase();

        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            do
            {
                try
                {
                    JSONObject jsonObject = new JSONObject();

                    if(cursor.getString(7).equalsIgnoreCase("PRICE")){

                        jsonObject.put("event_details_id", cursor.getString(1));
                        jsonObject.put("ticket_title", cursor.getString(2));
                        jsonObject.put("ticket_quantity", cursor.getString(5));
                        jsonObject.put("ticket_price", cursor.getString(3));
                        jsonObject.put("total_price", cursor.getString(6));
                        jsonObject.put("ticket_coin","0");
                        jsonObject.put("total_coin","0");
                        jsonArray.put(jsonObject);
                        parent.put("booking_cart",jsonArray);

                    }else{

                        jsonObject.put("event_details_id", cursor.getString(1));
                        jsonObject.put("ticket_title", cursor.getString(2));
                        jsonObject.put("ticket_quantity", cursor.getString(5));
                        jsonObject.put("ticket_price", "0");
                        jsonObject.put("total_price", "0");
                        jsonObject.put("ticket_coin", cursor.getString(3));
                        jsonObject.put("total_coin", cursor.getString(6).replace(".0",""));
                        jsonArray.put(jsonObject);
                        parent.put("booking_cart",jsonArray);

                    }


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        Log.w("Cart details", "Cart details : " + parent);

        return parent;

    }

    public int getCountRest(String string) {


        Log.e("String","String"+string);

        String query = "select * from " + Database.DATABASE_TABLE+ " where menuName= " + "'" + string + "'";
        db = database.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        return c.getCount();
    }
}
