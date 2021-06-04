package br.com.leonardo.organizze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.leonardo.organizze.R;
import br.com.leonardo.organizze.adapter.AdapterMovimentacao;
import br.com.leonardo.organizze.config.ConfiguracaoFirebase;
import br.com.leonardo.organizze.helper.Base64Custom;
import br.com.leonardo.organizze.model.Movimentacao;
import br.com.leonardo.organizze.model.Usuario;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView textoSaudacao, textoSaldo;
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoUsuario = 0.0;

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private DatabaseReference movimentacaoRef;
    private String mesAnoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Organizze");
        setSupportActionBar(toolbar);

        textoSaldo = findViewById(R.id.textSaldo);
        textoSaudacao = findViewById(R.id.textSaudacao);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recycleMovimentos);
        configuraCalendarView();

        //Configurar adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes,this);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( adapterMovimentacao );


    }

    public void recuperarMovimentacoes(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("movimentacao")
                .child( idUsuario )
                .child( mesAnoSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                movimentacoes.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren() ){

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    movimentacoes.add( movimentacao );

                }

                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarResumo(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        usuarioRef = firebaseRef.child("usuarios").child( idUsuario );

        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue( Usuario.class );

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format( resumoUsuario );

                textoSaudacao.setText("Olá, " + usuario.getNome() );
                textoSaldo.setText( "R$ " + resultadoFormatado );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair :
                autenticacao.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void configuraCalendarView(){

        CharSequence meses[] = {"Janeiro","Fevereiro", "Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        calendarView.setTitleMonths( meses );

        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1) );
        mesAnoSelecionado = String.valueOf( mesSelecionado + "" + dataAtual.getYear() );

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1) );
                mesAnoSelecionado = String.valueOf( mesSelecionado + "" + date.getYear() );

                movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
                recuperarMovimentacoes();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener( valueEventListenerUsuario );
        movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
    }
}


//public class PrincipalActivity extends AppCompatActivity {
//
//    private MaterialCalendarView calendarView;
//    private TextView textoSaudacao, textoSaldo;
//    private Double despesaTotal = 0.0;
//    private Double receitaTotal = 0.0;
//    private Double resumoUsuario = 0.0;
//    private DatabaseReference usuarioRef;
//    private ValueEventListener valueEventListenerUsuario;
//    private ValueEventListener valueEventListenerMovimentacoes;
//    private RecyclerView recyclerView;
//    private AdapterMovimentacao adapterMovimentacao;
//    private List<Movimentacao> movimentacoes = new ArrayList<>();
//    private DatabaseReference movimentacaoRef;
//    private String mesAnoSelecionado;
//
//    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
//    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_principal);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Organizze");
//        setSupportActionBar(toolbar);
//
//        textoSaldo = findViewById(R.id.textSaldo);
//        textoSaudacao = findViewById(R.id.textSaudacao);
//        calendarView = findViewById(R.id.calendarView);
//        recyclerView = findViewById(R.id.recycleMovimentos);
//        configuraCalendarView();
//
//        //Configurar adapter
//        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);
//
//        //Configurar recycleView
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapterMovimentacao);
//
//    }
//
//    public void recuperarMovimentacoes(){
//
//        String emailUsuario = autenticacao.getCurrentUser().getEmail();
//        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
//
//        movimentacaoRef = firebaseRef.child("movimentacao").child(idUsuario).child(mesAnoSelecionado);
//        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                movimentacoes.clear();
//
//                for (DataSnapshot dados: dataSnapshot.getChildren()){
//                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
//                    movimentacoes.add(movimentacao);
//                }
//
//                adapterMovimentacao.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    public void recuperarResumo(){
//
//        String emailUsuario = autenticacao.getCurrentUser().getEmail();
//        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
//        usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
//
//        Log.i("Evento", "evento adicionado");
//         valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Usuario usuario = dataSnapshot.getValue(Usuario.class);
//                assert usuario != null;
//                despesaTotal = usuario.getDespesaTotal();
//                receitaTotal = usuario.getReceitaTotal();
//                resumoUsuario = receitaTotal - despesaTotal;
//
//                DecimalFormat decimalFormat = new DecimalFormat("0.##");
//                String resultadoFormatado = decimalFormat.format(resumoUsuario);
//
//                textoSaudacao.setText("Olá, " + usuario.getNome());
//                textoSaldo.setText("R$" + resultadoFormatado);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_principal, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menuSair :
//                autenticacao.signOut();
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    public void adicionarReceita(View view){
//        startActivity(new Intent(this, ReceitasActivity.class));
//
//    }
//
//    public void adicionarDespesa(View view){
//        startActivity(new Intent(this, DespesasActivity.class));
//
//    }
//
//    String mesSelecionado;
//
//    private void configuraCalendarView() {
//        CharSequence meses[] = {"Janeiro","Fevereiro","Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembo", "Outubro", "Novembr", "Dezembro"};
//        calendarView.setTitleMonths(meses);
//
//        CalendarDay dataAtual = calendarView.getCurrentDate();
//         mesSelecionado = String.format("%02d", (dataAtual.getMonth()));
//        mesAnoSelecionado = ( mesSelecionado + "" + dataAtual.getYear());
//
//        calendarView.setOnMonthChangedListener((widget, date) -> {
//            mesSelecionado = String.format("%02d", (dataAtual.getMonth()));
//
//            mesAnoSelecionado = ((mesSelecionado + "" + date.getYear()));
//            movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
//            recuperarMovimentacoes();
//        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        recuperarResumo();
//        recuperarMovimentacoes();
//    }
//
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i("Evento", "Evento removido");
//        usuarioRef.removeEventListener(valueEventListenerUsuario);
//        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
//    }
//}