

import java.util.*;

public class User {
    private String username;
    private Map<Movie, Integer> moviesRated = new HashMap<>();

    public User(String username) { this.username = username; }
    @Override
    public String toString() {
        return username;
    }

    public void rateMovie(Movie movie, int rating){
        moviesRated.put(movie, rating);
    }

    private boolean isRatedMovie(Movie movie){
        return moviesRated.containsKey(movie);
    }

    public int getRatingOf(Movie movie){
        if (isRatedMovie(movie)){
           return moviesRated.get(movie);
        }
        return 0;
    }
}
