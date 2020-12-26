
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Movie implements Comparable<Movie>{
    private String title;
    private List<String> tags;
    private Map<User, Integer> ratings = new HashMap<>();

    public Movie(String title) { this.title = title;}

    public void setTags(String[] tags) {
        this.tags = Arrays.asList(tags);
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return title;
    }


    @Override
    public int compareTo(Movie o) {
        return this.toString().compareTo(o.toString());
    }

    public boolean containsAllTags(String[] tags){
        if (tags.length == 0) return false;

        boolean isSubset = true;
        for (String tag:tags){
            if (!this.tags.contains(tag)) isSubset = false;
        }
        return isSubset;
    }

    // previous input check needed from MovieApp!
    public void rateMovie(User user, int rating){
        ratings.put(user, rating);
    }

    public double getAverageRating(){
        if (ratings.size() == 0) return 0;

        double sumRating = 0.0;
        for (Map.Entry<User, Integer> pair:ratings.entrySet()){
            sumRating += pair.getValue();
        }
        return sumRating/ratings.size();
    }


}
