 package br.com.app.apptempo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

 public class MainActivity extends AppCompatActivity {

     EditText nomeDaCidade;
     TextView resultTextView;

     public void findWeather(View view) throws UnsupportedEncodingException {

        Log.i("nome da cidade", nomeDaCidade.getText().toString());
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(nomeDaCidade.getWindowToken(),0);

        String encodedCityName = URLEncoder.encode( nomeDaCidade.getText().toString(), "UTF-8");

         DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q=" +encodedCityName + "&APPID=6edeebdd9effb361137533c6d3231941");
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomeDaCidade = findViewById(R.id.nomeDaCidade);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public class DownloadTask extends AsyncTask<String, Void, String >{

        @Override
        protected String doInBackground(String... urls) {

            String resultado = "";
            URL url;
            HttpURLConnection urlConnection;


            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1){

                    char current = (char)data;
                    resultado += current;
                    data = reader.read();
                }

                return resultado;

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Digite uma cidade válida", Toast.LENGTH_LONG);
            }


            return null;
        }

        protected void onPostExecute(String resultado){
            super.onPostExecute(resultado);
                if (resultado != null) {


                    try {

                        String message = "";

                        JSONObject jsonObject = new JSONObject(resultado);

                        String weatherInfo = jsonObject.getString("weather");

                        Log.i("Clima tempo", weatherInfo);
                        JSONArray array = new JSONArray(weatherInfo);

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject jsonPart = array.getJSONObject(i);

                            String main = "";
                            String description = "";

                            main = jsonPart.getString("main");
                            description =  jsonPart.getString("description");

                            if (main != "" && description != ""){
                                message += main + ": " + description + "\r\n";
                            }

                        }

                        if (message !=""){
                            resultTextView.setText(message);
                        }else {
                            Toast.makeText(getApplicationContext(), "Digite uma cidade válida", Toast.LENGTH_LONG);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Digite uma cidade válida", Toast.LENGTH_LONG);
                    }
                }
        }
    }
}