package io.saeed.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoviesFragment extends Fragment {

    final String MDB_PAGE = "page";
    final String MDB_RESULTS = "results";
    final String MDB_TITLE = "original_title";
    final String MDB_POSTER = "poster_path";
    final String MDB_OVERVIEW = "overview";
    final String MDB_VOTE_AVG = "vote_average";
    final String MDB_DATE = "release_date";

    ImageAdapter imageAdapter;
    GridView gridView;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is to give this fragment menu options.
  //        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView)rootView.findViewById(R.id.movie_images_grid);
        imageAdapter = new ImageAdapter(getActivity(), new ArrayList<Map<String,String>>());
        gridView.setAdapter(imageAdapter);
        getMovies();

        return rootView;
    }

    public void getMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        moviesTask.execute(sortBy);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                HashMap item = (HashMap) imageAdapter.getItem(position);
                Bundle extras = new Bundle();
                extras.putSerializable("movieInfo", item);
                intent.putExtra("movieInfo", extras);
                startActivity(intent);
            }
        });
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            for(String movieJsonStr : result) {
                try {
                    JSONObject movieJson = new JSONObject(movieJsonStr);
                    Map<String,String> mMap = new HashMap<String,String>();
                    mMap.put(MDB_TITLE, movieJson.getString(MDB_TITLE));
                    mMap.put(MDB_POSTER, buildImagePath(movieJson.getString(MDB_POSTER)));
                    mMap.put(MDB_OVERVIEW, movieJson.getString(MDB_OVERVIEW));
                    mMap.put(MDB_DATE, movieJson.getString(MDB_DATE));
                    mMap.put(MDB_VOTE_AVG, String.valueOf(movieJson.getLong(MDB_VOTE_AVG)));

                    imageAdapter.add(mMap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonString = null;

            try {
                URL url = buildUrl(params[0]);

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    moviesJsonString = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    moviesJsonString = null;
                }

                moviesJsonString = buffer.toString();
                Log.v("Response from API",moviesJsonString);
            } catch (IOException e) {
                Log.e("MoviesFragment", "Error ", e);
                moviesJsonString = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MoviesFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(moviesJsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private URL buildUrl(String sortOption) throws MalformedURLException {
            //http://api.themoviedb.org/3/discover/movie?api_key=YOUR_API_KEY&sort_by=popularity.desc
            Uri.Builder builder = new Uri.Builder();

            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("api_key", "354bc6118f464541061311a8ebe450f6")
                    .appendQueryParameter("sort_by", sortOption);

            URL url = new URL(builder.toString());
            Log.v("Built URI", builder.toString());
            return url;
        }

        private ArrayList<String> getMovieDataFromJson(String moviesJsonString) throws JSONException {

            JSONObject MoviesJson = new JSONObject(moviesJsonString);
            JSONArray movieArray = MoviesJson.getJSONArray(MDB_RESULTS);

            ArrayList<String> moviesJsonList = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                moviesJsonList.add(movieArray.getJSONObject(i).toString());
            }

            return moviesJsonList;
        }

        private String buildImagePath(String fileName) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("image.tmdb.org")
                    .appendPath("t")
                    .appendPath("p")
                    .appendPath("w500").appendPath(fileName.replace("/", ""));
            return builder.build().toString();
        }
    }
}
