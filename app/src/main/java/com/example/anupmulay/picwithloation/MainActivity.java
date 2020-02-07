package com.example.anupmulay.picwithloation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    ImageView ivPhoto;
    Button btnTakePhoto,btnSharePhoto,btnSavePhoto;
    TextView tvAddress;

    Bitmap photo;

    GoogleApiClient mLocationClient;
    Location mLastLocation;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.h1));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivPhoto= (ImageView) findViewById(R.id.ivphoto);
        btnTakePhoto= (Button) findViewById(R.id.btnTakePhoto);
        btnSharePhoto= (Button) findViewById(R.id.btnSharePhoto);
        btnSavePhoto= (Button) findViewById(R.id.btnSavePhoto);
        tvAddress= (TextView) findViewById(R.id.tvAddress);

        animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.custom);
        ivPhoto.startAnimation(animation);

        GoogleApiClient.Builder builder=new GoogleApiClient.Builder(this);
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        mLocationClient=builder.build();

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent CameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(CameraIntent,1234);
            }
        });

        btnSharePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    File file=new File(getExternalCacheDir(),"my_image.jpg");
                    FileOutputStream fOut=new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                    Intent sharingIntent=new Intent (android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String sharebody=tvAddress.getText().toString();
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"My Address");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT,sharebody);
                    sharingIntent.setType("image/png");
                    startActivity(sharingIntent);
                }
                catch(Exception e)
                {
                    Toast.makeText(MainActivity.this,"Exception: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String state= Environment.getExternalStorageState();

                if(Environment.MEDIA_MOUNTED.equalsIgnoreCase(state))
                {
                    File root=Environment.getExternalStorageDirectory();
                    File Dir=new File(root+"/Imagesave");


                    if(!Dir.exists())
                    {
                        Dir.mkdir();
                    }
                    Toast.makeText(getApplicationContext(), "Dir created", Toast.LENGTH_LONG).show();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    Date date=new Date();
                    String fname=sdf.format(date)+".png";

                    File file=new File(Dir,fname);

                    try
                    {
                        FileOutputStream fos=new FileOutputStream(file);
                        BufferedOutputStream bos=new BufferedOutputStream(fos);
                        photo.compress(Bitmap.CompressFormat.PNG,90,bos);
                        Toast.makeText(getApplicationContext(),"file Saved",Toast.LENGTH_LONG).show();
                        bos.flush();
                        bos.close();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"File Could not be saved",Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"External Storage Issue",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            if(requestCode==1234)
            {
                photo= (Bitmap) data.getExtras().get("data");
                ivPhoto.setImageBitmap(photo);
                btnSharePhoto.setEnabled(true);
                btnSavePhoto.setEnabled(true);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mLocationClient!=null)
        {
            mLocationClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        if(mLastLocation!=null)
        {
            double latitude=mLastLocation.getLatitude();
            double longitude=mLastLocation.getLongitude();

            Geocoder geocoder=new Geocoder(this, Locale.ENGLISH);

            try
            {
                List<Address>addresses=geocoder.getFromLocation(latitude,longitude,1);

                if(addresses!=null)
                {
                    Address fetchedAddress=addresses.get(0);

                    tvAddress.setText("At: "+fetchedAddress.getFeatureName()
                    +","+fetchedAddress.getSubLocality()
                    +","+fetchedAddress.getLocality()
                    +"-"+fetchedAddress.getPostalCode()
                    +","+fetchedAddress.getAdminArea()
                    +","+fetchedAddress.getCountryName());
                }
                else
                {
                    tvAddress.setText("No Location Found!......");
                }
            }
            catch(IOException e)
            {
                Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(getApplicationContext(),"Connection Suspended",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(getApplicationContext(),"Connection Failed",Toast.LENGTH_LONG).show();
    }
}
