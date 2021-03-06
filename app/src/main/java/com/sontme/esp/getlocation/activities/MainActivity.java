package com.sontme.esp.getlocation.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.location.GpsStatus;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flurry.android.FlurryAgent;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shobhitpuri.custombuttons.GoogleSignInButton;
import com.sontme.esp.getlocation.BackgroundService;
import com.sontme.esp.getlocation.BuildConfig;
import com.sontme.esp.getlocation.CustomFormatter;
import com.sontme.esp.getlocation.DeviceInfo;
import com.sontme.esp.getlocation.R;
import com.sontme.esp.getlocation.Servers.ObjectSender;
import com.sontme.esp.getlocation.Servers.UDP_Client;
import com.sontme.esp.getlocation.SontHelper;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;

import static com.facebook.FacebookSdk.setAutoLogAppEventsEnabled;

//import com.bugsee.library.Bugsee;

public class MainActivity extends AppCompatActivity implements GpsStatus.Listener, PurchasesUpdatedListener {

    //region DEFINING VARIABLES
    public PublisherAdView mPublisherAdView;
    public Context context = this;
    public static TextView alti;
    public static TextView longi;
    public static TextView lati;
    public static TextView spd;
    public static TextView c;
    public static Switch sw;
    public static Switch sw2;
    public static Switch sw3;
    public static Switch sw4;
    public static TextView dst;

    public static TextView add;
    public static TextView provider;
    public static TextView uniq;
    public static TextView servicestatus;
    public static TextView val_succ;
    public static TextView csv;
    public static TextView zip;
    public static int retry_counter_2 = 0;

    private TextView val_errors;
    WifiManager wm;

    public Handler handler = new Handler();
    public Handler chart_handler = new Handler();
    public BackgroundService backgroundService;

    public static String INSERT_URL = "https://sont.sytes.net/wifilocator/wifi_insert.php";
    public static String[] myColors = {"#f857b5", "#f781bc", "#c5ecbe", "#00b8a9", "#f6416c", "#7effdb", "#b693fe", "#8c82fc", "#ff9de2", "#a8e6cf", "#ffd3b6", "#ffaaa5", "#fc5185", "#384259"};

    //endregion

    private BillingClient billingClient;
    private Handler mHandler = new Handler();
    public AlertDialog.Builder alert_dialog;
    public DrawerLayout dl;
    public ActionBarDrawerToggle t;
    public CallbackManager callbackManager;

