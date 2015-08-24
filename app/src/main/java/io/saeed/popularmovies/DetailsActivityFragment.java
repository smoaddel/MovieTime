package io.saeed.popularmovies;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

//    private static final String baseURL = "http://image.tmdb.org/t/p/w342";
    final String MDB_TITLE = "original_title";
    final String MDB_POSTER = "poster_path";
    final String MDB_OVERVIEW = "overview";
    final String MDB_VOTE_AVG = "vote_average";
    final String MDB_DATE = "release_date";

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle extras = getActivity().getIntent().getBundleExtra("movieInfo");
        HashMap movieInfo = (HashMap)extras.getSerializable("movieInfo");
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        //set movie title
        TextView title = (TextView)view.findViewById(R.id.movie_title);
        title.setText((CharSequence)movieInfo.get(MDB_TITLE));
        //set movie poster
        ImageView imageView = (ImageView)view.findViewById(R.id.movie_poster);
        String poster = (String) movieInfo.get(MDB_POSTER);
        Picasso.with(getActivity()).load(poster).into(imageView);
        //set movie year
        TextView txtViewYear = (TextView)view.findViewById(R.id.movie_year);
        String releaseDate = (String)movieInfo.get(MDB_DATE);
        txtViewYear.setText(releaseDate.subSequence(0, 4));

        //set rating
        TextView txtViewRating = (TextView)view.findViewById(R.id.movie_rating);
        String strRating = movieInfo.get(MDB_VOTE_AVG) + "/10";
        txtViewRating.setText(strRating);
        //set summary
        TextView txtViewSummary = (TextView)view.findViewById(R.id.movie_summary);
        txtViewSummary.setText((CharSequence)movieInfo.get(MDB_OVERVIEW));
        return view;
    }
}
