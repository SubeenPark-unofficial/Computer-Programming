import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;



public class BackEnd extends ServerResourceAccessible {
    // Use getServerStorageDir() as a default directory
    // TODO sub-program 1 ~ 4 :
    // Create helper funtions to support FrontEnd class

    public boolean isValidId(String id){
        File FILE_PATH = new File(getServerStorageDir());
        List<String> userDirectories = Arrays.asList(FILE_PATH.list());
        if (userDirectories.contains(id)) return true;
        return false;

    }

    public String retrieveUserPassword(String username){
        String password = null;
        try {
            BufferedReader br =
                    new BufferedReader(new FileReader(getServerStorageDir() + username + "/password.txt"));
            password = br.readLine();
        }
        catch (Exception e) {
        }
        return password;
    }

    public int setPostID(){
        File FILE_PATH = new File(getServerStorageDir());
        String[] userDirectories = FILE_PATH.list();
        int fileCount = 0;
        for (String user:userDirectories){
            File dir = new File(getServerStorageDir() + user + "/post/");
            if (dir.list() == null) return 0;
            fileCount += dir.list().length;
        }
        // System.out.println(fileCount);
        return fileCount;
    }

    private File getPostPath(User user){
        File postPath= new File(getServerStorageDir() + user.id  + "/post/" + setPostID() + ".txt");
        return postPath;
    }

    public void writePost(User user, String datetime, String title, String[] content){
        File file= getPostPath(user);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(datetime + "\n");
            fileWriter.write(title + "\n\n");
////            if (content.length != 1){
////                List<String> contentWithoutTrailing = Arrays.asList(content).subList(0, content.length-1);
//            for (int i = 0;)
////            }
////            if (content.length == 1){
////                fileWriter.write(content[0]);
//            }

            if (content.length == 1){
                fileWriter.write(content[0]);
            }
            else {
                for (int i = 0; i < content.length; i++){
                    if (i == content.length-1){
                        fileWriter.write(content[i]);
                    }
                    else {
                        fileWriter.write(content[i] + "\n");
                    }
                }
            }
            fileWriter.close();
        }
        catch (IOException e) {
            // do nothing
        }

    }

    private List<Post> getPostsOfFriend(User user){
        if (user == null) return null;

        List<String> friends = getFrindOfUser(user);
        if (friends == null) return null;

        List<Post> postsOfFriends = new ArrayList<>();

        for (String friend:friends){
            File FILE_PATH = new File(getServerStorageDir() + friend + "/post/");
            String[] posts = FILE_PATH.list();
            if (posts == null) return null;
                for (String post:posts){
                File postPath = new File(getServerStorageDir() + friend + "/post/" + post);
                postsOfFriends.add(retrievePost(postPath));
            }
        }
        return postsOfFriends;

    }

    public List<Post> recommendedPost(User user){
        List<Post> postsOfFriends = getPostsOfFriend(user);
        if (postsOfFriends == null) return null;

        Collections.sort(postsOfFriends);
        if (postsOfFriends.size() < 10) return postsOfFriends;

        return postsOfFriends.subList(0, 10);
    }

    private List<String> getFrindOfUser(User user){
        File friendFile = new File(getServerStorageDir() + user.id + "/friend.txt");

        List<String> friends = new ArrayList<>();
        try {
            Scanner input = new Scanner(friendFile);
            while (input.hasNextLine()) {
                String friend = input.nextLine();
                friends.add(friend);
                //System.out.println(friend);
            }
        }
        catch (Exception e){
            //do nothing
        }
        return friends;
    }


    private Post retrievePost(File postFile){
        String[] postInfo = null;
        String dateString = null;
        String title = null;
        String content = "";
        try {
            String text = Files.readString(postFile.toPath());
            postInfo = text.split("\n");
            dateString = postInfo[0].substring(0, 10) + "_" + postInfo[0].substring(11,19);
            title = postInfo[1];
            for (String line: Arrays.asList(postInfo).subList(3, postInfo.length)){
                content += (line + "\n");
            }
        }
        catch (Exception e) {
            // do nothing
        }

        // retrieve id
        String postFileString = postFile.toString();
        String idString = postFileString.substring(postFileString.lastIndexOf('/')+1, postFileString.lastIndexOf('.'));
        //System.out.println(idString);
        int id = Integer.parseInt(idString);

        //retrieve Datetime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd_HH:mm:ss");
        LocalDateTime datetime = LocalDateTime.parse(dateString,dateTimeFormatter);

        Post post = new Post(id,datetime, title, content);

        //System.out.println(post.toString());
        return post;
    }

    private Set<String> retrieveKeywords(String searchCommand){
        String[] searchCommandSplit = searchCommand.split(" ");
        Set<String> keywords = new HashSet<>(Arrays.asList(searchCommandSplit).subList(1, searchCommandSplit.length));
        return keywords;
    }

    public Boolean hasKeyword(Post post, String keyword){

        //title
        for (String word:post.getTitle().split(" ")){
            if (word.contains(keyword)) return true;
        }

        //content
        for (String line:post.getContent().split("\n")){
            for (String word:line.split(" ")){
                if (word.contains(keyword)) return true;
            }
        }

        return false;
    }

    private int countKeywords(Post post, Set<String> keywords){
        int count = 0;
        for (String keyword:keywords){
            if (hasKeyword(post, keyword)) count++;
        }
        return count;
    }

    public List<Post> searchPost(String searchCommand){
        List<Post> searchedPosts = new ArrayList<>();
        Map<Post, Integer> candidatePosts = new HashMap<>();

        File FILE_PATH = new File(getServerStorageDir());
        String[] userDirectories = FILE_PATH.list();
        if (userDirectories == null) return null;
        Set<String> keywords = retrieveKeywords(searchCommand);


        for (String user:userDirectories){
            String postPath = getServerStorageDir() + user + "/post/";
            String[] allFiles = new File(postPath).list();
            if (allFiles == null) return null;

            for (String file : allFiles){
                File filePath = new File(postPath + file);
                Post post = retrievePost(filePath);
                candidatePosts.put(post, countKeywords(post, keywords));
            }
        }

        Map<Post, Integer> sortedMap = sortMap(candidatePosts);
//        for (Map.Entry<Post, Integer> entry:sortedMap.entrySet()){
//            System.out.println(entry.getValue() + " " + entry.getKey().getSummary());
//        }

        int count = 10;
        for (Map.Entry<Post, Integer> entry:sortedMap.entrySet()){
            if (count > 0 && entry.getValue() > 0) {
                searchedPosts.add(entry.getKey());
                count--;
            }
            else{
                break;
            }
        }


        return searchedPosts;

    }


    private Map<Post, Integer> sortMap(Map<Post, Integer> map) {
        List<Map.Entry<Post, Integer>> list = new LinkedList<>(map.entrySet());


        Collections.sort(list, new Comparator<>() {
            @Override
            public int compare(Map.Entry<Post, Integer> o1, Map.Entry<Post, Integer> o2) {
                int comparison = (o1.getValue() - o2.getValue()) * -1;
                return comparison == 0 ? o1.getKey().compareTo(o2.getKey()) : comparison;
            }
        });

        Map<Post, Integer> sortedMap = new LinkedHashMap<>();
        for (Iterator<Map.Entry<Post, Integer>> iter = list.iterator(); iter.hasNext();) {
            Map.Entry<Post, Integer> entry = iter.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;




    }


}

