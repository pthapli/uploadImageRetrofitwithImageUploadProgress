package com.example.pratyush.uploadimageretrofit;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Process.myUid;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextTime,editTextVenue,editTextDescription,editTextDate,editTextTitle;
    ImageView imageView,imageViewTime,imageViewDate;
    ImageButton buttonBackToolbar;
    Button buttonUpload,buttonDelete;
    Bitmap image;//can't be local since on click listener is set but keep trying it out
    ProgressBar progressBar;
    TextView textView;
    private static final int IMAGE_REQUEST = 1;
    int check;//0 when image is not loaded , 1 when image is loaded
    long lengthbmp;
    Toolbar toolbar;
    String mimeType,imageName;
    private boolean checkThread=true;
    int uid=myUid();
    static long dataSent=0;
    static long initialDataUsed=0;

    int userYear,userMonth,userDay,userHour,userMinute;


    NotificationManagerCompat mNotificationManager;
    NotificationCompat.Builder mNotifyBuilder;
    NotificationHelper helper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        int uid=myUid();
        Log.i("uid",String.valueOf(uid));
        helper=new NotificationHelper(this);

        Log.i("check",String.valueOf(check));
        //imageView=findViewById(R.id.imageview);
        buttonBackToolbar=findViewById(R.id.buttonbacktoolbar);
        //buttonChooseImage=findViewById(R.id.chooseimage);
        buttonUpload=findViewById(R.id.uploadimage);
        progressBar=findViewById(R.id.progressbar);
        textView=findViewById(R.id.textupload);
        buttonDelete=findViewById(R.id.buttondelete);
        toolbar=findViewById(R.id.app_bar);
        imageViewDate=findViewById(R.id.imageviewdate);
        imageViewTime=findViewById(R.id.imageViewtime);

        editTextDate=findViewById(R.id.datenotice);
        editTextDescription=findViewById(R.id.descriptionnotice);
        editTextTime=findViewById(R.id.timenotice);
        editTextTitle=findViewById(R.id.titlenotice);
        editTextVenue=findViewById(R.id.venuenotice);


        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setTitle("create notice");
        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //Log.i("title",getSupportActionBar().getTitle().toString());
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        //toolbar.setTitleTextColor(Color.WHITE);

       /* toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });*/



        //buttonChooseImage.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        textView.setOnClickListener(this);
        imageViewTime.setOnClickListener(this);
        imageViewDate.setOnClickListener(this);
        buttonBackToolbar.setOnClickListener(this);
        editTextDate.setOnClickListener(this);
        editTextTime.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            /*case R.id.chooseimage:
                check=0;
                selectImage();
                break;*/

            case R.id.uploadimage:
                imageUpload();
                break;

            case R.id.textupload:
                selectImage();
                break;

            case R.id.imageviewdate:
                pickDate();
                break;

            case R.id.imageViewtime:
                pickTime();
                break;

            case R.id.datenotice:
                pickDate();
                break;

            case R.id.timenotice:
                pickTime();
                break;

            case R.id.buttonbacktoolbar:
                //do what back button on toolbar does here...




                break;


        }
    }

    private void selectImage()
    {
        //for one image to be selected
        Toast.makeText(MainActivity.this,"50KB < Image Size < 1MB",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,IMAGE_REQUEST);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data.getData()!=null)
        {
            Uri path = data.getData();
            Log.i("path",path.toString());

            String ext = path.getLastPathSegment();
            Log.i("ext",ext);

            mimeType = getContentResolver().getType(path);
            Log.i("mime",mimeType);

            //String s = path.toString();
            //Log.i("extension",s.substring(s.lastIndexOf(".")));

            File f = new File(path.toString());
            //Log.i("name",f.getAbsolutePath());




            try {

                if(mimeType.equals("image/gif"))
                {
                    Toast.makeText(MainActivity.this,"gifs cant be selected",Toast.LENGTH_SHORT).show();
                    //imageView.setImageResource(R.drawable.ic_launcher_foreground);
                    //buttonChooseImage.setText("Attachments");
                    textView.setText("Attachments");
                    return;
                }

                image = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                //check = 1;
                //Log.i("check",String.valueOf(check));


                //String imagepath = data.getData().getPath();
                //String extension = imagepath.substring(imagepath.lastIndexOf("."));
                //Log.i("extension",extension);
                //Log.i("path",imagepath.substring(imagepath.lastIndexOf(".")));






                //imageView.setImageBitmap(image);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                lengthbmp = imageInByte.length;
                Log.i("size bytes",String.valueOf(lengthbmp));
                Log.i("size KB",String.valueOf(lengthbmp/1024));
                if(lengthbmp/1024<=50)
                {
                    Toast.makeText(MainActivity.this,"image size should be greater than 50KB",Toast.LENGTH_SHORT).show();
                    //imageView.setImageResource(R.drawable.ic_launcher_foreground);
                    //buttonChooseImage.setText("Attachments");
                    textView.setText("Attachments");
                    //Glide.with(MainActivity.this).load(path).into(imageView);
                    return;
                }
                double imageSize = (double)lengthbmp/(1024*1024);//imageSize is in MB

                double quality =(100*0.9)/imageSize;
                Log.i("size MB normal:",String.valueOf(imageSize));
                if(imageSize<=1)
                {

                    //imageView.setImageBitmap(image);
                    //Glide.with(MainActivity.this).load(path).into(imageView);
                    Toast.makeText(MainActivity.this,"image ready to upload",Toast.LENGTH_SHORT).show();
                    check=1;

                    //buttonChooseImage.setText(f.getName());
                    textView.setText(f.getName());
                    buttonDelete.setVisibility(View.VISIBLE);
                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            textView.setText("Attachments");
                            //imageView.setImageBitmap(null);
                            buttonDelete.setVisibility(View.GONE);
                            check=0;
                        }
                    });
                }

                else
                {
                    compressImage((int)quality);

                    if(imageIsGreaterThanLimit())
                        toastImageGreaterThanOneMB();

                    else
                    {
                        //imageView.setImageBitmap(image);
                        //Glide.with(MainActivity.this).load(path).into(imageView);
                        Toast.makeText(MainActivity.this,"image ready to upload",Toast.LENGTH_SHORT).show();
                        //buttonChooseImage.setText(f.getName());
                        textView.setText(imageName=f.getName());
                        buttonDelete.setVisibility(View.VISIBLE);
                        buttonDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                textView.setText("Attachments");
                                //imageView.setImageBitmap(null);
                                buttonDelete.setVisibility(View.GONE);
                                check=0;
                            }
                        });
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

        checkThread=true;
        boolean checkBooleanValue = checkEditTextFields();

        if(!checkBooleanValue)
            return;

        Log.i("check",String.valueOf(check));
        if(check == 0)
        {
            Toast.makeText(MainActivity.this,"please select an image first",Toast.LENGTH_SHORT).show();
        }

        else {


            checkThread=true;
            progressBar.setVisibility(View.VISIBLE);

            String Image = imageToString();
            //String Title = "imageName123";
            String Extension = "."+mimeType.substring(mimeType.lastIndexOf("/")+1);

            String Venue = editTextVenue.getText().toString();
            String Date = editTextDate.getText().toString();
            String Description = editTextDescription.getText().toString();
            String Title = editTextTitle.getText().toString();
            String Time = editTextTime.getText().toString();

            /*ImageClass imageClass=new ImageClass(Extension,Time,Date,Venue,Description,Title);*/
            //sendNetworkRequest(imageClass);


            Log.i("extension",Extension);
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<ImageClass> call = apiInterface.uploadImage(Image,Extension,Title,Date,Venue,Time,Description);


            initialDataUsed=TrafficStats.getUidTxBytes(myUid());


            Thread myThread = new Thread(new MyRunnable(),"thread");
            myThread.start();

            call.enqueue(new Callback<ImageClass>() {
                @Override
                public void onResponse(Call<ImageClass> call, Response<ImageClass> response) {

                    checkThread=false;
                    Log.i("response",response.message());
                    Log.i("response code",String.valueOf(response.code()));
                    ImageClass imageClass = response.body();
                    //Log.i("response",imageClass.getResponse());



                    if(response.isSuccessful())
                    {
                        progressBar.setVisibility(View.GONE);
                        //Toast.makeText(MainActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, imageClass.getResponse(), Toast.LENGTH_SHORT).show();
                        Notification.Builder builder2 = helper.getChannelNotificationUpdate("image upload successful");
                        helper.getManager().notify(123,builder2.build());
                        //Log.i("response full:",response.toString());
                        //buttonUpload.setEnabled(false);
                        //Toast.makeText(MainActivity.this,"Upload Successful",Toast.LENGTH_LONG).show();
                    }


                    else
                    {
                        checkThread=false;
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                        Notification.Builder builder2 = helper.getChannelNotificationUpdate("upload not successful");
                        helper.getManager().notify(123,builder2.build());
                    }
                    
                }

                @Override
                public void onFailure(Call<ImageClass> call, Throwable t) {

                    checkThread=false;
                    progressBar.setVisibility(View.GONE);
                    Notification.Builder builder2 = helper.getChannelNotificationUpdate("upload not successful");
                    helper.getManager().notify(123,builder2.build());

                    //Toast.makeText(MainActivity.this, "could Not Connect to server", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

   /* private void sendNetworkRequest(ImageClass imageClass)
    {

        RequestBody description = RequestBody.create(MultipartBody.FORM,editTextDescription.getText().toString());
        RequestBody date = RequestBody.create(MultipartBody.FORM,editTextDate.getText().toString());
        RequestBody venue = RequestBody.create(MultipartBody.FORM,editTextVenue.getText().toString());
        RequestBody time = RequestBody.create(MultipartBody.FORM,editTextTime.getText().toString());
        RequestBody title = RequestBody.create(MultipartBody.FORM,editTextTitle.getText().toString());














        Retrofit.Builder builder= new Retrofit.Builder()
                .baseUrl("http://10.1.130.68:3030/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=builder.build();
        ApiInterface apiInterface=retrofit.create(ApiInterface.class);
        Call<ImageClass> call = apiInterface.uploadImage(imageClass);

        call.enqueue(new Callback<ImageClass>() {
            @Override
            public void onResponse(Call<ImageClass> call, Response<ImageClass> response) {
                Toast.makeText(MainActivity.this,"successful",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ImageClass> call, Throwable t) {
                Toast.makeText(MainActivity.this,"UNsuccessful",Toast.LENGTH_SHORT).show();
            }
        });


    }*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private boolean checkEditTextFields() {
        String errorMessage = "This field needs to be filled";

        String venue = editTextVenue.getText().toString();
        String date = editTextDate.getText().toString();
        String description = editTextDescription.getText().toString();
        String title = editTextTitle.getText().toString();
        String time = editTextTime.getText().toString();


        //checking which fields are empty

        if (title.isEmpty()) {
            editTextTitle.requestFocus();
            editTextTitle.setError(errorMessage);
            return false;
        }

        if (description.isEmpty()) {
            editTextDescription.requestFocus();
            editTextDescription.setError(errorMessage);
            return false;
        }


        return true;
    }


    private void pickDate()
    {

       final int systemYear,systemMonth,systemDay;
        Calendar calendar=Calendar.getInstance();

        systemYear = calendar.get(Calendar.YEAR);
        systemMonth = calendar.get(Calendar.MONTH);
        systemDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog( MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                userYear=year;
                userMonth=monthOfYear;
                userDay=dayOfMonth;
                editTextDate.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                editTextDate.setGravity(Gravity.CENTER);
            }
        }, systemYear, systemMonth, systemDay);
        datePickerDialog.show();
    }



    private void pickTime()
    {
        Calendar calendar=Calendar.getInstance();
        int systemHour,systemMinute,systemSecond;

        systemHour=calendar.get(Calendar.HOUR_OF_DAY);
        systemMinute=calendar.get(Calendar.MINUTE);
        //systemSecond=calendar.get(Calendar.SECOND);
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                userHour=hourOfDay;
                userMinute=minute;
                editTextTime.setText(hourOfDay+":"+minute);
                editTextTime.setGravity(Gravity.CENTER);
            }

        }, systemHour, systemMinute, false);
        timePickerDialog.show();




    }


  /*  public class Uploader extends AsyncTask<Void,Integer,Integer>
    {

        @Override
        protected void onPreExecute() {
            progressBar.setMax(100);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            for(int i=0;i<100;++i)
            {
                publishProgress(i);

                try {
                    Thread.sleep(50 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }
*/

  public class MyRunnable implements Runnable
  {

      @TargetApi(Build.VERSION_CODES.O)
      @Override
      public void run() {


         /* Notification.Builder builder = helper.getChannelNotification("image uploading",imageName,dataSent,lengthbmp);
          helper.getManager().notify(123,builder.build());*/

          //testing progress
         /* lengthbmp=10000;
          dataSent=0;*/
          while(checkThread)
          {
              //dataSent+=500;

              dataSent = TrafficStats.getUidTxBytes(myUid())-initialDataUsed;
              Log.i("uid",String.valueOf(dataSent));
              Notification.Builder builder1 = helper.getChannelNotification("title","image uploading",dataSent,lengthbmp);
              helper.getManager().notify(123,builder1.build());

              try {
                  Thread.sleep(100);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }


              if(dataSent>=lengthbmp)
                  checkThread=false;
          }



          /*if(dataSent>=lengthbmp)
          {
              Notification.Builder builder2 = helper.getChannelNotificationUpdate("image upload successful");
              helper.getManager().notify(123,builder2.build());
          }

          else if(dataSent<=lengthbmp)
          {

              Notification.Builder builder2 = helper.getChannelNotificationUpdate("upload not successful");
              helper.getManager().notify(123,builder2.build());
          }*/

          Notification.Builder builder3 = helper.getChannelNotificationClear();
          Notification.Builder builder4 = helper.getChannelNotificationUploadSuccessful();

          if(dataSent>=lengthbmp)
          {
              helper.getManager().notify(123,builder4.build());
          }
          else
          {
              helper.getManager().notify(123,builder3.build());
          }


      }
  }


}



