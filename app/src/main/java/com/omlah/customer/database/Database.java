package com.omlah.customer.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME   = "poppay";
    public static final String DATABASE_TABLE  = "ticket";
    public static final String PRIMARY_KEY     = "id";
    public static final String TICKET_ID       = "ticketId";      //1
    public static final String TICKET_TITLE    = "ticketTitle";   //2
    public static final String TICKET_PRICE    = "ticketPrice";   //3
    public static final String TICKET_DES      = "ticketDes";     //4
    public static final String TICKET_QUANTITY = "ticketQuantity";//5
    public static final String TOTAL_PRICE     = "totalPrice";    //6
    public static final String TICKET_TYPE     = "ticketType";    //7
    public static final int DATABASE_VERSION   = 1;

    String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE +
            "(" + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TICKET_ID + " TEXT NOT NULL,"
            + TICKET_TITLE + " TEXT NOT NULL,"
            + TICKET_PRICE + " TEXT NOT NULL,"
            + TICKET_DES + " TEXT NOT NULL,"
            + TICKET_QUANTITY + " TEXT NOT NULL,"
            + TOTAL_PRICE + " TEXT NOT NULL,"
            + TICKET_TYPE + " TEXT NOT NULL" + ")";


    public Database(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        Log.d("DB Created ", "Database created success ");
    }

    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            db.execSQL(CREATE_TABLE);
            Log.e("Table Created", "Table created successfully");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Log.e("Table Error", "" + e.getMessage());
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
    }

}