    private final Handler mHandler2 = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;
    private double graph2LastXValue = 5d;

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (sw4.isChecked() == true) {
                    longi.setText(BackgroundService.longitude);
                    lati.setText(BackgroundService.latitude);
                    alti.setText(BackgroundService.altitude);
                    spd.setText(BackgroundService.speed + " km/h");
                    if (BackgroundService.distance != null) {
                        dst.setText(SontHelper.round(Double.valueOf(BackgroundService.distance), 2) + " meters");
                    }
                    add.setText(BackgroundService.address);
                    if (BackgroundService.getCount() != 0) {
                        // c.setText(SontHelper.count);
                    }
                    provider.setText(BackgroundService.provider);
                    if (BackgroundService.uniqueAPS.size() > 0) {
                        // uniq.setText(SontHelper.uniqueAPS.size());
                    }
                    //WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                    String ipv4 = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    BackgroundService.ipaddress = ipv4;
                    TextView ip = findViewById(R.id.ip);
                    ip.setText(BackgroundService.ipaddress);
                    servicestatus.setText("Not available");

                    File f1;
                    File f2;
                    String deviceMan = android.os.Build.MANUFACTURER;
                    if (deviceMan.equalsIgnoreCase("huawei")) {
                        f1 = new File("/data/user/0/com.sontme.esp.getlocation/files/wifilocator_database.csv");
                        f2 = new File("/data/user/0/com.sontme.esp.getlocation/files/wifilocator_database.zip");
                    } else {
                        f1 = new File("/storage/emulated/0/Documents/wifilocator_database.csv");
                        f2 = new File("/storage/emulated/0/Documents/wifilocator_database.zip");
                    }

                    TextView csv1 = findViewById(R.id.val_csv);
                    TextView zip1 = findViewById(R.id.val_zip);
                    csv1.setText((int) (f1.length()) / 1024 + " kb");
                    zip1.setText((int) (f2.length()) + " bytes");
                    csv.setText((int) (f1.length()) / 1024 + " kb");
                    zip.setText((int) (f2.length()) + " bytes");
                    val_errors = findViewById(R.id.val_error);
                    val_errors.setText(String.valueOf(BackgroundService.urlList_failed.size()));
                    val_succ = findViewById(R.id.val_succ);
                    val_succ.setText(String.valueOf(BackgroundService.urlList_successed.size()));
                }
            } catch (Exception e) {
                Log.d("TIMER_MAIN_FONTOS", e.toString());
                e.printStackTrace();
            }
            if (BackgroundService.longitude == null) {
                alti.setText("0");
                longi.setText("0");
                lati.setText("0");
                spd.setText("0.00 km/h");
                dst.setText("0.00 meter");
                add.setText("Not available");
                c.setText("0");
                provider.setText("Not available");
                uniq.setText("0");
                servicestatus.setText("Not available");
                TextView ip = findViewById(R.id.ip);
                ip.setText(BackgroundService.ipaddress);

                File f1;
                File f2;
                String deviceMan = android.os.Build.MANUFACTURER;
                if (deviceMan.equalsIgnoreCase("huawei")) {
                    f1 = new File("/data/user/0/com.sontme.esp.getlocation/files/wifilocator_database.csv");
                    f2 = new File("/data/user/0/com.sontme.esp.getlocation/files/wifilocator_database.zip");
                } else {
                    f1 = new File("/storage/emulated/0/Documents/wifilocator_database.csv");
                    f2 = new File("/storage/emulated/0/Documents/wifilocator_database.zip");
                }
                TextView csv1 = findViewById(R.id.val_csv);
                TextView zip1 = findViewById(R.id.val_zip);
                csv1.setText((int) (f1.length()) / 1024 + " kb");
                zip1.setText((int) (f2.length()) + " bytes");
                csv.setText((int) (f1.length()) / 1024 + " kb");
                zip.setText((int) (f2.length()) + " bytes");
                val_errors = findViewById(R.id.val_error);
                val_errors.setText(String.valueOf(BackgroundService.urlList_failed.size()));
                val_succ = findViewById(R.id.val_succ);
                val_succ.setText(String.valueOf(BackgroundService.urlList_successed.size()));
            }
            handler.postDelayed(this, 1000);
        }
    };
    public Runnable chart_runnable = new Runnable() {
        @Override
        public void run() {
            try {
                //Log.d("TIMER_CHART_", "LEFUTOTT");
                if (sw.isChecked() == true) {
                    getChart_timer_updated("https://sont.sytes.net/wifilocator/wifis_chart_updated.php");
                }
                if (sw2.isChecked() == true) {
                    getChart_timer_new("https://sont.sytes.net/wifilocator/wifis_chart_new.php");
                }
                if (sw3.isChecked() == true) {
                    getChart_timer_pie("https://sont.sytes.net/wifilocator/wifis_chart_2.php");
                }
                if (sw4.isChecked() == true) {
                    getStatHttp("https://sont.sytes.net/wifilocator/wifi_stats.php?source=" + BackgroundService.googleAccount);
                }
            } catch (Exception e) {
                Log.d("TIMER_CHART_", e.toString());
            }
            chart_handler.postDelayed(this, 5000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        wm = (WifiManager) getSystemService(WIFI_SERVICE);


        FrameLayout placeholder = findViewById(R.id.placeholder);
        NavigationView nv = findViewById(R.id.nv);
        placeholder.setVisibility(View.VISIBLE);
        nv.setVisibility(View.VISIBLE);

        UDP_Client udp = new UDP_Client("sont.sytes.net", 5000, getApplicationContext());
        udp.execute("STARTED ACTIVITY");

        Button sharebutton = findViewById(R.id.sharebutton);
        Button testbutton = findViewById(R.id.testbutton);
        Button livebtn = findViewById(R.id.testbutton2);
        sharebutton.setVisibility(View.GONE);
        //testbutton.setVisibility(View.GONE);

        testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(),HeatMapp.class);
                //Intent i = new Intent(getApplicationContext(), opencv_realtime.class);
                //Intent i = new Intent(getApplicationContext(), RealTimeChart.class);
                //Intent i = new Intent(getApplicationContext(), Nearby_browser.class);
                //String x = SontHelper.generateKML(BackgroundService.locations);
                //String y = SontHelper.generateGFX(BackgroundService.locations);

                Intent i = new Intent(getApplicationContext(), testAct.class);
                startActivity(i);

                //region CRAWLER START
                /*
                final String[] site = {""};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Start URL");
                builder.setMessage("Please give me a start URL");
                final EditText input = new EditText(MainActivity.this);
                InputFilter filter = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        String filtered = "";
                        for (int i = start; i < end; i++) {
                            char character = source.charAt(i);
                            if (!Character.isWhitespace(character)) {
                                filtered += character;
                            }
                        }
                        return filtered;
                    }
                };
                input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                input.setFilters(new InputFilter[]{filter});
                input.setText("http://");
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        site[0] = input.getText().toString();
                        Intent i = new Intent(getApplicationContext(), CrawlerActivity.class);
                        i.putExtra("site", site[0]);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                */
                //endregion

                // region BL

                //SontHelper.BluetoothFunctions.native_BL();
                //SontHelper.AudioTools.record(MainActivity.this);
                //SontHelper.AudioTools.recordAndPlay(getApplicationContext());

                /*
                final BluetoothDevice[] connectTo = new BluetoothDevice[1];
                SontHelper.playTone();
                SontHelper.vibrate(getApplicationContext());
                Bluetooth bluetooth = new Bluetooth(getApplicationContext());
                DeviceCallback deviceCallback = new DeviceCallback() {
                    @Override
                    public void onDeviceConnected(BluetoothDevice device) {
                        Log.d("BLUETOOTH_LIBRARY_", "Connected to: " + device.getAddress());
                    }

                    @Override
                    public void onDeviceDisconnected(BluetoothDevice device, String message) {
                        Log.d("BLUETOOTH_LIBRARY_", "Disconnected from: " + device.getAddress());
                    }

                    @Override
                    public void onMessage(String message) {
                        Log.d("BLUETOOTH_LIBRARY_", "message_" + message);
                    }

                    @Override
                    public void onError(int errorCode) {
                        Log.d("BLUETOOTH_LIBRARY_", "error_" + errorCode);
                    }

                    @Override
                    public void onConnectError(BluetoothDevice device, String message) {
                        Log.d("BLUETOOTH_LIBRARY_", "ConnectionError_ " + message);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "CONNECTION ERROR:\n" +
                                                device.getName() + "\n" +
                                                device.getAddress() + "\n" +
                                                message,
                                        Toast.LENGTH_SHORT
                                ).show();
                                SontHelper.double_vibrate(getApplicationContext());
                                SontHelper.vibrate(getApplicationContext());
                            }
                        });
                    }
                };
                DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
                    @Override
                    public void onDiscoveryStarted() {
                    }

                    @Override
                    public void onDiscoveryFinished() {
                        try {
                            bluetooth.onStop();
                            bluetooth.onStart();
                            if (!bluetooth.isEnabled())
                                bluetooth.enable();
                            bluetooth.startScanning();

                            //bluetooth.pair(connectTo[0]);
                            //bluetooth.connectToDevice(connectTo[0]);

                            if (connectTo[0] != null) {
                                Log.d("BLUETOOTH_LIBRARY_", "Device NOT null");
                                bluetooth.connectToDevice(connectTo[0]);
                                Log.d("BLUETOOTH_LIBRARY_", "Connecting ! " +
                                        connectTo[0].getName() + " " +
                                        connectTo[0].getAddress());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("BLUETOOTH_LIBRARY_", e.getMessage());
                        }

                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                SontHelper.double_vibrate(getApplicationContext());
                            }
                        });
                    }

                    @Override
                    public void onDeviceFound(BluetoothDevice device) {
                        Log.d("BLUETOOTH_LIBRARY_","device found: " + device.getName() + "_" + device.getAddress());
                        SontHelper.playTone();
                        SontHelper.vibrate(getApplicationContext());
                        try {
                            bluetooth.pair(device);
                            bluetooth.connectToDevice(device);
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.d("BLUETOOTH_LIBRARY_","error: " + e.getMessage());
                        }
                        if (device.getAddress().equals("00:19:86:00:10:AE")) {
                            if (SontHelper.BluetoothFunctions.isBLDevicePaired(device) == true) {
                                Log.d("BLUETOOTH_LIBRARY_", "Found: " +
                                        device.getAddress() + " _ PAIRED");
                            } else {
                                Log.d("BLUETOOTH_LIBRARY_", "Found: " +
                                        device.getAddress() + " _ NOT PAIRED");
                            }
                        } else {
                            UDP_Client udp = new UDP_Client("sont.sytes.net", 5000, getApplicationContext());
                            udp.execute(device.getName() + "_" + device.getAddress() + "_" + BackgroundService.latitude + "_" + BackgroundService.longitude);
                        }
                    }

                    @Override
                    public void onDevicePaired(BluetoothDevice device) {
                        Log.d("BLUETOOTH_LIBRARY_", "pairing_" +
                                device.getName() + " _ " +
                                device.getAddress());
                    }

                    @Override
                    public void onDeviceUnpaired(BluetoothDevice device) {
                        Log.d("BLUETOOTH_LIBRARY_", "un_pairing_" + device.getName());
                    }

                    @Override
                    public void onError(int errorCode) {
                        Log.d("BLUETOOTH_LIBRARY_", "Error_ " + errorCode);
                    }
                };

                bluetooth.setDiscoveryCallback(discoveryCallback);
                bluetooth.setDeviceCallback(deviceCallback);

                bluetooth.onStart();
                if (!bluetooth.isEnabled())
                    bluetooth.enable();
                bluetooth.startScanning();
                */
                // endregion


            }
        });
        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PackageManager m = getPackageManager();
                    String app = getPackageName();
                    PackageInfo p = m.getPackageInfo(app, 0);

                    File f = new File(p.applicationInfo.sourceDir);
                    File f2 = new File(Environment.getExternalStorageDirectory(), "wifilogger_share.apk");
                    copyFile(f, f2);

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    Intent sharingIntent = new Intent();
                    Uri uri = Uri.fromFile(f);
                    sharingIntent.setType("*/*");
                    //sharingIntent.setType("application/vnd.android.package-archive");
                    sharingIntent.setAction(Intent.ACTION_SEND);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(sharingIntent, "Share app"));
                } catch (Exception e) {
                    Log.d("APPINFO", "hiba: " + e.getMessage());
                }
            }
        });
        livebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater alert_inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View alert_v = alert_inflater.inflate(R.layout.custom_dialog_livedata, null, false);
                    TextView alert_txt = alert_v.findViewById(R.id.txt_livedata);

                    alert_dialog.setView(alert_v);
                    alert_txt.setText(Html.fromHtml(BackgroundService.livedata));
                    alert_dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //region API INIT
        //HashMap<String, Object> options = new HashMap<>();
        //options.put(Bugsee.Option.NotificationBarTrigger, false);
        //Bugsee.launch(this, "c92a6836-3405-491e-ac28-e5024324a9d6", options);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoLogAppEventsEnabled(true);
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        FacebookSdk.setAdvertiserIDCollectionEnabled(true);
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("app_started");

        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, getResources().getString(R.string.flurry_key));
        if (BuildConfig.DEBUG)
            Log.d("APP_DEBUG_", "DEBUG on!");

        setAutoLogAppEventsEnabled(true);
        //endregion
        //region Google Firebase
        FirebaseApp.initializeApp(getApplicationContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        //endregion
        //region EXPERIMENTAL FEATURES
        startReceivingObject();

        //SontHelper.BluetoothFunctions.native_BL();
        //endregion
        //region SIGN IN/OUT BUTTONS
        callbackManager = CallbackManager.Factory.create();
        if (isFbLogged()) {
            Log.d("FACEBOOK_LOGIN", "Status: Logged IN");
            ImageView imgv = findViewById(R.id.fbprofimg);
            TextView txtname = findViewById(R.id.fbtxtname);

            txtname.setVisibility(View.VISIBLE);
            imgv.setVisibility(View.VISIBLE);

            txtname.setText(Profile.getCurrentProfile().getName());
            new DownloadImageTask(imgv)
                    .execute(String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(50, 50)));
        } else {
            Log.d("FACEBOOK_LOGIN", "Status: Logged OFF");
            ImageView imgv = findViewById(R.id.fbprofimg);
            TextView txtname = findViewById(R.id.fbtxtname);
            txtname.setText("Logged out");

            txtname.setVisibility(View.GONE);
            imgv.setVisibility(View.GONE);
            //imgv.setImageResource(R.drawable.com_facebook_button_icon);
        }
        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                if (currentProfile == null) {
                    Log.d("FACEBOOK_LOGIN", "LOGGED OUT");
                    ImageView imgv = findViewById(R.id.fbprofimg);
                    TextView txtname = findViewById(R.id.fbtxtname);
                    txtname.setText("Logged out");
                    txtname.setVisibility(View.GONE);
                    imgv.setVisibility(View.GONE);
                    //imgv.setImageResource(R.drawable.com_facebook_button_icon);
                } else {
                    Log.d("FACEBOOK_LOGIN", "PROFILE_CHANGED_1");

                    ImageView imgv = findViewById(R.id.fbprofimg);
                    TextView txtname = findViewById(R.id.fbtxtname);
                    LoginButton btn = findViewById(R.id.login_button);
                    imgv.setVisibility(View.VISIBLE);
                    txtname.setVisibility(View.VISIBLE);
                    txtname.setText(Profile.getCurrentProfile().getName());
                    new DownloadImageTask(imgv)
                            .execute(String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(50, 500)));
                }
            }
        };
        profileTracker.startTracking();

        GoogleSignInButton googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 102);
            }
        });
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.setLoginText("Login");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setFacebookData(loginResult);
                //getFacebookFriends(loginResult);
                Log.d("FACEBOOK_LOGIN", "Success appid: " + loginResult.getAccessToken().getApplicationId());
                Log.d("FACEBOOK_LOGIN", "Success uid: " + loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                Log.d("FACEBOOK_LOGIN", "cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FACEBOOK_LOGIN", "error: " + error.toString());
            }
        });
        Button googlesignout = findViewById(R.id.gsignout);
        googlesignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGLogged() == true) {
                    mGoogleApiClient.disconnect();
                    mGoogleSignInClient.revokeAccess();
                    mGoogleSignInClient.signOut();
                    googlesignout.setVisibility(View.GONE);

                    ImageView img = findViewById(R.id.googleprofimg);
                    GoogleSignInButton btn = findViewById(R.id.sign_in_button);
                    TextView t = findViewById(R.id.googletxtname);
                    ImageView i = findViewById(R.id.googleprofimg);
                    t.setVisibility(View.GONE);
                    i.setVisibility(View.GONE);
                    btn.setVisibility(View.VISIBLE);
                    img.setVisibility(View.GONE);
                } else {
                    googlesignout.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "You are not logged in", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (isGLogged() == true) {
            googleSignInButton.setVisibility(View.GONE);
            Button so = findViewById(R.id.gsignout);
            ImageView img = findViewById(R.id.googleprofimg);
            img.setVisibility(View.VISIBLE);
            so.setVisibility(View.VISIBLE);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            Log.d("GOOGLE_LOGIN", "already logged in " + account.getEmail());
            //googleSignInButton.setText("Unlink Google");
            TextView gtxt = findViewById(R.id.googletxtname);
            ImageView gimg = findViewById(R.id.googleprofimg);
            gtxt.setText(account.getDisplayName());
            gtxt.setVisibility(View.VISIBLE);
            new DownloadImageTask(gimg)
                    .execute(account.getPhotoUrl().toString());
        } else {
            ImageView img = findViewById(R.id.googleprofimg);
            img.setVisibility(View.GONE);
            //img.setImageResource(R.drawable.exiticon);
            googleSignInButton.setVisibility(View.VISIBLE);
            googleSignInButton.setText("Sign in with Google");
            Button so = findViewById(R.id.gsignout);
            so.setVisibility(View.GONE);
            TextView gtxt = findViewById(R.id.googletxtname);
            gtxt.setVisibility(View.GONE);
            Log.d("GOOGLE_LOGIN", "logged out");
            //mGoogleSignInClient.silentSignIn();
        }

        //endregion
        //region LinearLayout Definitions
        LinearLayout lin2 = findViewById(R.id.firstlin2);
        LinearLayout lin3 = findViewById(R.id.firstlin3);
        LinearLayout lin4 = findViewById(R.id.firstlin4);
        LinearLayout lin5 = findViewById(R.id.firstlin5);
        lin2.setVisibility(LinearLayout.GONE);
        lin3.setVisibility(LinearLayout.GONE);
        lin4.setVisibility(LinearLayout.GONE);
        lin5.setVisibility(LinearLayout.GONE);
        //endregion
        //region PERMISSIONS / GPS INITIALIZATION
        SontHelper.wifi_check_enabled(getApplicationContext());
        SontHelper.adminPermission_check(getApplicationContext(), MainActivity.this);
        SontHelper.requestPermissions(this);
        SontHelper.turnGPSOn(getApplicationContext());
        //endregion
        //region NOTIFICATION BAR COLOR RANDOMIZATOR
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        Collections.shuffle(Arrays.asList(myColors));
        window.setStatusBarColor(Color.parseColor(myColors[1]));
        window.setNavigationBarColor(Color.parseColor(myColors[1]));
        setTitleColor(Color.parseColor(myColors[1]));
        //endregion
        //region UNHANDLED EXCEPTION HANDLER
        Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                UDP_Client udp = new UDP_Client("sont.sytes.net", 5000, getApplicationContext());
                udp.execute("ERROR_" + ex.getMessage() + "\n" + ex.getStackTrace());

                Intent mStartActivity = new Intent(context, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
        //endregion
        //region GET ACCOUNT INFO
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        String acc = "no";
        for (Account s : list) {
            acc = String.valueOf(s.name);
            Log.d("APP_LOGIN", s.name);
        }
        if (acc.length() > 3) {
            BackgroundService.googleAccount = acc;
            Log.d("APP_LOGIN", acc);
        } else {
            BackgroundService.googleAccount = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        //endregion
        //region DRAWER VERSION INFO
        View hView = nv.getHeaderView(0);
        TextView tex = hView.findViewById(R.id.header_verinfo);
        String version = "Version: " + BuildConfig.VERSION_NAME + " Build: " + BuildConfig.VERSION_CODE;
        tex.setText(version);
        //endregion
        //region DRAWER
        dl = findViewById(R.id.drawler);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.main:
                        dl.closeDrawers();
                        return true;
                    case R.id.map:
                        dl.closeDrawers();
                        Intent i = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.list:
                        dl.closeDrawers();
                        Intent i2 = new Intent(MainActivity.this, ListActivity.class);
                        startActivity(i2);
                        return true;
                    case R.id.nearby:
                        dl.closeDrawers();
                        //Intent i4 = new Intent(MainActivity.this, NearbyActivity.class);
                        //startActivity(i4);
                        Intent i4 = new Intent(getApplicationContext(), Nearby_browser.class);
                        startActivity(i4);
                        return true;
                    case R.id.more:
                        dl.closeDrawers();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sont.sytes.net/moreapps.php"));
                        startActivity(browserIntent);
                        return true;
                    default:
                        dl.closeDrawers();
                        return true;
                }
            }
        });

        mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
        mPublisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(getApplicationContext(), "AD loaded", Toast.LENGTH_SHORT).show();
                Log.d("ADVERT_", "ad loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (errorCode == 3) {
                    Log.d("ADVERT_", "code ok but no ad");
                } else {
                    // CODE NOT OK
                    Log.d("ADVERT_", "code not ok");
                }
            }
        });
        //endregion
        //region UI ELEMENTS
        alti = findViewById(R.id.alti);
        add = findViewById(R.id.add);
        c = findViewById(R.id.c);
        longi = findViewById(R.id.longi);
        lati = findViewById(R.id.lati);
        spd = findViewById(R.id.spd);
        dst = findViewById(R.id.dst);
        sw = findViewById(R.id.switch1);
        sw2 = findViewById(R.id.switch2);
        sw3 = findViewById(R.id.switch3);
        sw4 = findViewById(R.id.switch4);

        provider = findViewById(R.id.prov);
        uniq = findViewById(R.id.uniq);
        WebView webview = findViewById(R.id.webview);
        servicestatus = findViewById(R.id.servstatus);
        csv = findViewById(R.id.val_csv);
        zip = findViewById(R.id.val_zip);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    LinearLayout lin2 = findViewById(R.id.firstlin2);
                    lin2.setVisibility(LinearLayout.VISIBLE);
                } else {
                    LinearLayout lin2 = findViewById(R.id.firstlin2);
                    lin2.setVisibility(LinearLayout.GONE);
                }
            }
        });
        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    LinearLayout lin3 = findViewById(R.id.firstlin3);
                    lin3.setVisibility(LinearLayout.VISIBLE);
                } else {
                    LinearLayout lin3 = findViewById(R.id.firstlin3);
                    lin3.setVisibility(LinearLayout.GONE);
                }
            }
        });
        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    LinearLayout lin4 = findViewById(R.id.firstlin4);
                    lin4.setVisibility(LinearLayout.VISIBLE);
                } else {
                    LinearLayout lin4 = findViewById(R.id.firstlin4);
                    lin4.setVisibility(LinearLayout.GONE);
                }
            }
        });
        sw4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    LinearLayout lin5 = findViewById(R.id.firstlin5);
                    lin5.setVisibility(LinearLayout.VISIBLE);
                } else {
                    LinearLayout lin5 = findViewById(R.id.firstlin5);
                    lin5.setVisibility(LinearLayout.GONE);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                }
            }
        });

        //endregion
        //region SET UP BILLING
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                //if (billingResult.getResponseCode() == BillingResponse.OK) {}
                Log.d("BILLING_", "debug: " + billingResult.getDebugMessage());
                Log.d("BILLING_", "resp code: " + billingResult.getResponseCode());
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d("BILLING_", "disconnected");
            }
        });
        //endregion
        //region REALTIME GRAPH
        GraphView graph = findViewById(R.id.rlgraph_main);
        graph.setTitle("Realtime WiFi");
        mSeries1 = new LineGraphSeries<>(generateData());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        mSeries1.setTitle("Realtime Data");
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.nicered1));
        paint.setAlpha(100);
        paint.setStrokeWidth(10);
        mSeries1.setCustomPaint(paint);
        graph.addSeries(mSeries1);
        //endregion
        //region OPEN_CV
        if (OpenCVLoader.initDebug()) {
            Log.d("open_cv", "init done");
        } else {
            Log.d("open_cv", "init fail");
        }
        //endregion

        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Toast.makeText(getApplicationContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BackgroundService.LocalBinder mLocalBinder = (BackgroundService.LocalBinder) service;
                backgroundService = mLocalBinder.getServerInstance();
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MainActivity.this, BackgroundService.class));
            bindService(new Intent(MainActivity.this, BackgroundService.class), mConnection, BIND_AUTO_CREATE);
        } else {
            startService(new Intent(MainActivity.this, BackgroundService.class));
            bindService(new Intent(MainActivity.this, BackgroundService.class), mConnection, BIND_AUTO_CREATE);
        }

        // TIMED EVENTS (CHARTS / LIVE DATA)
        alert_dialog = new AlertDialog.Builder(MainActivity.this);
        chart_handler.postDelayed(chart_runnable, 5000);
        handler.postDelayed(runnable, 1000);
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                mSeries1.appendData(new DataPoint(graph2LastXValue, getWifiC()), true, 100);
                mHandler2.postDelayed(this, 100);
            }
        };
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //Log.d("test_func", "ran");
                    //LayoutInflater alert_inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //View alert_v = alert_inflater.inflate(R.layout.custom_dialog_livedata, null, false);
                    //TextView alert_txt = alert_v.findViewById(R.id.txt_livedata);
                    //alert_dialog.setView(alert_v);
                    //alert_txt.setText(Html.fromHtml(BackgroundService.livedata));
                    //alert_dialog.show();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        });
        mHandler2.postDelayed(mTimer1, 0);
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        SontHelper.takeFullScreenshot(getApplicationContext(), MainActivity.this);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //thread.start();

        Thread th = new Thread() {
            public void run() {
                while (true) {
                    try {

                        long start = System.currentTimeMillis();

                        View iv = findViewById(R.id.drawler);
                        iv.setDrawingCacheEnabled(true);
                        Bitmap img = iv.getDrawingCache();

                        int chunks = 4;

                        img = resizeBitmap(img, 4);
                        byte[] image = SontHelper.bitmapToArray(img);
                        byte[] compressed = SontHelper.GZIPCompress(image);
                        /*
                        ArrayList<Bitmap> splitted = splitImagee(img, chunks);
                        HashMap<Integer, byte[]> parts_byte = new HashMap<>();

                        int c = 0;
                        for(Bitmap b : splitted){
                            byte[] bit = bitmapToArray_2(b);
                            parts_byte.put(c, bit);
                            c++;
                        }
                        */
                        ObjectSender os = new ObjectSender(compressed);
                        //ObjectSender os = new ObjectSender(parts_byte);
                        os.execute();

                        iv.setDrawingCacheEnabled(false);
                        img.recycle();

                    } catch (Exception e) {
                        Log.d("STREAMING_", e.toString());
                        e.printStackTrace();
                    }

                }
            }
        };
        if (SontHelper.isWifiConnected(getApplicationContext())) {
            //th.start();
        }
    }

    public static Bitmap resizeBitmap(Bitmap bmp, int div) {
        Bitmap b = bmp;
        try {
            b = bmp.copy(bmp.getConfig(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bm = Bitmap.createScaledBitmap(b, b.getWidth() / div, b.getHeight() / div, true);
        return bm;
    }

    public static byte[] bitmapToArray(Bitmap bmp) {
        ByteBuffer buffer = null;
        try {
            int bytes = bmp.getByteCount();
            buffer = ByteBuffer.allocate(bytes);
            bmp.copyPixelsToBuffer(buffer);
        } catch (Exception e) {
            //e.printStackTrace();
            //return null;
        }
        return buffer.array();
    }

    public static byte[] bitmapToArray_2(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Bitmap b = bmp.copy(bmp.getConfig(), true);
            b.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static Bitmap[] splitBitmap(Bitmap picture) {
        Bitmap[] imgs = new Bitmap[4];

        imgs[0] = Bitmap.createBitmap(picture, 0, 0, picture.getWidth() / 2, picture.getHeight() / 2);
        imgs[1] = Bitmap.createBitmap(picture, picture.getWidth() / 2, 0, picture.getWidth() / 2, picture.getHeight() / 2);
        imgs[2] = Bitmap.createBitmap(picture, 0, picture.getHeight() / 2, picture.getWidth() / 2, picture.getHeight() / 2);
        imgs[3] = Bitmap.createBitmap(picture, picture.getWidth() / 2, picture.getHeight() / 2, picture.getWidth() / 2, picture.getHeight() / 2);

        return imgs;
    }

    private static ArrayList<Bitmap> splitImagee(Bitmap image, int chunkNumbers) {
        int rows, cols;
        int chunkHeight, chunkWidth;

        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(chunkNumbers);

        Bitmap bitmap = image;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmap.getHeight() / rows;
        chunkWidth = bitmap.getWidth() / cols;

        int yCoord = 0;
        for (int x = 0; x < rows; x++) {
            int xCoord = 0;
            for (int y = 0; y < cols; y++) {
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        return chunkedImages;
    }

    private static Bitmap[] splitImage(Bitmap image, int chunkNumbers) {

        int rows, cols;
        int chunkHeight, chunkWidth;

        //ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(chunkNumbers);

        Bitmap[] chunkedImages = new Bitmap[chunkNumbers];
        try {
            //Getting the scaled bitmap of the source image
            //BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            //Bitmap bitmap = drawable.getBitmap();
            Bitmap bitmap = image;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            rows = cols = (int) Math.sqrt(chunkNumbers);
            chunkHeight = bitmap.getHeight() / rows;
            chunkWidth = bitmap.getWidth() / cols;

            //xCoord and yCoord are the pixel positions of the image chunks
            int yCoord = 0;
            for (int x = 0; x < rows; x++) {
                int xCoord = 0;
                for (int y = 0; y < cols; y++) {
                    //chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                    chunkedImages[x] = Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight);
                    xCoord += chunkWidth;
                }
                yCoord += chunkHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chunkedImages;
    }

    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");

                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();

                            if (Profile.getCurrentProfile() != null) {
                                Log.d("FACEBOOK_LOGIN_", "PROFILEPICTURE: " + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                ImageView imgv = findViewById(R.id.fbprofimg);
                                TextView txtname = findViewById(R.id.fbtxtname);
                                txtname.setText(Profile.getCurrentProfile().getName());
                                new DownloadImageTask(imgv)
                                        .execute(String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(50, 50)));
                                imgv.setVisibility(View.VISIBLE);
                                txtname.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("FACEBOOK_LOGIN_", "profile is null !");
                                ImageView imgv = findViewById(R.id.fbprofimg);
                                TextView txtname = findViewById(R.id.fbtxtname);
                                imgv.setVisibility(View.VISIBLE);
                                txtname.setVisibility(View.VISIBLE);
                            }

                            Log.d("FACEBOOK_LOGIN_" + "Email: ", email);
                            Log.d("FACEBOOK_LOGIN_" + "FirstName: ", firstName);
                            Log.d("FACEBOOK_LOGIN_" + "LastName: ", lastName);
                            Log.d("FACEBOOK_LOGIN_" + "ID: ", id);
                            Log.d("FACEBOOK_LOGIN_" + "Link: ", link);
                            Log.d("FACEBOOK_LOGIN_", response.getJSONObject().getString("friends"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender,friends");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private boolean isGLogged() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        return account != null;
    }

    private boolean isFbLogged() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        return isLoggedIn;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        String type = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
                        String name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        Log.d("GOOGLE_ACC_", type + "_" + name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 5000:
                    Log.d("SCREENSHOT_", "ran");
                    MediaProjectionManager mgr = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    MediaProjection a = mgr.getMediaProjection(resultCode, data);

                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    int mDensity = metrics.densityDpi;
                    Display mDisplay = getWindowManager().getDefaultDisplay();

                    Point size = new Point();
                    mDisplay.getSize(size);
                    int mWidth = size.x;
                    int mHeight = size.y;

                    // start capture reader
                    ImageReader mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
                    int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
                    VirtualDisplay mVirtualDisplay = a.createVirtualDisplay("screenshot", mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);

                    ImageAvailableListener listener = new ImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            //int w = reader.acquireLatestImage().getWidth();
                            Image image = reader.acquireLatestImage();
                            Log.d("screenshot_", "got image: " + image.getHeight() + " -> " + reader.getHeight());

                            final Image.Plane[] planes = image.getPlanes();

                            final ByteBuffer buffer = planes[0].getBuffer();
                            int offset = 0;
                            int pixelStride = planes[0].getPixelStride();
                            int rowStride = planes[0].getRowStride();
                            int rowPadding = rowStride - pixelStride * 200;

                            Bitmap bmp = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.RGB_565);
                            bmp.copyPixelsFromBuffer(buffer);

                            Log.d("screenshot_", "h: " + bmp.getHeight() + " w: " + bmp.getWidth());

                            Bitmap img = resizeBitmap(bmp, 4);
                            byte[] imagee = SontHelper.bitmapToArray(img);
                            byte[] compressed = SontHelper.GZIPCompress(imagee);
                            ObjectSender os = new ObjectSender(compressed);
                            os.execute();
                            img.recycle();

                            image.close();
                            reader.close();
                        }
                    };
                    mImageReader.setOnImageAvailableListener(listener, new Handler());

                    //a.registerCallback(listener, new Handler());

                case 999:
                    try {
                        /*
                        Uri selectedImage = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap faced_bmp = BitmapFactory.decodeStream(imageStream);
                        AlertDialog.Builder face_img_dialog = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.custom_dialog_img_full, null, false);
                        ImageView imgv = v.findViewById(R.id.popupimg);
                        imgv.setImageBitmap(SontHelper.findFaceDrawRectROI(faced_bmp, 5));
                        //imgv.setImageBitmap(SontHelper.findFaceCropROI(faced_bmp,5));
                        face_img_dialog.setView(v);
                        face_img_dialog.show();
                        */
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        if (requestCode == 102) { // GOOGLE SIGN IN CALLBACK
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GOOGLE_LOGIN", "Success: " + account.getEmail() + " _ " + account.getDisplayName() + " _ " + account.getPhotoUrl().toString());
                TextView gtxt = findViewById(R.id.googletxtname);
                ImageView gimg = findViewById(R.id.googleprofimg);
                Button btn = findViewById(R.id.gsignout);
                GoogleSignInButton btn2 = findViewById(R.id.sign_in_button);

                gimg.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
                gtxt.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
                gtxt.setText(account.getDisplayName());
                new DownloadImageTask(gimg)
                        .execute(account.getPhotoUrl().toString());
            } catch (Exception e) {
                Log.d("GOOGLE_LOGIN", "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public void startReceivingObject() {
        //region udp

        Thread thx = new Thread() {
            public void run() {
                boolean check = true;
                try {
                    ServerSocket server = new ServerSocket(1234);
                    while (check) {
                        Socket s = server.accept();

                        InputStream is = s.getInputStream();
                        byte[] data = IOUtils.toByteArray(is);

                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                        Object obj = ois.readObject();

                        if (obj instanceof SealedObject) {
                            byte[] key = "1234567890000000".getBytes();
                            String transformation = "Blowfish";
                            SecretKeySpec sks = new SecretKeySpec(key, transformation);
                            Cipher cipher = Cipher.getInstance(transformation);
                            cipher.init(Cipher.DECRYPT_MODE, sks);
                            Object o = ((SealedObject) obj).getObject(cipher);

                            if (o instanceof Location) {
                                String str = (String) ((SealedObject) obj).getObject(cipher);
                                Gson gson = new Gson();
                                Location loc = gson.fromJson(str, Location.class);
                                Log.d("OBJECT_SCK_", "Received: Location : ");
                            }
                            if (o instanceof DeviceInfo) {
                                String str = (String) ((SealedObject) obj).getObject(cipher);
                                Gson gson = new Gson();
                                DeviceInfo di = gson.fromJson(str, DeviceInfo.class);
                                Log.d("OBJECT_SCK_", "Received: DeviceInfo : " + di.toString());
                            }
                            if (o instanceof BluetoothDevice) {
                                String str = (String) ((SealedObject) obj).getObject(cipher);
                                Gson gson = new Gson();
                                BluetoothDevice bd = gson.fromJson(str, BluetoothDevice.class);
                                Log.d("OBJECT_SCK_", "Received: BluetoothDevice : " + bd.toString());
                            }
                            //DeviceInfo di = (DeviceInfo) ((SealedObject) obj).getObject(cipher);


                            //Log.d("OBJECT_SCK_", "RECEIVED: sealed, DEVICEINFO -> " + di.TEST1);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("OBJECT_SCK_", "ERROR_main_" + e.toString());
                }
            }
        };
        thx.start();
        //endregion
    }

    private DataPoint[] generateData() {
        DataPoint[] values = new DataPoint[1];
        DataPoint v = new DataPoint(0, 0);
        values[0] = v;
        return values;
    }

    private double getWifiC() {
        double r = 0;
        try {
            r = BackgroundService.scanResults_forchart.size();
        } catch (Exception e) {
            r = 0;
        }
        return r;
    }

    @Override
    public void onStop() {
        UDP_Client udp = new UDP_Client("sont.sytes.net", 5000, getApplicationContext());
        udp.execute("STOPPED ACT");
        super.onStop();
    }

    @Override
    public void onResume() {
        UDP_Client udp = new UDP_Client("sont.sytes.net", 5000, getApplicationContext());
        udp.execute("RESUMING ACT");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        UDP_Client udp = new UDP_Client("sont.sytes.net", 5000, getApplicationContext());
        udp.execute("PAUSING ACT");
        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void getChart_timer_updated(String path) {
        AndroidNetworking.get(path)
                .setTag("chart_auto")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        LineChart newchart = findViewById(R.id.newchart);
                        List<Entry> entries = new ArrayList<Entry>();

                        Map<Integer, Integer> hours = new HashMap<Integer, Integer>();
                        for (int i = 0; i < 24; i++) {
                            hours.put(i, 0);
                        }
                        Map<Integer, Integer> values = new HashMap<Integer, Integer>();
                        Map<Integer, Integer> combined = new HashMap<Integer, Integer>(hours);

                        String str = response;
                        String[] lines = str.trim().split("\\r?\\n");
                        try {
                            for (String line : lines) {
                                String[] words = line.trim().split("\\s+");
                                values.put(Integer.valueOf(words[0]), Integer.valueOf(words[1]));
                            }
                            combined.putAll(values);
                            for (Map.Entry<Integer, Integer> entry : combined.entrySet()) {
                                int key = entry.getKey();
                                int value = entry.getValue();
                                entries.add(new Entry(key, value));
                            }
                        } catch (Exception e) {
                            Log.d("Error_", e.getMessage());
                            e.printStackTrace();
                        }

                        LineDataSet dataSet = new LineDataSet(entries, "Stats");
                        dataSet.setLineWidth(4f);
                        dataSet.setDrawFilled(true);
                        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.color.nicered1);
                        dataSet.setFillDrawable(drawable);
                        dataSet.setDrawHighlightIndicators(true);
                        Collections.shuffle(Arrays.asList(myColors));
                        dataSet.setHighLightColor(Color.parseColor(myColors[1]));
                        dataSet.setHighlightLineWidth(3f);
                        dataSet.setDrawValues(true);
                        dataSet.setValueTextSize(13);
                        dataSet.setValueFormatter(new DefaultValueFormatter(0));
                        dataSet.setHighlightEnabled(false);
                        dataSet.setDrawHighlightIndicators(false);
                        dataSet.setColors(Color.parseColor(myColors[0]));
                        dataSet.setLineWidth(1);
                        dataSet.setValueFormatter(new CustomFormatter());
                        LineData lineData = new LineData(dataSet);
                        newchart.setData(lineData);
                        newchart.setDrawBorders(false);
                        newchart.getAxisRight().setDrawGridLines(false);
                        newchart.getAxisLeft().setDrawGridLines(false);

                        newchart.getXAxis().setDrawGridLines(false);
                        newchart.getXAxis().setDrawLabels(true);

                        newchart.getDescription().setEnabled(false);
                        newchart.getLegend().setEnabled(false);
                        newchart.setScaleEnabled(false);
                        newchart.setPinchZoom(false);
                        newchart.invalidate();
                        newchart.animateX(500);
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    public void getChart_timer_new(String path) {
        AndroidNetworking.get(path)
                .setTag("chart_auto")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        LineChart newchart = findViewById(R.id.newchart2);
                        List<Entry> entries = new ArrayList<Entry>();

                        Map<Integer, Integer> hours = new HashMap<Integer, Integer>();
                        for (int i = 0; i <= 24; i++) {
                            hours.put(i, 0);
                        }
                        Map<Integer, Integer> values = new HashMap<Integer, Integer>();
                        Map<Integer, Integer> combined = new HashMap<Integer, Integer>(hours);

                        String str = response;

                        String[] lines = str.trim().split("\\r?\\n");
                        try {
                            for (String line : lines) {
                                String[] words = line.trim().split("\\s+");
                                values.put(Integer.valueOf(words[0]), Integer.valueOf(words[1]));
                            }
                            combined.putAll(values);
                            for (Map.Entry<Integer, Integer> entry : combined.entrySet()) {
                                int key = entry.getKey();
                                int value = entry.getValue();
                                entries.add(new Entry(key, value));
                            }
                        } catch (Exception e) {
                        }

                        LineDataSet dataSet = new LineDataSet(entries, "Stats");
                        dataSet.setLineWidth(4f);
                        dataSet.setDrawFilled(true);
                        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.color.nicered1);
                        dataSet.setFillDrawable(drawable);
                        dataSet.setDrawHighlightIndicators(true);
                        Collections.shuffle(Arrays.asList(myColors));
                        dataSet.setHighLightColor(Color.parseColor(myColors[1]));
                        dataSet.setHighlightLineWidth(3f);
                        dataSet.setDrawValues(true);
                        dataSet.setLineWidth(1);
                        dataSet.setValueTextSize(13);
                        dataSet.setValueFormatter(new DefaultValueFormatter(0));
                        dataSet.setHighlightEnabled(false);
                        dataSet.setDrawHighlightIndicators(false);
                        dataSet.setColors(Color.parseColor(myColors[0]));
                        dataSet.setValueFormatter(new CustomFormatter());
                        LineData lineData = new LineData(dataSet);
                        newchart.setData(lineData);
                        newchart.setDrawBorders(false);
                        newchart.getAxisRight().setDrawGridLines(false);
                        newchart.getAxisLeft().setDrawGridLines(false);

                        newchart.getXAxis().setDrawGridLines(false);
                        newchart.getXAxis().setDrawLabels(true);

                        newchart.getDescription().setEnabled(false);
                        newchart.getLegend().setEnabled(false);
                        newchart.setScaleEnabled(false);
                        newchart.setPinchZoom(false);
                        newchart.invalidate();
                        newchart.animateX(500);
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    public void getChart_timer_pie(String path) {
        AndroidNetworking.get(path)
                .setTag("chart_auto2")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        PieChart piechart = findViewById(R.id.piechart);
                        List<PieEntry> entries = new ArrayList<>();

                        String str = response;
                        String[] lines = str.trim().split("xxx");

                        int i = 0;
                        Collections.shuffle(Arrays.asList(myColors));
                        int ossz = 0;
                        for (String line : lines) {
                            String[] words = line.trim().split("\\s+");
                            if ((words[0] == "73bedfbd149e01de") || (words[0].equals("73bedfbd149e01de"))) {
                                words[0] = "Sajat";
                            } else if ((words[0] == "4d32dfcf42ebf336") || (words[0].equals("4d32dfcf42ebf336"))) {
                                words[0] = "Anya";
                            } else if ((words[0] == "4dddd08a27a4e4cd") || (words[0].equals("4dddd08a27a4e4cd"))) {
                                words[0] = "Fater";
                            }
                            try {
                                entries.add(new PieEntry(Integer.valueOf(words[1]), words[0]));
                                ossz = ossz + Integer.valueOf(words[1]);
                                i++;
                            } catch (Exception e) {
                            }
                        }

                        PieDataSet set = new PieDataSet(entries, "");
                        set.setColors(Color.parseColor(myColors[0]), Color.parseColor(myColors[1]), Color.parseColor(myColors[2]), Color.parseColor(myColors[3]), Color.parseColor(myColors[4]), Color.parseColor(myColors[5]));
                        set.setValueTextColor(Color.BLACK);
                        set.setValueTextSize(12);
                        PieData data = new PieData(set);

                        Description d = new Description();
                        d.setText("");
                        piechart.setDescription(d);
                        piechart.setCenterText("Shares" + "\n(" + ossz + ")");
                        piechart.getLegend().setEnabled(true);
                        //piechart.animateXY(2000, 2000);
                        piechart.setEntryLabelColor(SontHelper.invertColor(Color.parseColor(myColors[0])));
                        piechart.setEntryLabelTextSize(12);

                        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        set.setSliceSpace(5);

                        piechart.setExtraBottomOffset(20f);
                        piechart.setExtraLeftOffset(20f);
                        piechart.setExtraRightOffset(20f);
                        piechart.animateY(500);
                        set.setValueLinePart1OffsetPercentage(10.f);
                        set.setValueLinePart1Length(0.43f);
                        set.setValueLinePart2Length(.1f);
                        piechart.getLegend().setWordWrapEnabled(true);
                        Legend l = piechart.getLegend();
                        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                        l.setXEntrySpace(7f);
                        l.setYEntrySpace(0f);
                        l.setYOffset(0f);
                        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
                        l.setWordWrapEnabled(true);

                        piechart.setData(data);
                        piechart.invalidate();
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    public void getStatHttp(String path) {
        path = path.replaceAll(Pattern.quote("+"), "");
        path = path.replaceAll(Pattern.quote(" "), "%20");
        path = path.replaceAll(Pattern.quote("%20"), "");
        Log.d("STAT_", path);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(path, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = "";
                    str = new String(responseBody, StandardCharsets.UTF_8);
                    Log.d("STAT_", str);
                    String[] vagott = str.split(Pattern.quote("/"));
                    // 0 - new me
                    // 1 - new all
                    // 2 - up me
                    // 3 - up all
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String NOTIFICATION_CHANNEL_ID = "new";
                        String channelName = "new";
                        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);
                        chan.setLightColor(Color.BLUE);
                        chan.setShowBadge(true);
                        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        assert manager != null;
                        manager.createNotificationChannel(chan);

                        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

                        for (int i = 0; i < vagott.length; i++) {
                            vagott[i] = vagott[i].replace("\n", "").replace("\r", "");
                        }

                        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notif_lay_up);
                        contentView.setTextViewText(R.id.texttxt, "Up: " + vagott[2] + "/" + vagott[3] + " | New: " + vagott[0] + "/" + vagott[1] + " | Travelled: " + BackgroundService.sumOfTravelDistance + "m");

                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
                        Notification notification = notificationBuilder
                                .setOngoing(true)
                                .setSmallIcon(R.drawable.gps2)
                                .setGroup("wifi")
                                //.setContentTitle("Statistics")
                                .setContentTitle("Up: " + vagott[2] + "/" + vagott[3] + " | New: " + vagott[0] + "/" + vagott[1] + " | " + BackgroundService.sumOfTravelDistance + "m")
                                .setContent(contentView)
                                .setSubText("Searching")
                                .setContentIntent(pi)
                                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                                .setCategory(Notification.CATEGORY_SERVICE)
                                .setNumber(1)
                                .build();
                        //startForeground(3, notification);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            manager.notify(1, notification);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean getUseSynchronousMode() {
                return false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (retry_counter_2 < 20) {
                    getStatHttp("https://sont.sytes.net/wifilocator/wifi_stats.php?source=" + BackgroundService.googleAccount);
                    retry_counter_2++;
                }
            }
        });
    }

    public static void copyFile(File src, File dst) {
        Thread thread = new Thread() {
            public void run() {
                try (InputStream in = new FileInputStream(src)) {
                    try (OutputStream out = new FileOutputStream(dst)) {
                        // Transfer bytes from in to out
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("APPINFO", "Copy Fail");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("APPINFO", "Copy Fail");
                }
                Log.d("APPINFO", "Copy Done");
            }
        };
        thread.start();
    }

    @Override
    public void onGpsStatusChanged(int event) {
        //mStatus = mService.getGpsStatus(mStatus);
        if (event != GpsStatus.GPS_EVENT_FIRST_FIX &&
                event != GpsStatus.GPS_EVENT_SATELLITE_STATUS &&
                event != GpsStatus.GPS_EVENT_STARTED &&
                event != GpsStatus.GPS_EVENT_STOPPED) {
            //Toast.makeText(getApplicationContext(), "GPS Unknown event: " + event, Toast.LENGTH_SHORT).show();
        }
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                //Toast.makeText(getApplicationContext(), "GPS Event Started", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                //Toast.makeText(getApplicationContext(), "GPS Event Stopped", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                //Toast.makeText(getApplicationContext(), "GPS Event First FIX", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //Toast.makeText(getApplicationContext(), "GPS SAT Status", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        Log.d("BILLING_", "debug_callback: " + billingResult.getDebugMessage());
    }

    public void findFaceOnAllPhotos() {
        try {
            ArrayList<String> photos = new ArrayList<>();
            photos = SontHelper.getAllImagesPath(MainActivity.this);

            Uri photouri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Log.d("face_find", "count: " + photos.size());
            int i = 0;
            for (String photo : photos) {
                i++;
                Log.d("face_find", i + " photo: " + photo);
                File f = new File(photo);
                Uri img_uri = Uri.fromFile(f);
                InputStream is = getContentResolver().openInputStream(img_uri);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap face_bmp = BitmapFactory.decodeStream(bis);
                Bitmap rect_face_bmp = SontHelper.findFaceDrawRectROI(face_bmp, 5);
                Log.d("face_find", "original bytes: " + face_bmp.getByteCount());
                Log.d("face_find", "rected bytes: " + rect_face_bmp.getByteCount());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}

class ImageAvailableListener extends android.media.projection.MediaProjection.Callback implements ImageReader.OnImageAvailableListener {
    @Override
    public void onImageAvailable(ImageReader reader) {
        //int w = reader.acquireLatestImage().getWidth();
        //Log.d("screenshot_", "got image: " + w + " -> " + reader.getHeight());
        //reader.close();
    }
}