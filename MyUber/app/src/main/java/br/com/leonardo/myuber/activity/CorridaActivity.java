package br.com.leonardo.myuber.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;

import br.com.leonardo.myuber.R;
import br.com.leonardo.myuber.config.ConfiguracaoFirebase;
import br.com.leonardo.myuber.model.Requisicao;
import br.com.leonardo.myuber.model.Usuario;

public class CorridaActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private Button buttonAceitarCorrida;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localMotorista;
    private Usuario motorista;
    private String idRequisicao;
    private Requisicao requisicao;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corrida);

        inicializarComponentes();

        //Recupera dados do usuário
        if( getIntent().getExtras().containsKey("idRequisicao")
                && getIntent().getExtras().containsKey("motorista") ){
            Bundle extras = getIntent().getExtras();
            motorista = (Usuario) extras.getSerializable("motorista");
            idRequisicao = extras.getString("idRequisicao");
            verificaStatusRequisicao();
        }

    }

    private void verificaStatusRequisicao(){

        DatabaseReference requisicoes = firebaseRef.child("requisicoes")
                .child( idRequisicao );
        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Recupera requisição
                requisicao = dataSnapshot.getValue(Requisicao.class);

                switch ( requisicao.getStatus() ){
                    case Requisicao.STATUS_AGUARDANDO :
                        requisicaoAguardando();
                        break;
                    case Requisicao.STATUS_A_CAMINHO :
                        requisicaoACaminho();
                        break;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void requisicaoAguardando(){
        buttonAceitarCorrida.setText("Aceitar corrida");
    }

    private void requisicaoACaminho(){
        buttonAceitarCorrida.setText("A caminho do passageiro");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar localizacao do usuário
        recuperarLocalizacaoUsuario();

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localMotorista = new LatLng(latitude, longitude);

                mMap.clear();
                mMap.addMarker(
                        new MarkerOptions()
                                .position(localMotorista)
                                .title("Meu Local")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro))
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(localMotorista, 20)
                );

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Solicitar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }


    }

    public void aceitarCorrida(View view){

        //Configura requisicao
        requisicao = new Requisicao();
        requisicao.setId( idRequisicao );
        requisicao.setMotorista( motorista );
        requisicao.setStatus( Requisicao.STATUS_A_CAMINHO );

        requisicao.atualizar();

    }

    private void inicializarComponentes(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Iniciar corrida");

        buttonAceitarCorrida = findViewById(R.id.buttonAceitarCorrida);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


}

//    private Button buttonAceitarCorrida;
//    private GoogleMap mMap;
//    private LocationManager locationManager;
//    private LocationListener locationListener;
//    private LatLng localMotorista;
//    private Usuario motorista;
//    private String idRequisicao;
//    private Requisicao requisicao;
//    private DatabaseReference firebaseRef;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_corrida);
//
//        inicializarComponentes();
//
//        //Recuperar dados usuario
//        if (getIntent().getExtras().containsKey("idRequisicao") && getIntent().getExtras().containsKey("motorisra")){
//
//            Bundle extras = getIntent().getExtras();
//            motorista = (Usuario) extras.getSerializable("motorista");
//            idRequisicao = extras.getString("idRequisicao");
//            verificarStatusRequisicao();
//
//        }
//
//    }
//
//    private void verificarStatusRequisicao(){
//
//        DatabaseReference requisicoes = firebaseRef.child("requisicoes").child(idRequisicao);
//        requisicoes.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                //Recuperar requisição
//                requisicao = snapshot.getValue(Requisicao.class);
//                switch (requisicao.getStatus()){
//                    case Requisicao.STATUS_AGUARDANDO : requisicaoAguardando();
//                         break;
//                    case Requisicao.STATUS_A_CAMINHO : requisicaoACaminho();
//                        break;
//                }
//
//
//            }
//
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void requisicaoAguardando(){
//
//        buttonAceitarCorrida.setText("Aceitar corrida");
//    }
//
//    private void requisicaoACaminho(){
//        buttonAceitarCorrida.setText("A caminho do  passageiro");
//
//    }
//
//
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        //Recuperar localizacao do usuario
//        recuperarLocalizacaoUsuario();
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
//
//    private void recuperarLocalizacaoUsuario() {
//
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        locationListener = location -> {
//
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//            localMotorista = new LatLng(latitude, longitude);
//
//            mMap.clear();
//            mMap.addMarker(
//                    new MarkerOptions()
//                            .position(localMotorista)
//                            .title("Meu local")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro))
//            );
//
//            mMap.moveCamera(
//                    CameraUpdateFactory.newLatLngZoom(localMotorista, 15)
//            );
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
//        }
//
//    }
//
//    public void aceitarCorrida(View view){
//        //Configura requisicao
//        requisicao = new Requisicao();
//        requisicao.setId(idRequisicao);
//        requisicao.setMotorista(motorista);
//        requisicao.setStatus(Requisicao.STATUS_A_CAMINHO);
//        requisicao.atualizar();
//
//    }
//
//    private void inicializarComponentes(){
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        setTitle("Iniciar corrida");
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        buttonAceitarCorrida = findViewById(R.id.buttonAceitarCorrida);
//
//        //Configurações iniciais
//        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
//
//        //obtem a localização no mapa
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//}