package com.example.memeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ImageView memeImageView;
    private ProgressBar progressBar;
    private String currentMemeUrl; // To store the current meme URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the ImageView and ProgressBar
        memeImageView = findViewById(R.id.meme);
        progressBar = findViewById(R.id.progressBar);

        // Load a meme when the activity starts
        loadMeme();
    }

    // Method to load a meme
    public void loadMeme() {
        progressBar.setVisibility(View.VISIBLE);  // Show the progress bar while loading the meme

        // Create a new RequestQueue instance
        RequestQueue queue = Volley.newRequestQueue(this);
        // URL to fetch the meme JSON object
        String url = "https://meme-api.com/gimme";

        // Create a new JsonObjectRequest to fetch meme data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get the URL of the meme from the response JSON object
                            currentMemeUrl = response.getString("url"); // Store the URL
                            // Use Glide to load the meme image into the ImageView
                            Glide
                                    .with(MainActivity.this)  // Use MainActivity.this instead of 'this'
                                    .load(currentMemeUrl)
                                    .into(memeImageView);  // Use memeImageView instead of myImageView
                            // Hide the progress bar after the image is loaded
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            throw new RuntimeException(e); // Handle JSON parsing error
                        }
                        Log.d("API Response", response.toString()); // Log the API response
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log the error if the request fails
                        Log.e("API Error", "Failed to fetch data", error);
                        // Hide the progress bar if there is an error
                        progressBar.setVisibility(View.GONE);
                    }
                });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    // Method to share the current meme URL
    public void sharmeme(View view) {
        if (currentMemeUrl != null) {
            // Create an Intent to share the meme URL
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this meme: " + currentMemeUrl);
            // Start the share Intent
            startActivity(Intent.createChooser(shareIntent, "Share meme using"));
        } else {
            // Log an error if there is no meme URL to share
            Log.e("Share Error", "No meme URL to share");
        }
    }

    // Method to load the next meme
    public void nextmeme(View view) {
        loadMeme(); // Call the loadMeme() method to load the next meme
    }
}
