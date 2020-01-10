package es.upv.gnd.letslock.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import es.upv.gnd.letslock.R;
import es.upv.gnd.letslock.bbdd.Casa;
import es.upv.gnd.letslock.bbdd.Casas;
import es.upv.gnd.letslock.bbdd.CasasCallback;


public class MapaFragment extends Fragment {

    private static final int SOLICITUD_PERMISO_FINE_LOCATION = 0;

    View vista;
    MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private LatLng houseLocation;
    private LatLng myLocation;
    private Bitmap bitmap;
    private BitmapDescriptor descriptor;
    public Marker houseMarker;
    public MarkerOptions placeMarker;
    public Polyline currentPolyline;
    JSONObject jso;
    public String duration;
    public TextView txtDuration;
    public Button btnWalking;
    public Button btnDriving;
    public String mode = "driving";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        final Casas casaBD = new Casas();

        vista = inflater.inflate(R.layout.fragment_mapa, container, false);
        mapView = (MapView) vista.findViewById(R.id.mapa);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();
        txtDuration = vista.findViewById(R.id.txtDuration);
        txtDuration.setVisibility(View.INVISIBLE);
        btnWalking = vista.findViewById(R.id.btnWalking);
        btnDriving = vista.findViewById(R.id.btnDriving);
        btnWalking.setVisibility(View.INVISIBLE);
        btnDriving.setVisibility(View.INVISIBLE);

        bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.house_icon);
        descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                Log.e("PERMISO", " " + ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION));

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                } else {
                    solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION, "Sin el permiso" +
                                    " no puedo obtener la localización actual.",
                            SOLICITUD_PERMISO_FINE_LOCATION, getActivity());
                }
                getUserLastLocation();

            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        db = FirebaseFirestore.getInstance();


        final Button btnDirCasa = vista.findViewById(R.id.btnDirCasa);
        final FloatingActionsMenu menuBotones = (FloatingActionsMenu) vista.findViewById(R.id.btnFloatingMenu);
        final LinearLayout greyBg = vista.findViewById(R.id.greybg);
        menuBotones.setScaleX(0);
        menuBotones.setScaleY(0);

        //REGISTRAR CASA
        btnDirCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle("Registrar mi casa")
                        .setMessage("¿Desea guardar su ubicación actual como la localización de su casa?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {

                                        houseLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                        casaBD.setCasa(usuario.getUid(), getContext(), houseLocation);
                                        menuBotones.setVisibility(View.VISIBLE);
                                        menuBotones.setEnabled(true);
                                        btnDirCasa.setEnabled(false);
                                        btnDirCasa.setVisibility(View.INVISIBLE);
                                        greyBg.setVisibility(View.INVISIBLE);
                                        setMarkerHouse();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                        .show();

            }
        });

        // FloatingActionsMenu

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(vista.getContext(), android.R.interpolator.fast_out_slow_in);

            menuBotones.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(interpolador)
                    .setDuration(600)
                    .setStartDelay(500)
            ;
        }
        final FloatingActionButton fabBorrar = vista.findViewById(R.id.btnFabBorrar);
        final FloatingActionButton fabRuta = vista.findViewById(R.id.btnFabRuta);
        final FloatingActionButton fabCasa = vista.findViewById(R.id.btnFabCasa);

        fabBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng ceroCero = new LatLng(0, 0);
                casaBD.setCasa(usuario.getUid(), getContext(), ceroCero);
                btnDirCasa.setEnabled(true);
                btnDirCasa.setVisibility(View.VISIBLE);
                menuBotones.setVisibility(View.INVISIBLE);
                menuBotones.setEnabled(false);
                greyBg.setVisibility(View.VISIBLE);

                houseMarker.remove();

            }
        });
        fabRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevaRuta(googleMap);
            }
        });
        fabCasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(houseLocation).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        menuBotones.setVisibility(View.VISIBLE);
        menuBotones.setEnabled(true);
        btnDirCasa.setEnabled(false);
        btnDirCasa.setVisibility(View.INVISIBLE);
        greyBg.setVisibility(View.INVISIBLE);


        casaBD.getCasa(getContext(), new CasasCallback() {
            @Override
            public void getCasasCallback(Casa casa) {
                LatLng ceroCero = new LatLng(0.0, 0.0);
                Log.e("Objeto", " " + casa.toString());
                if (casa.getLocalizacion().latitude == ceroCero.latitude && casa.getLocalizacion().longitude == ceroCero.longitude) { // latitud y longitud
                    btnDirCasa.setEnabled(true);
                    btnDirCasa.setVisibility(View.VISIBLE);
                    greyBg.setVisibility(View.VISIBLE);

                    menuBotones.setVisibility(View.INVISIBLE);
                    menuBotones.setEnabled(false);
                } else {
                    houseLocation = casa.getLocalizacion();
                    setMarkerHouse();

                }
            }
        });

        return vista;
    }


    public void solicitarPermiso(final String permiso, String justificacion, final int requestCode, final Activity actividad) {

        new AlertDialog.Builder(actividad).setTitle("Solicitud de MartaG")
                .setMessage(justificacion)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        requestPermissions(new String[]{permiso}, requestCode);

                    }
                }).show();

    }

    public void getUserLastLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                placeMarker = new MarkerOptions().position(myLocation).title("Marker Title").snippet("Marker Description");

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });
    }

    public void getLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // For showing a move to my location button
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SOLICITUD_PERMISO_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLastLocation();


            } else {
                Toast.makeText(getActivity(), "Sin el permiso, no puedo realizar la " + "acción", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void setMarkerHouse() {
        if (houseLocation != null) {
            houseMarker = googleMap.addMarker(new MarkerOptions().position(houseLocation).title("Mi casa").icon(descriptor));

        }

    }


    public void nuevaRuta(GoogleMap googleMap) {
        btnWalking.setVisibility(View.VISIBLE);
        btnDriving.setVisibility(View.VISIBLE);
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                getLocation();
                Log.e("House", "" + houseLocation);
                Log.e("MyLoc", "" + myLocation);
                Log.e("mode", mode);
                modeRoute();
                String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + myLocation.latitude + "," + myLocation.longitude + "&destination=" + houseLocation.latitude + "," + houseLocation.longitude + "&mode=" + mode + "&key=AIzaSyCN8QGPO7D9WhmfUeBVByECdmnruehiKDE";
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            jso = new JSONObject(response);
                            trazarRuta(jso);
                            Log.i("Ruta: ", "" + response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(stringRequest);

            }
        });
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }


    private void trazarRuta(JSONObject jso) {
        resetMap();

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        String time = "";

        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) (jRoutes.get(i))).getJSONArray("legs");

                for (int j = 0; j < jLegs.length(); j++) {

                    time = ((JSONObject) jLegs.get(j)).getJSONObject("duration").getString("text");
                    duration = time;
                    txtDuration.setVisibility(View.VISIBLE);
                    txtDuration.setText("Tiempo estimado para llegar a casa: " + duration);
                    Log.e("duracion", duration);

                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "" + ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end", "" + polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        currentPolyline = googleMap.addPolyline(new PolylineOptions().addAll(list).color(Color.BLUE).width(5));

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void resetMap() {
        googleMap.clear();
        setMarkerHouse();
    }

    public void modeRoute() {

        btnWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "walking";
            }
        });
        btnDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "driving";
            }
        });

    }


}
