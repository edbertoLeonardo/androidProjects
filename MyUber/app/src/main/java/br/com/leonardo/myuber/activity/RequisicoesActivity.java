package br.com.leonardo.myuber.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.leonardo.myuber.R;
import br.com.leonardo.myuber.adapter.RequisicoesAdapter;
import br.com.leonardo.myuber.config.ConfiguracaoFirebase;
import br.com.leonardo.myuber.helper.RecyclerItemClickListener;
import br.com.leonardo.myuber.helper.UsuarioFirebase;
import br.com.leonardo.myuber.model.Requisicao;
import br.com.leonardo.myuber.model.Usuario;

public class RequisicoesActivity extends AppCompatActivity {

    private RecyclerView recyclerRequisicoes;
    private TextView textResultado;

    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private RequisicoesAdapter adapter;
    private Usuario motorista;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        inicializarComponentes();

        //Recuperar localizacao do usu??rio
        recuperarLocalizacaoUsuario();

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                if( !latitude.isEmpty() && !longitude.isEmpty() ){
                    motorista.setLatitude(latitude);
                    motorista.setLongitude(longitude);
                    locationManager.removeUpdates(locationListener);
                    adapter.notifyDataSetChanged();
                }

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

        //Solicitar atualiza????es de localiza????o
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                autenticacao.signOut();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){

        getSupportActionBar().setTitle("Requisi????es");

        //Configura componentes
        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
        textResultado = findViewById(R.id.textResultado);

        //Configura????es iniciais
        motorista = UsuarioFirebase.getDadosUsuarioLogado();
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //Configurar RecyclerView
        adapter = new RequisicoesAdapter(listaRequisicoes, getApplicationContext(), motorista );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRequisicoes.setLayoutManager( layoutManager );
        recyclerRequisicoes.setHasFixedSize(true);
        recyclerRequisicoes.setAdapter( adapter );

        //Adiciona evento de clique no recycler
        recyclerRequisicoes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerRequisicoes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Requisicao requisicao = listaRequisicoes.get(position);
                                Intent i = new Intent(RequisicoesActivity.this, CorridaActivity.class );
                                i.putExtra("idRequisicao", requisicao.getId() );
                                i.putExtra("motorista", motorista );
                                startActivity( i );
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        recuperarRequisicoes();

    }

    private void recuperarRequisicoes(){

        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        Query requisicaoPesquisa = requisicoes.orderByChild("status")
                .equalTo(Requisicao.STATUS_AGUARDANDO);

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot.getChildrenCount() > 0 ){
                    textResultado.setVisibility( View.GONE );
                    recyclerRequisicoes.setVisibility( View.VISIBLE );
                }else {
                    textResultado.setVisibility( View.VISIBLE );
                    recyclerRequisicoes.setVisibility( View.GONE );
                }

                listaRequisicoes.clear();
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    Requisicao requisicao = ds.getValue( Requisicao.class );
                    listaRequisicoes.add( requisicao );
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
//    private TextView textResultado;
//    private RecyclerView recyclerRequisicoes;
//
//    private FirebaseAuth autenticacao;
//    private DatabaseReference firebaseRef;
//    private List<Requisicao> listaRequisicaos = new ArrayList<>();
//    private RequisicoesAdapter adapter;
//    private Usuario motorista;
//
//    private LocationManager locationManager;
//    private LocationListener locationListener;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_requisicoes);
//
//        inicalizarComponentes();
//
//        //Recuperear localizacao usuario
//        recuperarLocalizacaoUsuario();
//
//    }
//
//    private void recuperarLocalizacaoUsuario() {
//
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        locationListener = location -> {
//
//            String latitude = String.valueOf(location.getLatitude());
//            String longitude = String.valueOf( location.getLongitude());
//
//            if (!latitude.isEmpty() && !longitude.isEmpty()){
//                motorista.setLatitude(latitude);
//                motorista.setLongitude(longitude);
//                locationManager.removeUpdates(locationListener);
//                adapter.notifyDataSetChanged();
//            }
//
//
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//        }
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menuSair :
//                autenticacao.signOut();
//                finish();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void inicalizarComponentes(){
//
//        getSupportActionBar().setTitle("Requisi????es");
//
//        textResultado = findViewById(R.id.textResultado);
//        recyclerRequisicoes = findViewById(R.id.recyclerRequisicoes);
//
//        //Configura????es iniciais
//        motorista = UsuarioFirebase.getDadosUsuarioLogado();
//        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
//        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
//
//        //Configurar recyclerView
//        adapter = new RequisicoesAdapter(listaRequisicaos, getApplicationContext(), motorista);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerRequisicoes.setLayoutManager(layoutManager);
//        recyclerRequisicoes.setHasFixedSize(true);
//        recyclerRequisicoes.setAdapter(adapter);
//
//        recyclerRequisicoes.addOnItemTouchListener(new RecyclerItemClickListener(
//                getApplicationContext(), recyclerRequisicoes, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Requisicao requisicao = listaRequisicaos.get(position);
//                Intent i = new Intent(RequisicoesActivity.this, CorridaActivity.class);
//                i.putExtra("idRequisicao", requisicao.getId());
//                i.putExtra("motorista", motorista);
//                startActivity(i);
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        }
//        ));
//
//        recuperarRequisicoes();
//
//    }
//
//    private  void recuperarRequisicoes(){
//
//        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
//        Query requisicaoPesquisa =  requisicoes.orderByChild("status").equalTo(Requisicao.STATUS_AGUARDANDO);
//        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.getChildrenCount() > 0){
//                    textResultado.setVisibility(View.GONE);
//                    recyclerRequisicoes.setVisibility(View.VISIBLE);
//                }else {
//                    textResultado.setVisibility(View.VISIBLE);
//                    recyclerRequisicoes.setVisibility(View.GONE);
//                }
//
//                listaRequisicaos.clear();
//                for(DataSnapshot ds : snapshot.getChildren()){
//                    Requisicao requisicao = ds.getValue(Requisicao.class);
//                    listaRequisicaos.add(requisicao);
//                }
//
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//}