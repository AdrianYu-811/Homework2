package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

//Adrian Yu

public class MainActivity extends AppCompatActivity {
    LoaderCallbackInterface loaderCallbackInterface = new LoaderCallbackInterface() {
        @Override
        public void onManagerConnected(int status) {

        }

        @Override
        public void onPackageInstall(int operation, InstallCallbackInterface callback) {

        }
    };
    TextView textView;
    ImageView imageView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!OpenCVLoader.initDebug()){
            boolean success = OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this,loaderCallbackInterface);
        }
        else{
            loaderCallbackInterface.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        try {
            Mat img = Utils.loadResource(MainActivity.this, R.drawable.qrcode, CvType.CV_8UC4);
            Bitmap bitmap=Bitmap.createBitmap(img.width(),img.height(), Bitmap.Config.ARGB_8888);
            ImageView imageView = findViewById(R.id.imageView);
            Utils.matToBitmap(img,bitmap);
            imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            button.setOnClickListener(new View.OnClickListener() {
                Mat img = Utils.loadResource(MainActivity.this, R.drawable.qrcode, CvType.CV_8UC4);

                @Override
                public void onClick(View v) {
                    try {
                        Bitmap bitmap=Bitmap.createBitmap(img.width(),img.height(), Bitmap.Config.ARGB_8888);
                        ImageView imageView = findViewById(R.id.imageView);
                        Utils.matToBitmap(img,bitmap);
                        imageView.setImageBitmap(bitmap);
                        String line = decodeQRCode(bitmap);
                        String [] arrlines = line.split(";",-1);
                        Point firstpoint = new Point();
                        Point secondpoint = new Point();
                        Scalar linecolor = new Scalar(255,0,0,255);
                        int linnewidth = 3;
                        int count =0;
                        for (String a: arrlines){
                            Log.d("adrian","line"+a);
                            String [] arrpoints = a.split(" ",-1);
                            for (String b: arrpoints){
                                Log.d("adrian","point"+b);
                                count++;
                                String [] xy = b.split(",",-1);
                                if (count==1){
                                    firstpoint.x = Integer.valueOf(xy[0]);
                                    firstpoint.y = Integer.valueOf(xy[1]);
                                }
                                else if (count==2){
                                    secondpoint.x =Integer.valueOf(xy[0]);
                                    secondpoint.y = Integer.valueOf(xy[1]);
                                    count=0;
                                }
                            }
                            Imgproc.line(img,firstpoint,secondpoint,linecolor,linnewidth);
                        }
                        Utils.matToBitmap(img,bitmap);
                        imageView.setImageBitmap(bitmap);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String decodeQRCode(Bitmap bitmap){
        Mat mat= new Mat();
        Utils.bitmapToMat(bitmap, mat);
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        String result = qrCodeDetector.detectAndDecode(mat);
        Log.d("zencher",""+result);
        textView.setText(result);
        return result;
    }

}
