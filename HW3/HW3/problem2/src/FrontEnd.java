import java.util.*;
import java.time.LocalDateTime;

public class FrontEnd {
    private UserInterface ui;
    private BackEnd backend;
    private User user;

    public FrontEnd(UserInterface ui, BackEnd backend) {
        this.ui = ui;
        this.backend = backend;
    }
    
    public boolean auth(String authInfo){
        // TODO sub-problem 1
        String[] info = authInfo.split("\n");
        if (info.length < 2) return false;

        String idInput = info[0];
        String pwInput = info[1];

        // Check whether id is valid
        if (!backend.isValidId(idInput))  return false;

        // Check whether id-password.txt pair matches with the data
        if (backend.retrieveUserPassword(idInput).equals(pwInput)){
            user = new User(idInput, pwInput);
            return true;
        }

        return false;
    }

    public void post(Pair<String, String> titleContentPair) {
        // TODO sub-problem 2
        Post post = new Post(titleContentPair.key, titleContentPair.value);
        post.setId(backend.setPostID());

        String[] content = post.getContent().split("\n");
        backend.writePost(user, post.getDate(), post.getTitle(), content);
    }
    
    public void recommend(){
        // TODO sub-problem 3
        try {
            List<Post> recommendedPost = backend.recommendedPost(user);
            if (recommendedPost == null) return;

            for (Post post:recommendedPost){
                ui.println(post.toString());
            }
        }
        catch(NullPointerException e){
            return;
        }


    }

    public void search(String command) {
        // TODO sub-problem 4
        List<Post> searchResult = backend.searchPost(command);
        try {
            for (Post post:searchResult){
                ui.println(post.getSummary());
            }
        }
        catch (NullPointerException e){
            return;
        }


    }
    
    User getUser(){
        return user;
    }
}
