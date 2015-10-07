package com.example.mladen.firstapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mladen.firstapp.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;


public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://mladendinev.comxa.com/";

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
    }


    public void storeUserDataInBackground(User user, int alreadyExists,
                                          GetUserCallback userCallBack) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, alreadyExists, userCallBack).execute();
    }

    public void fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
        progressDialog.show();
        new fetchUserDataAsyncTask(user, userCallBack).execute();
    }

    /**
     * parameter sent to task upon execution progress published during
     * background computation result of the background computation
     */

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallBack;
        int alreadyExists;

        public StoreUserDataAsyncTask(User user,int alreadyExists, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
            this.alreadyExists = alreadyExists;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            ArrayList<NameValuePair> existUser = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));
            dataToSend.add(new BasicNameValuePair("email", user.email));

            existUser.add(new BasicNameValuePair("email", user.email));


            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");
            HttpPost existUserPost = new HttpPost(SERVER_ADDRESS + "ExistUser.php");

            try {
                existUserPost.setEntity(new UrlEncodedFormEntity(existUser));
                HttpResponse httpResponse = client.execute(existUserPost);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);
                if (jObject.getString("exists").contentEquals("1"))
                {
                    alreadyExists = 1;
                }

                else{
                    alreadyExists = 0;
                    post.setEntity(new UrlEncodedFormEntity(dataToSend));
                    client.execute(post);
                }

                ;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private HttpParams getHttpRequestParams() {
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            return httpRequestParams;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            userCallBack.done(null,alreadyExists);
        }

    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallBack;
        int alreadyExists;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
            this.alreadyExists = 0;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");

            User returnedUser = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

                if (jObject.length() != 0) {
                    Log.v("happened", "2");
                    String email = jObject.getString("email");

                    returnedUser = new User(email, user.username,
                            user.password);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
            progressDialog.dismiss();
            userCallBack.done(returnedUser,0);
        }
    }
}