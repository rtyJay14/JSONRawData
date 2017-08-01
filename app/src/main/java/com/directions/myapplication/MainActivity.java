package com.directions.myapplication;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity implements View.OnClickListener {

    EditText userId, password;
    TextView appIsConnected;
    Button btnPost;

    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to the views
        appIsConnected = (TextView) findViewById(R.id.appIsConnected);
        userId = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btnPost = (Button) findViewById(R.id.loginButton);

        // check if you are connected or not
        if(isConnected()){
            appIsConnected.setBackgroundColor(0xFF00CC00);
            appIsConnected.setText("You are conncted");
        }
        else{
            appIsConnected.setText("You are NOT conncted");
        }

        // add click listener to Button "POST"
        btnPost.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loginButton:
                if (!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://mark.journeytech.com.ph/mobile_api/authentication.php");
                break;
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            account = new Account();
            account.setUserid(userId.getText().toString());
            account.setPassword(password.getText().toString());
//            account.setTwitter(etTwitter.getText().toString());

            return POST(urls[0], account);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!" + result, Toast.LENGTH_LONG).show();
        }
    }

    public static String POST(String url, Account account) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("userid", account.getUserid());
            jsonObject.accumulate("pass", account.getPassword());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();


            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);/*, ContentType.APPLICATION_JSON);*/

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();


            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private boolean validate() {
        if (userId.getText().toString().trim().equals(""))
            return false;
        else if (password.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}