 package br.com.app.testedoleo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

 public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            SQLiteDatabase bancoDados = openOrCreateDatabase("Autores", MODE_PRIVATE, null);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS autores (id INTEGER PRIMARY KEY AUTOINCREMENT, nome VARCHAR, nacionalidade VARCHAR, dataNascimento int(8), notas int(2), avaliacao int (1))");

//            bancoDados.execSQL("INSERT INTO autores(nome, nacionalidade, dataNascimento, notas, avaliacao) VALUES ('Leo', 'brasileiro', 18021990, 5, 5)");
//            bancoDados.execSQL("INSERT INTO autores(nome, nacionalidade, dataNascimento, notas, avaliacao) VALUES ('Ze', 'brasileiro', 20042000, 5, 5)");
//            bancoDados.execSQL("INSERT INTO autores(nome, nacionalidade, dataNascimento, notas, avaliacao) VALUES ('Ana', 'brasileiro', 20041990, 4, 4)");

            Cursor cursor = bancoDados.rawQuery("SELECT nome, nacionalidade, dataNascimento, notas, avaliacao FROM autores ", null);

            int indiceNome = cursor.getColumnIndex("nome");
            int indiceNacionalidade = cursor.getColumnIndex("nacionalidade");


            cursor.moveToFirst();
            while (cursor != null){
                Log.i("Resultado", cursor.getString(indiceNome));
                Log.i("Resultado", cursor.getString(indiceNacionalidade));
                cursor.moveToNext();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}