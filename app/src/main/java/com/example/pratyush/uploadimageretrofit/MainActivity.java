package com.example.pratyush.uploadimageretrofit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    ImageView imageView;
    Button buttonUpload,buttonChooseImage;
    Bitmap image;//can't be local since on click listener is set but keep trying it out
    ProgressBar progressBar;
    private static final int IMAGE_REQUEST = 1;
    int check;//0 when image is not loaded , 1 when image is loaded
    long lengthbmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.i("check",String.valueOf(check));
        imageView=findViewById(R.id.imageview);
        buttonChooseImage=findViewById(R.id.chooseimage);
        buttonUpload=findViewById(R.id.uploadimage);
        editText=findViewById(R.id.name);
        progressBar=findViewById(R.id.progressbar);

        buttonChooseImage.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.chooseimage:
                check=0;
                selectImage();
                break;

            case R.id.uploadimage:
                imageUpload();
                break;


        }
    }

    private void selectImage()
    {
        Toast.makeText(MainActivity.this,"50KB < Image Size < 1MB",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data.getData()!=null)
        {
            Uri path = data.getData();

            try {

                image = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                //check = 1;
                Log.i("check",String.valueOf(check));
                //imageView.setImageBitmap(image);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                lengthbmp = imageInByte.length;
                Log.i("size KB",String.valueOf(lengthbmp/1024));
                if(lengthbmp/1024<=50)
                {
                    Toast.makeText(MainActivity.this,"image size should be greater than 50KB",Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(null);
                    return;
                }
                double imageSize = (double)lengthbmp/(1024*1024);//imageSize is in MB

                double quality =(100*0.9)/imageSize;
                Log.i("size MB normal:",String.valueOf(imageSize));
                if(imageSize<=1)
                {
                    //imageView.setImageURI(path);


                    imageView.setImageBitmap(image);
                    check=1;
                }

                else
                {
                    compressImage((int)quality);
                    if(imageIsGreaterThanLimit())
                        toastImageGreaterThanOneMB();
                    else
                    {
                        imageView.setImageBitmap(image);
                        check=1;
                    }
                }
                /*else if(imageSize <=2)
                {
                   // 50 percent compression

                    compressImage(50);

                    if(imageIsGreaterThanLimit())
                        toastImageGreaterThanOneMB();
                    else
                    {
                        imageView.setImageBitmap(image);
                        check=1;
                    }

                }
                else if(imageSize <=3)
                {
                    // 75 percent compression
                    compressImage(25);


                    if(imageIsGreaterThanLimit())
                        toastImageGreaterThanOneMB();
                    else
                    {
                        imageView.setImageBitmap(image);
                        check=1;
                    }
                }
                else
                {
                    compressImage(15);

                    if(imageIsGreaterThanLimit())
                        toastImageGreaterThanOneMB();
                    else
                    {
                        imageView.setImageBitmap(image);
                        check=1;
                    }

                }*/

                Log.i("length",String.valueOf(lengthbmp));


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imageToString()
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[]imageByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByte,Base64.DEFAULT);
    }

    private void imageUpload()
    {

        Log.i("check",String.valueOf(check));
        if(check == 0)
        {
            Toast.makeText(MainActivity.this,"please select an image first",Toast.LENGTH_SHORT).show();
        }

        else {


            progressBar.setVisibility(View.VISIBLE);

            String Image = imageToString();
            //String Title = "imageName123";
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<ImageClass> call = apiInterface.uploadImage(Image);

            call.enqueue(new Callback<ImageClass>() {
                @Override
                public void onResponse(Call<ImageClass> call, Response<ImageClass> response) {

                    Log.i("response",response.message());
                    Log.i("response code",String.valueOf(response.code()));
                    ImageClass imageClass = response.body();
                    //Log.i("response",imageClass.getResponse());



                    if(response.isSuccessful())
                    {
                        progressBar.setVisibility(View.GONE);
                        //Toast.makeText(MainActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, imageClass.getResponse(), Toast.LENGTH_SHORT).show();
                        Log.i("response full:",response.toString());
                        //buttonUpload.setEnabled(false);
                        //Toast.makeText(MainActivity.this,"Upload Successful",Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageClass> call, Throwable t) {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "could Not Connect to server", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private boolean imageIsGreaterThanLimit()
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        lengthbmp = imageInByte.length;
        double imageSize = (double)lengthbmp/(1024*1024);
        Log.i("size MB comp:",String.valueOf(imageSize));
        if(imageSize>=1)
            return true;
        else
            return false;
    }

    private void toastImageGreaterThanOneMB()
    {
        Toast.makeText(MainActivity.this,"image is too large to upload",Toast.LENGTH_SHORT).show();
        imageView.setImageBitmap(null);
    }

    private void compressImage(int quality)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,quality,byteArrayOutputStream);
        byte[]imageByte = byteArrayOutputStream.toByteArray();
        image = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
    }

}
