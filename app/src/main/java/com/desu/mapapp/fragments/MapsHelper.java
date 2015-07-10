package com.desu.mapapp.fragments;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desu.mapapp.DataBaseHelper;
import com.desu.mapapp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MapsHelper {

    LatLng BishkekCoordinates = new LatLng(42.876318, 74.588016);
    LatLng BishkekCoordinates1 = new LatLng(42.890028, 74.679523);

    private GoogleMap mMap;
    private Context context;
    public float currentZoom = 15;//зум для уменьшения маркеров

    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();

    public void mapInit(GoogleMap Map,Context cont){
        mMap=Map;
        context=cont;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(context, marker.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition pos) {
               // Toast.makeText(context, Float.toString(pos.zoom), Toast.LENGTH_SHORT).show();

                if (pos.zoom < currentZoom) {
                    //currentZoom = pos.zoom;
                    minimizeMarkers();
                    // do you action here
                }
                else restoreMarkers();
            }
        });
    }
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = LayoutInflater.from(context).inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            ImageView ivIcon = ((ImageView)myContentsView.findViewById(R.id.icon));
            ivIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.marker));


            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public void toPos(LatLng coordinates,float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinates)
                .zoom(zoom)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
    }
    public void toPos(double latitude, double longitude,float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
    }//перегрузка для двух double

    public void setMarker(String locality, LatLng point){
        int marker_icon;
        float zoomLevel = mMap.getCameraPosition().zoom;
        if (zoomLevel < currentZoom) marker_icon = R.drawable.red_dot;
        else marker_icon = R.drawable.marker;

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(locality)
                .icon(BitmapDescriptorFactory.fromResource(marker_icon)));
        mMarkerArray.add(marker);
    }
    public void setMarker(String locality, double lat, double lng){
        int marker_icon;
        float zoomLevel = mMap.getCameraPosition().zoom;
        if (zoomLevel < currentZoom) marker_icon = R.drawable.red_dot;
        else marker_icon = R.drawable.marker;

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(locality)
                .icon(BitmapDescriptorFactory.fromResource(marker_icon)));
        mMarkerArray.add(marker);
    }//перегрузка для двух double

    public void clear(){
        mMap.clear();
        mMarkerArray.clear();
    }

    public void minimizeMarkers(){
        for (Marker marker : mMarkerArray) {
            //marker.setVisible(false);
            //marker.remove(); <-- works too!

            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
        }
    }
    public void restoreMarkers(){
        for (Marker marker : mMarkerArray) {
            //marker.setVisible(false);
            //marker.remove(); <-- works too!

            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        }
    }

    public void getDirection(LatLng from,LatLng to){

String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+ Convert(from) +"&destination="+ Convert(to) +"&sensor=false";
        //System.out.println("----------------------------------------url" +url);
//第一引数：execute()で入れるパラメータ
//第二引数：onProgressUpdate()にいれるパラメータ
//第三引数：onPostExecute()に入れるパラメータ
        new AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost(params[0]);
// Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    System.out.println("-------------------Exception---------------------\n"+e.toString());
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception e){System.out.println("-------------------Exception---------------------\n"+e.toString());}
                }

                //System.out.println(result );
                try{

                    //System.out.println("----------------------------------------11");
                    JSONObject resp = new JSONObject(result);
                    //Log.i("Location", "Contenido del kml: " + resp);
                    JSONArray routeObject = resp.getJSONArray("routes");
                    JSONObject routes = routeObject.getJSONObject(0);
                    JSONObject overviewPolylines = routes
                            .getJSONObject("overview_polyline");
                    result = overviewPolylines.getString("points");
                    //System.out.println(encodedString+ "----------------------------------------22");
                }
                catch (Exception e) {System.out.println("-------------------Exception---------------------\n"+e.toString());}

                return result;
            }
            //doInBackGroundの結果を受け取る
            @Override
            protected void onPostExecute(String result){
                try{
                List<LatLng> decodedPath = PolyUtil.decode(result);
                mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
                }
                catch (Exception e) {System.out.println("-------------------Exception---------------------\n"+e.toString());}
            }
        }.execute(url);
        /*String LINE = "}gudGq_gfMnJZjIh@bH^zENjG\\hFVBs@`@uRToQVkPHiF";
        List<LatLng> decodedPath = PolyUtil.decode(LINE);
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));*/
    }
    public String Convert(LatLng latLng){
        Double l1=latLng.latitude;
        Double l2=latLng.longitude;
        String coordl1 = l1.toString();
        String coordl2 = l2.toString();
        return coordl1 + "," + coordl2;
    }

    public void getPoints(String url){

          // System.out.println("----------------------------------------url\n" + url);
//第一引数：execute()で入れるパラメータ
//第二引数：onProgressUpdate()にいれるパラメータ
//第三引数：onPostExecute()に入れるパラメータ
            new AsyncTask<String,Void,String>(){
                @Override
                protected String doInBackground(String... params) {
                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                    HttpGet httpget = new HttpGet (params[0]);//get get get get get get get get get get get get get get get get get get get get get get get get
// Depends on your web service
                    httpget.setHeader("Content-type", "application/json");

                    InputStream inputStream = null;
                    String result = null;
                    try {
                        HttpResponse response = httpclient.execute(httpget);
                        HttpEntity entity = response.getEntity();

                        inputStream = entity.getContent();
                        // json is UTF-8 by default
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();

                        String line = null;
                        while ((line = reader.readLine()) != null)
                        {
                            sb.append(line + "\n");
                        }
                        result = sb.toString();
                    } catch (Exception e) {
                        System.out.println("-------------------Exception---------------------\n"+e.toString());
                    }
                    finally {
                        try{if(inputStream != null)inputStream.close();}catch(Exception e){System.out.println("-------------------Exception---------------------\n"+e.toString());}
                    }

                    return result;
                }
                //doInBackGroundの結果を受け取る
                @Override
                protected void onPostExecute(String result){

                    //System.out.println(result );
                    try{
                       // System.out.println("----------------------------------------11\n"/*+result*/);
                        JSONObject resp = new JSONObject(result);
                        JSONArray points = resp.getJSONArray("points");

                        DataBaseHelper myDbHelper = new DataBaseHelper(context);//не менять, иначе бд не загружается
                        myDbHelper = new DataBaseHelper(context);
                        myDbHelper.openDataBase();

                        myDbHelper.ClearData();
                        for (int i = 0;i<points.length();i++) {
                            JSONObject point = points.getJSONObject(i);
                            JSONObject coordinate = point.getJSONObject("coordinate");
                            JSONObject vendor = point.getJSONObject("vendor");
                            JSONObject network = vendor.getJSONObject("network");

                            setMarker(point.getString("title"), coordinate.getDouble("lat"), coordinate.getDouble("lng"));

                            DataBaseHelper.Data data = new DataBaseHelper.Data();
                            data._id = Integer.toString(i);
                            data.id = point.getString("id");
                            data.title = point.getString("title");
                            data.lat = coordinate.getString("lat");
                            data.lng = coordinate.getString("lng");
                            data.phone = point.getString("phone");
                            data.address = point.getString("address");
                            data.vendor = vendor.getString("title");
                            data.network = network.getString("title");
                            data.features = "11";
                            myDbHelper.SetData(data);
                        }

                        myDbHelper.close();

                        //System.out.println(point.getString("title"));
                        //System.out.println(coordinate.getString("lat"));
                        //System.out.println(coordinate.getString("lng"));



                       // System.out.println("----------------------------------------22\n");
                    }
                    catch (Exception e) {
                        System.out.println("-------------------Exception---------------------\n"+e.toString());
                    }

                }
            }.execute(url);
        }

}
