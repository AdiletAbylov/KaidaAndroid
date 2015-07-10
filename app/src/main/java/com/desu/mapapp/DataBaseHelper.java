package com.desu.mapapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper {

    // путь к базе данных вашего приложения
    private static String DB_PATH = "/data/data/com.desu.mapapp/databases/";
    private static String DB_NAME = "points.sqlite";
    private SQLiteDatabase myDataBase;
    private final Context mContext;
    private static final String DB_TABLE = "DB_Points";






    /**
     * Конструктор
     * Принимает и сохраняет ссылку на переданный контекст для доступа к ресурсам приложения
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     * */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if(dbExist){
            //ничего не делать - база уже есть
        }else{
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //база еще не существует
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     * */
    private void copyDataBase() throws IOException{
        //Открываем локальную БД как входящий поток
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        //Путь ко вновь созданной БД
        String outFileName = DB_PATH + DB_NAME;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //открываем БД
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Здесь можно добавить вспомогательные методы для доступа и получения данных из БД
    // вы можете возвращать курсоры через "return myDataBase.query(....)", это облегчит их использование
    // в создании адаптеров для ваших view

    // Получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return myDataBase.query(DB_TABLE, null, null, null, null, null, null);
    }

    public Data getData(int id)
    {
        Data result = new Data();
        //String sqlQuery = "SELECT * FROM city AS t1, region AS t2 WHERE t1.region_number = t2._id;";
        String where = "_id = "+id;// country_id-ая строка
        Cursor c = myDataBase.query(DB_TABLE, null, where, null, null, null, null);//выдрать с бд
        c.moveToFirst();
        //System.out.println("ColumnName - " + c.getColumnName(1));
        result.id = c.getString(c.getColumnIndex("id"));
        result.title = c.getString(c.getColumnIndex("title"));//создать возвращаемая структура
        result.lat = c.getString(c.getColumnIndex("lat"));
        result.lng = c.getString(c.getColumnIndex("lng"));
        result.address = c.getString(c.getColumnIndex("address"));
        result.phone = c.getString(c.getColumnIndex("phone"));
        result.vendor = c.getString(c.getColumnIndex("vendor"));
        result.network = c.getString(c.getColumnIndex("network"));
        result.features = c.getString(c.getColumnIndex("features"));
        c.close();
        return (result);
    }
    public static class Data //возвращаемая структура
    {
        public String _id;
        public String id;
        public String title;
        public String lat;
        public String lng;
        public String address;
        public String phone;
        public String vendor;
        public String network;
        public String features;
    }

    public void SetData(Data InData){
        ContentValues cv = new ContentValues();
        cv.put("_id", InData._id);
        cv.put("id", InData.id);
        cv.put("title", InData.title);
        cv.put("lat", InData.lat);
        cv.put("lng", InData.lng);
        cv.put("address", InData.address);
        cv.put("phone", InData.phone);
        cv.put("vendor", InData.vendor);
        cv.put("network", InData.network);
        cv.put("features", InData.features);
        myDataBase = this.getWritableDatabase();
        myDataBase.insert(DB_TABLE, null, cv);
    }

    public void ClearData(){
        myDataBase = this.getWritableDatabase();
        myDataBase.delete(DB_TABLE, null, null);
    }
}
