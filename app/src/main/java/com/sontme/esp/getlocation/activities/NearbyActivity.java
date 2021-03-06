package com.sontme.esp.getlocation.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sontme.esp.getlocation.ApStrings;
import com.sontme.esp.getlocation.BackgroundService;
import com.sontme.esp.getlocation.BuildConfig;
import com.sontme.esp.getlocation.R;
import com.sontme.esp.getlocation.SontHelper;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.clustering.StaticCluster;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

// something is wrong with zooming in/out the map. the markers are jumping around

public class NearbyActivity extends AppCompatActivity implements GpsStatus.Listener {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private MapView map;
    private FloatingActionButton btn;
    String content = null;
    private int NEARBY_MIN_DISTANCE = 1000;

    static Map<Location, ApStrings> loc_ssid2 = new HashMap<Location, ApStrings>();

    public static List<GeoPoint> geoPoints;
    public static ArrayList<OverlayItem> overlayItemArray;
    public static Polyline line;
    public BackgroundService backgroundService;

    public Switch sw; // open wifi only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        Intent mIntent = new Intent(NearbyActivity.this, BackgroundService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);

        Button km1 = findViewById(R.id.maxdistok1);
        Button km10 = findViewById(R.id.maxdistok10);
        Button kminf = findViewById(R.id.maxdistokinf);
        sw = findViewById(R.id.asdd);
        km1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NEARBY_MIN_DISTANCE = 1000;
                if (sw.isChecked() == false) {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_all.php");
                } else {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_open.php");
                }
            }
        });
        km10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NEARBY_MIN_DISTANCE = 10000;
                if (sw.isChecked() == false) {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_all.php");
                } else {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_open.php");
                }
            }
        });
        kminf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NEARBY_MIN_DISTANCE = Integer.MAX_VALUE;
                if (sw.isChecked() == false) {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_all.php");
                } else {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_open.php");
                }

            }
        });


        map = findViewById(R.id.osmmap2);
        btn = findViewById(R.id.button6);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw.isChecked() == false) {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_all.php");
                } else {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_open.php");
                }
            }
        });

        //OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        IMapController mapController = map.getController();

        drawRouteAndStart();
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == false) {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_all.php");
                } else {
                    getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_open.php");
                }
            }
        });
        if (sw.isChecked() == false) {
            getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_all.php");
        } else {
            getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_open.php");
        }

        map.setMultiTouchControls(true);
        mapController.setZoom(17.0);
        GeoPoint startPoint;
        if (BackgroundService.getLatitude() == 0 || BackgroundService.getInitLat() == null) {
            if (sw.isChecked() == false) {
                getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_all.php");
            } else {
                getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_open.php");
            }
        }
        if (BackgroundService.getLatitude() != 0) {
            startPoint = new GeoPoint(Double.valueOf(BackgroundService.getLatitude()), Double.valueOf(BackgroundService.getLongitude()));
        } else if (BackgroundService.getInitLat() != "0") {
            startPoint = new GeoPoint(Double.valueOf(BackgroundService.getInitLat()), Double.valueOf(BackgroundService.getInitLong()));
        } else {
            startPoint = new GeoPoint(47.935900, 20.367770);
        }
        mapController.setCenter(startPoint);

        dl = findViewById(R.id.drawler4);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = findViewById(R.id.nv4);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.main:
                        dl.closeDrawers();
                        Intent i0 = new Intent(NearbyActivity.this, MainActivity.class);
                        startActivity(i0);
                        return true;
                    case R.id.map:
                        dl.closeDrawers();
                        Intent i = new Intent(NearbyActivity.this, MapActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.list:
                        dl.closeDrawers();
                        Intent i2 = new Intent(NearbyActivity.this, ListActivity.class);
                        startActivity(i2);
                        return true;
                    case R.id.nearby:
                        dl.closeDrawers();
                        return true;
                    case R.id.more:
                        dl.closeDrawers();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sont.sytes.net/moreapps"));
                        startActivity(browserIntent);
                        return true;
                    default:
                        dl.closeDrawers();
                        return true;
                }
            }
        });
        NavigationView navigationView = findViewById(R.id.nv4);
        View hView = navigationView.getHeaderView(0);
        TextView tex = hView.findViewById(R.id.header_verinfo);
        String version = "Version: " + BuildConfig.VERSION_NAME + " Build: " + BuildConfig.VERSION_CODE;
        tex.setText(version);


    }

    public void drawRouteAndStart() {
        IMapController mapController = map.getController();
        LocationManager mService;
        mService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mService.addGpsStatusListener(this);

        GeoPoint startPoint;

        line = new Polyline();
        geoPoints = new ArrayList<>();
        overlayItemArray = new ArrayList<OverlayItem>();

        map.setMultiTouchControls(true);
        mapController.setZoom(18.0);

        if (BackgroundService.getLatitude() != 0) {
            startPoint = new GeoPoint(Double.valueOf(BackgroundService.getLatitude()), Double.valueOf(BackgroundService.getLongitude()));
        } else {
            startPoint = new GeoPoint(47.935900, 20.367770);
        }
        mapController.setCenter(startPoint);

        updateMap(map);
        drawPoint(map);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateMap(map);
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    public void drawPoint(MapView map) {
        map.getOverlays().clear();
        map.invalidate();
        Drawable pin = getResources().getDrawable(R.drawable.wifi5);
        GeoPoint geo;

        if (BackgroundService.getLatitude() != 0 || BackgroundService.getLatitude() != 0) {
            geo = new GeoPoint(Double.valueOf(BackgroundService.getLatitude()), Double.valueOf(BackgroundService.getLongitude()));
        } else {
            geo = new GeoPoint(47.935900, 20.367770);
        }
        Marker m = new Marker(map);
        m.setTitle("Start Point");
        m.setSubDescription("The location where you started");
        m.setIcon(resize(pin, 100));
        m.setPosition(geo);
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(getApplicationContext(), "Marker count: " + mapView.getOverlays().size(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        map.getOverlays().add(m);
        map.invalidate();
    }

    // Draw line when moving around
    private void updateMap(MapView map) {
        GeoPoint geo;
        if (BackgroundService.getLatitude() != 0) {
            geo = new GeoPoint(Double.valueOf(BackgroundService.getLatitude()), Double.valueOf(BackgroundService.getLongitude()));
        } else {
            geo = new GeoPoint(47.935900, 20.367770);
        }
        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(this, overlayItemArray, null);
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Log.d("MARKER_LINE_", "Clicked: " + polyline.getDistance());
                return false;
            }
        });

        if (geoPoints.contains(geo) != true) {
            geoPoints.add(geo);
            map.getOverlays().add(itemizedIconOverlay);
            //line.setColor(Color.argb(90,240,128,128));
            line.setColor(Color.argb(90, 0, 173, 181));
            line.setWidth(20.0f);
            line.getPaint().setStrokeJoin(Paint.Join.ROUND);
            try {
                line.setPoints(geoPoints);
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.getOverlayManager().add(line);
        }
        map.invalidate();

    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
            // backgroundService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder mLocalBinder = (BackgroundService.LocalBinder) service;
            backgroundService = mLocalBinder.getServerInstance();
        }
    };

    private Drawable resize(Drawable image, Integer size) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, size, size, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    protected void drawCircle(MapView map) {
        Polygon oPolygon = new Circlee(map);
        final double radius = 150;
        Double lat = null;
        Double lon = null;
        try {
            lat = Double.valueOf(BackgroundService.getLatitude());
            lon = Double.valueOf(BackgroundService.getLongitude());
        } catch (Exception e) {
            lat = 47.935902;
            lon = 20.367769;
        }
        ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>();
        GeoPoint p = new GeoPoint(lat, lon);
        circlePoints.add(p);
        for (float f = 0; f < 360; f++) {
            circlePoints.add(new GeoPoint(lat, lon).destinationPoint(radius, f));
        }
        oPolygon.setPoints(circlePoints);
        oPolygon.setStrokeWidth(20.0f);
        final InfoWindow pop = new PopUpWin(R.layout.popup, map);
        oPolygon.setTitle("karika title");
        oPolygon.setSubDescription("karika subdest");
        oPolygon.setInfoWindow(pop);
        oPolygon.setFillColor(Color.argb(60, 233, 150, 122));
        oPolygon.setStrokeColor(Color.argb(85, 255, 0, 255));
        oPolygon.setOnClickListener(new Polygon.OnClickListener() {
            @Override
            public boolean onClick(Polygon polygon, MapView mapView, GeoPoint eventPos) {
                return false;
            }
        });
        map.getOverlays().add(oPolygon);

    }

    protected void drawMarkers(final MapView map, Map<Location, ApStrings> loc_ssid) {
        Thread thread = new Thread() {
            public void run() {
                final RadiusMarkerClusterer clusterer = new CustomCluster(getApplicationContext());
                final List<Overlay> overlays = map.getOverlays();
                MapController mapController = (MapController) map.getController();

                overlays.clear();

                Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.wifi4_cluster_25);

                clusterer.setIcon(icon);
                clusterer.setRadius(85);
                clusterer.mTextAnchorU = 0.70f;
                clusterer.mTextAnchorV = 0.27f;
                clusterer.getTextPaint().setTextSize(20.0f);
                clusterer.getTextPaint().setColor(Color.LTGRAY);

                map.getOverlays().clear();
                map.invalidate();
                //int counter = 0;

                int height = 70;
                int width = 70;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mappinicon_min);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                Drawable d = new BitmapDrawable(getResources(), smallMarker);

                InfoWindow pop = new PopUpWin(R.layout.popup, map);

                for (Map.Entry<Location, ApStrings> entry : loc_ssid.entrySet()) {
                    Location coords = entry.getKey();

                    String time = entry.getValue().getTime();
                    String ssid = entry.getValue().getSsid();
                    String bssid = entry.getValue().getMac();
                    String source = entry.getValue().getSource();

                    String description = "Time: " + time + "\n" + "MAC: " + bssid;
                    String snippet = "Source: " + source;

                    GeoPoint geo = new GeoPoint(coords.getLatitude(), coords.getLongitude());
                    // CHECK DISTANCE FROM CURRENT TO COORDS
                    CustomMarker m = new CustomMarker(map);
                    m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            final int[] counter_ = {0};
                            Timer t = new Timer();
                            t.scheduleAtFixedRate(new TimerTask() {
                                                      @Override
                                                      public void run() {
                                                          Log.d("ALPHA", String.valueOf(marker.getAlpha()));
                                                          if (marker.getAlpha() >= 255) {
                                                              marker.setAlpha(marker.getAlpha() - 0.005f);
                                                          } else if (marker.getAlpha() <= 1) {
                                                              marker.setAlpha(marker.getAlpha() + 0.005f);
                                                          }
                                                          map.invalidate();
                                                          counter_[0]++;
                                                      }
                                                  },
                                    //Set how long before to start calling the TimerTask (in milliseconds)
                                    100,
                                    //Set the amount of time between each execution (in milliseconds)
                                    100);

                            if (marker.isInfoWindowShown()) {
                                marker.closeInfoWindow();
                            } else {
                                marker.showInfoWindow();
                            }
                            return true;
                        }
                    });
                    m.setTitle(ssid);
                    m.setSnippet(snippet);
                    m.setSubDescription(description);
                    m.setInfoWindow(pop);
                    m.setIcon(d); // pin
                    m.setPosition(geo);
                    clusterer.add(m);
                    //counter++;
                }

                overlays.add(clusterer);
                if (map.getMaxZoomLevel() <= 17) {
                    mapController.setZoom(18);
                }
                map.invalidate();
                NearbyActivity.loc_ssid2.clear();
            }
        };
        thread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void getList(final Context context, String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                content = new String(response, StandardCharsets.UTF_8);
                String stripped = html2text(content);
                String[] lines = stripped.split("\\r?\\n");
                int lineCount = 0;
                for (String s : lines) {
                    lineCount++;
                    String[] splittedStr = s.split("OVER");
                    String source;
                    String recordTime = null;
                    String ssid = null;
                    String bssid = null;
                    String str = null;
                    try {
                        recordTime = splittedStr[1];
                        ssid = splittedStr[2];
                        bssid = splittedStr[3];
                        str = splittedStr[4];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        source = splittedStr[8];
                    } catch (Exception e) {
                        source = "No data";
                    }
                    try {
                        double longi = ParseDouble(splittedStr[6]);
                        double lati = ParseDouble(splittedStr[7]);
                        Location x = new Location("");
                        x.setLatitude(lati);
                        x.setLongitude(longi);
                        ApStrings desc = new ApStrings(recordTime, ssid, bssid, str, source);
                        // check if < 500 meters
                        if (SontHelper.getDistance(lati, BackgroundService.getLatitude(), longi, BackgroundService.getLongitude()) <= NEARBY_MIN_DISTANCE) {
                            loc_ssid2.put(x, desc);
                            Log.d("DISTANCE_MARK_", " > " + SontHelper.getDistance(lati, BackgroundService.getLatitude(), longi, BackgroundService.getLongitude()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(), "Found: " + lineCount, Toast.LENGTH_SHORT).show();
                drawCircle(map);
                drawMarkers(map, loc_ssid2);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                getList(getBaseContext(), "https://sont.sytes.net/wifilocator/wifis_nearby_all.php");
                Toast.makeText(getApplicationContext(), "Retrying", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                Toast.makeText(getApplicationContext(), "Retrying", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String html2text(String html) {
        return android.text.Html.fromHtml(html).toString();
    }

    static double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;
            }
        } else return 0;
    }

    public Context getContext() {
        return getApplicationContext();
    }

    public void onGpsStatusChanged(int event) {
        //mStatus = mService.getGpsStatus(mStatus);
        if (event != GpsStatus.GPS_EVENT_FIRST_FIX &&
                event != GpsStatus.GPS_EVENT_SATELLITE_STATUS &&
                event != GpsStatus.GPS_EVENT_STARTED &&
                event != GpsStatus.GPS_EVENT_STOPPED) {
            Toast.makeText(getBaseContext(), "GPS Unknown event: " + event, Toast.LENGTH_SHORT).show();
        }
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                Toast.makeText(getBaseContext(), "GPS Event Started", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                Toast.makeText(getBaseContext(), "GPS Event Stopped", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Toast.makeText(getBaseContext(), "GPS Event First FIX", Toast.LENGTH_SHORT).show();
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //Toast.makeText(getBaseContext(), "GPS SAT Status", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

class Circlee extends Polygon {
    private MapView map;

    public Circlee(MapView map) {
        this.map = map;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e, MapView mapView) {
        /*if (e.getAction() == MotionEvent.ACTION_UP && contains(e)) {
            return true;
        }
        return super.onSingleTapUp(e, mapView);*/
        return false;
    }

    @Override
    public boolean onLongPress(MotionEvent e, MapView mapView) {
        Log.d("TAPI", "LONGTAPTAPTAPTAPTAP_" + e.toString());
        return super.onLongPress(e, mapView);
    }
}

class PopUpWin extends InfoWindow {
    private int layoutID;
    private MapView map;

    public PopUpWin(int layoutResId, MapView map) {
        super(layoutResId, map);
        this.layoutID = layoutID;
        this.map = map;
    }

    @Override
    public void onOpen(Object item) {
        InfoWindow.closeAllInfoWindowsOn(map);
        PopUpWin.closeAllInfoWindowsOn(map);
        String title;
        String desc;
        String snip;

        LinearLayout layout = mView.findViewById(R.id.plinlay);
        Button btn = mView.findViewById(R.id.pbtn);
        TextView ssid = mView.findViewById(R.id.pssid);
        TextView descr = mView.findViewById(R.id.pdesc);
        TextView psnip = mView.findViewById(R.id.psnip);
        ImageView img = mView.findViewById(R.id.pimg);
        TextView pstat = mView.findViewById(R.id.pstat);

        if (item instanceof Marker) {
            final Marker marker = (Marker) item;

            title = marker.getTitle();
            desc = marker.getSubDescription();
            snip = marker.getSnippet();

            String android_id = Settings.Secure.getString(map.getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String source_;
            if (snip.contains(android_id)) {
                String[] a = snip.split("_");
                source_ = "Source: YOU_" + a[1];
                psnip.setText(source_);
            } else {
                psnip.setText(snip);
            }
            ssid.setText("SSID: " + title);
            descr.setText(desc);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CLICKED BUTTON", "title: " + marker.getTitle());
                    Log.d("CLICKED BUTTON", "desc: " + marker.getSubDescription());

                    marker.remove(map);
                    marker.closeInfoWindow();
                    map.invalidate();
                }
            });
        }
    }

    @Override
    public void onClose() {

    }
}

class CustomCluster extends RadiusMarkerClusterer {

    private Context ctx;
    private static int counter;
    public BackgroundService backgroundService;

    public CustomCluster(Context ctx) {
        super(ctx);
        this.ctx = ctx;
        counter++;
        ServiceConnection mConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                //Toast.makeText(getApplicationContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
                // backgroundService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                BackgroundService.LocalBinder mLocalBinder = (BackgroundService.LocalBinder) service;
                backgroundService = mLocalBinder.getServerInstance();
            }
        };
    }

    public void animateMarkerDropping(final Marker marker, MapView map) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 500;

        final Interpolator interpolator = new AccelerateInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                final long elapsed = SystemClock.uptimeMillis() - start;
                final float t = Math.max(1 -
                        interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAlpha(marker.getAlpha() - 0.5f);
                //marker.setAnchor(0.5f, 1f + 14 * t);
                map.invalidate();

                if (t > 0f) {
                    handler.postDelayed(this, 150);
                }
            }
        });
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e, MapView mapView) {

        Projection proj = mapView.getProjection();
        GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
        String longitude = Double.toString(((double) loc.getLongitudeE6()) / 1000000);
        String latitude = Double.toString(((double) loc.getLatitudeE6()) / 1000000);
        String asd = "0";
        if (Double.valueOf(BackgroundService.getLatitude()) != 0) {
            asd = String.valueOf(round(getDistance(Double.valueOf(latitude), Double.valueOf(BackgroundService.getLatitude()), Double.valueOf(longitude), Double.valueOf(BackgroundService.getLongitude())), 1));
            Log.d("TAPI1: ", asd);
        } else if (Double.valueOf(BackgroundService.getInitLat()) != 0) {
            asd = String.valueOf(round(getDistance(Double.valueOf(latitude), Double.valueOf(BackgroundService.getInitLat()), Double.valueOf(longitude), Double.valueOf(BackgroundService.getInitLong())), 1));
            Log.d("TAPI2: ", asd);
        } else {
            asd = String.valueOf(round(getDistance(Double.valueOf(latitude), Double.valueOf(47.935900), Double.valueOf(longitude), Double.valueOf(20.367770)), 1));
            Log.d("TAPI3: ", asd);
        }
        Log.d("TAPI4: ", String.valueOf(Double.valueOf(BackgroundService.getLatitude())));

        Toast.makeText(ctx, asd + " meters", Toast.LENGTH_SHORT).show();
        return super.onSingleTapUp(e, mapView);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static double getDistance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double el1 = 0;
        double el2 = 0;
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    @Override
    public Marker buildClusterMarker(StaticCluster cluster, MapView mapView) {
        Marker m = new Marker(mapView);
        m.setPosition(cluster.getPosition());
        m.setInfoWindow(null);
        m.setAnchor(mAnchorU, mAnchorV);

        Bitmap finalIcon = Bitmap.createBitmap(mClusterIcon.getWidth(), mClusterIcon.getHeight(), mClusterIcon.getConfig());
        Canvas iconCanvas = new Canvas(finalIcon);
        iconCanvas.drawBitmap(mClusterIcon, 0, 0, null);
        String text = "" + cluster.getSize();
        int textHeight = (int) (mTextPaint.descent() + mTextPaint.ascent());
        iconCanvas.drawText(text,
                mTextAnchorU * finalIcon.getWidth(),
                mTextAnchorV * finalIcon.getHeight() - textHeight / 2,
                mTextPaint);
        m.setIcon(new BitmapDrawable(mapView.getContext().getResources(), finalIcon));
        //beggining of modification
        List<Marker> markersInCluster = new ArrayList<Marker>();
        for (int i = 0; i < cluster.getSize(); i++) {
            markersInCluster.add(cluster.getItem(i));
        }
        m.setRelatedObject(markersInCluster);
        //end of modification

        return m;
    }


}

class CustomMarker extends Marker {

    public CustomMarker(MapView mapView) {
        super(mapView);
    }

    public CustomMarker(MapView mapView, Context resourceProxy) {
        super(mapView, resourceProxy);
    }


}
