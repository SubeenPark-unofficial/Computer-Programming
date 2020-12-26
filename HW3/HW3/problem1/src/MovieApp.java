
import java.util.*;

public class MovieApp {

    private List<Movie> movies = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Movie> moviesRated = new ArrayList<>();
    private Map<User, List<Movie>> searchHistories = new HashMap<>();

    public boolean addMovie(String title, String[] tags) {
        // TODO sub-problem 1
        if (findMovie(title) == null){
            Movie movie = new Movie(title);
            movie.setTags(tags);
            movies.add(movie);
            return true;
        }
        return false;
    }

    public boolean addUser(String name) {
        // TODO sub-problem 1
        if (findUser(name) == null){
            User user = new User(name);
            users.add(user);
            return true;
        }
        return false;
    }

    public Movie findMovie(String title) {
        // TODO sub-problem 1
        for (Movie movie:movies){
            if (movie.toString().equals(title)) return movie;
        }
        return null;
    }

    public User findUser(String username) {
        // TODO sub-problem 1
        for (User user:users){
            if (user.toString().equals(username)) return user;
        }
        return null;
    }

    public List<Movie> findMoviesWithTags(String[] tags) {
        // TODO sub-problem 2
        List<Movie> moviesWithTags = new LinkedList<>();
        for (Movie movie:movies){
            if (movie.containsAllTags(tags)) moviesWithTags.add(movie);
        }
        Collections.sort(moviesWithTags);
        return moviesWithTags;
    }


    public boolean rateMovie(User user, String title, int rating) {
        // TODO sub-problem 3
        if (user != null && title != null
                && findUser(user.toString())!= null && findMovie(title) != null
                && rating >= 1 && rating <=10){
            Movie movie = findMovie(title);
            moviesRated.add(movie);
            movie.rateMovie(user, rating);
            user.rateMovie(movie, rating);
            return true;
        }
        return false;
    }

    public int getUserRating(User user, String title) {
        // TODO sub-problem 3
        if (user != null && title != null && findMovie(title) != null){
            return user.getRatingOf(findMovie(title));
        }
        return -1;
    }

    public List<Movie> findUserMoviesWithTags(User user, String[] tags) {
        // TODO sub-problem 4i
        if (user != null && findUser(user.toString()) != null){
            List<Movie> searchResult = findMoviesWithTags(tags);
            if (searchHistories.containsKey(user)){
                searchHistories.get(user).addAll(searchResult);
            }
            else {
                searchHistories.put(user, searchResult);
            }
            return searchResult;
        }
        return new LinkedList<>();
    }

    public List<Movie> recommend(User user) {
        // TODO sub-problem 4
        if (user == null || findUser(user.toString()) == null
                || searchHistories.get(user) == null)  return new LinkedList<>();
        else if (searchHistories.get(user).size() < 3) return searchHistories.get(user);
        else{
            return recommendMovie(searchHistories.get(user));
        }
    }

    private List<Movie> recommendMovie(List<Movie> searchedMovieList){
        List<CandidateMovie> candidateMovies = new ArrayList<>();
        for (Movie movie :searchedMovieList){
            candidateMovies.add(new CandidateMovie(movie));
        }
        Collections.sort(candidateMovies);
        return CandidateMovie.toMovieList(candidateMovies.subList(0,3));
    }

    private void printMovieList(List<Movie> movieList){
        for (Movie movie:movieList){
            System.out.print(movie.toString() + " ");
        }
        System.out.println("");
    }

}

class CandidateMovie implements Comparable<CandidateMovie>{
    private Movie movie;
    private String title;
    private double averageRating;

    CandidateMovie(Movie movie){
        this.movie = movie;
        this.title = movie.toString();
        this.averageRating = movie.getAverageRating();
    }

    public double getAverageRating() {
        return averageRating;
    }

    public String getTitle(){
        return title;
    }

    public Movie getMovie(){
        return movie;
    }

    public static List<Movie> toMovieList(List<CandidateMovie> candidateMovies){
        List<Movie> movieList = new ArrayList<>();
        for (CandidateMovie candidateMovie:candidateMovies){
            movieList.add(candidateMovie.getMovie());
        }
        return movieList;
    }

    @Override
    public int compareTo(CandidateMovie o) {
        if (averageRating == o.getAverageRating()){
            return this.title.compareTo(o.getTitle());
        }
        else {
            return Double.compare(o.getAverageRating(), averageRating);
        }
    }
}
