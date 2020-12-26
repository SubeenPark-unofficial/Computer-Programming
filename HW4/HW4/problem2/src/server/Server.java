package server;

import course.*;
import user.*;
import utils.Config;
import utils.ErrorCode;
import utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Server {

    public List<Course> search(Map<String,Object> searchConditions, String sortCriteria) {
        // TODO Problem 2-1
        List<Course> allCourses = retrieveCourseInfo();
        List<Course> searcedCourses = getSearcedCourses(allCourses, searchConditions);
        sortByConditions(searcedCourses, sortCriteria);

        return searcedCourses;
    }

    public int bid(int courseId, int mileage, String userId){
        // TODO Problem 2-2
        int errorCode = ErrorCode.SUCCESS;
        User user = null;

        try {
            if (!isValidUser(userId)){
                errorCode = Math.min(errorCode, ErrorCode.USERID_NOT_FOUND);
            }
            else {
                user = new User(userId);

                if (mileage > Config.MAX_MILEAGE_PER_COURSE) {
                    errorCode = Math.min(errorCode, ErrorCode.OVER_MAX_COURSE_MILEAGE);
                }
                if (mileage < 0){
                    errorCode = Math.min(errorCode, ErrorCode.NEGATIVE_MILEAGE);
                }
                if (!isValidCourse(courseId)){
                    errorCode = Math.min(errorCode, ErrorCode.NO_COURSE_ID);
                }

                if (user.expectedMileage(courseId, mileage) > Config.MAX_MILEAGE)
                    errorCode = Math.min(errorCode, ErrorCode.OVER_MAX_MILEAGE);
            }
        } catch (FileNotFoundException e){
            errorCode = Math.min(errorCode, ErrorCode.IO_ERROR);
            return errorCode;
        }

        if (errorCode == ErrorCode.SUCCESS){
            try {
                user.makeBid(courseId, mileage);
            }
            catch (FileNotFoundException e){
                // do nothing
            }
            catch (IOException e){
                // do nothing
            }
        }

        return errorCode;
    }

    public Pair<Integer,List<Bidding>> retrieveBids(String userId){
        // TODO Problem 2-2
        int errorCode = ErrorCode.SUCCESS;
        List<Bidding> biddings = new ArrayList<>();
        try {
            if (!isValidUser(userId)) {
                errorCode = ErrorCode.USERID_NOT_FOUND;
            }
            else {
                User user = new User(userId);
                biddings = user.retrieveBids();
            }
        } catch (FileNotFoundException e) {
            errorCode = Math.min(errorCode, ErrorCode.IO_ERROR);
            return new Pair<>(errorCode, biddings);
        }
        return new Pair<>(errorCode, biddings);

    }

    public boolean confirmBids(){

        // TODO Problem 2-3
        List<User> users = retrieveAllUsers();
        List<Course> courses = retrieveCourseInfo();

        try {
            registerAllStudents(courses, users);
        } catch (IOException e){
            return false;
        }
        return true;
    }

    public Pair<Integer,List<Course>> retrieveRegisteredCourse(String userId){
        // TODO Problem 2-3

        List<Course> courses = new ArrayList<>();
        int errorCode = ErrorCode.SUCCESS;
        try {
            if (!isValidUser(userId)) {
                errorCode = Math.min(errorCode, ErrorCode.USERID_NOT_FOUND);
            } else {
                User user = new User(userId);
                courses = user.retrieveRegisteredCourse();
            }
        } catch (IOException e){
            errorCode = Math.min(errorCode, ErrorCode.IO_ERROR);
        }
        return new Pair<>(errorCode, courses);
    }

    // PROBLEM 1
    private final String coursePath = "data/Courses/2020_Spring/";

    // Problem 1-1 : retrieve information of all existing courses
    private List<Course> retrieveCourseInfo(){
        List<Course> allCourses = new ArrayList<>();
        try {
            List<String> colleges = getColleges();

            if (colleges.size() != 0) {
                for (String college : colleges) {
                    List<String> courses = getCourses(college);
                    if (courses.size() != 0) {
                        for (String course : courses) {
                            Course courseObj = retrieveCourse(college, course);
                            allCourses.add(courseObj);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // do nothing
        }
        return allCourses;
    }

    // Problem 1-1-1 : retrieve all college lists
    private List<String> getColleges(){
        List<String> colleges = new ArrayList<>();

        File path = new File(coursePath);
        File[] collegeFiles = path.listFiles();
        if (collegeFiles != null){
            for (File collegeFile:collegeFiles){
                if (collegeFile.isDirectory()) colleges.add(collegeFile.getName());
            }
        }
        return colleges;
    }

    // Problem 1-1-2 : retrieve all class lists
    private List<String> getCourses(String college){
        List<String> classes = new ArrayList<>();

        File path = new File(coursePath + college);
        File[] courseFiles = path.listFiles();
        if (courseFiles != null){
            for (File courseFile:courseFiles){
                if (courseFile.isFile()) classes.add(courseFile.getName());
            }
        }

        return classes;
    }

    // Problem 1-1-3 : retrieveCourse : create class instances
    private Course retrieveCourse(String college, String courseIdTxt) throws IOException {
        Path path = Path.of(coursePath + college + "/" + courseIdTxt);
        String[] courseInfo = Files.readString(path).split("\\|");

        int courseId = Integer.parseInt(courseIdTxt.substring(0, courseIdTxt.lastIndexOf('.')));
        String department = courseInfo[0];
        String academicDegree = courseInfo[1];
        int academicYear = Integer.parseInt(courseInfo[2]);
        String courseName = courseInfo[3];
        int credit = Integer.parseInt(courseInfo[4]);
        String location = courseInfo[5];
        String instructor = courseInfo[6];
        int quota = Integer.parseInt(courseInfo[7]);

        return new Course (courseId, college, department, academicDegree, academicYear,
        courseName, credit, location, instructor, quota);
    }

    // Problem 1-2: sieve courses in order (null -> dept -> ay -> names)
    private List<Course> getSearcedCourses(List<Course> allCourses, Map<String, Object> searchConditions){
        List<Course> courses = allCourses;
        List<String> conditionList = new ArrayList<>(searchConditions.keySet());

        // System.out.println(searchConditions); // checkpoint

        if (searchConditions == null || searchConditions.isEmpty()){
            return courses;
        }
        if (conditionList.contains("ay")) {
            courses = searchCoursesByAy(courses, (int) searchConditions.get("ay"));
        }
        if (conditionList.contains("dept")) {
            courses = searchCoursesByDept(courses, (String) searchConditions.get("dept"));
        }
        if (conditionList.contains("name")) {
            courses = searchCoursesByName(courses, (String) searchConditions.get("name"));
        }

        return courses;
    }

    // Problem 1-2-1: search courses by "Dept"
    private List<Course> searchCoursesByDept(List<Course> courses, String department){
        List<Course> searched = new ArrayList<>();
        for (Course course:courses){
            if (course.department.equals(department)) searched.add(course);
        }
        return searched;
    }

    // Problem 1-2-3: search courses by "ay"(academic year)
    private List<Course> searchCoursesByAy(List<Course> courses, int academicYear){
        List<Course> searched = new ArrayList<>();
        for (Course course:courses){
            if (course.academicYear == academicYear) searched.add(course);
        }
        return searched;
    }

    // Problem 1-2-1: search courses by "name"
    private List<Course> searchCoursesByName(List<Course> courses, String searchKeyword){
        List<Course> searched = new ArrayList<>();
        for (Course course:courses){
            if (containsAllKeywords(course, searchKeyword)) searched.add(course);
        }
        return searched;
    }



    // Problem 1-2-3-1: check whether the courseName contains all keywords
    private boolean containsAllKeywords(Course course, String searchKeyword){
        List<String> courseNameList = splitTexts(course.courseName);
        List<String> searchKeywordList = splitTexts(searchKeyword);

        for (String word:searchKeywordList){
            if (!courseNameList.contains(word)) {
                return false;
            }
        }
        return true;
    }

    // Problem 1-2-3-1-1: splitTexts(used for courseName/searchKeyword splitting)
    private List<String> splitTexts(String text){
        return Arrays.asList(text.split(" "));
    }

    // Problem 1-3: Sort search result by conditions NULL -> EMPTY STRING -> NAME -> DEPT -> ID
    private void sortByConditions(List<Course> courses, String sortCriteria){

        if (sortCriteria == null || sortCriteria.equals("") || sortCriteria.equals("id")){
            courses.sort(new IdComparator());
            return;
        }

        if (sortCriteria.equals("name")) courses.sort(new NameComparator());
        if (sortCriteria.equals("dept")) courses.sort(new DeptComparator());
        if (sortCriteria.equals("ay")) courses.sort(new AyComparator());

    }

    // Problem 1-3-1: Sort search result by Id
    static class IdComparator implements Comparator<Course>{
        @Override
        public int compare(Course o1, Course o2) {
            return Integer.compare(o1.courseId, o2.courseId);
        }
    }

    // Problem 1-3-2: Sort search result by Name
    static class NameComparator implements Comparator<Course>{
        @Override
        public int compare(Course o1, Course o2) {
            if (o1.courseName.equals(o2.courseName)) return Integer.compare(o1.courseId, o2.courseId);
            return o1.courseName.compareTo(o2.courseName);
        }
    }

    // Problem 1-3-3: Sort search result by Dept
    static class DeptComparator implements Comparator<Course>{
        @Override
        public int compare(Course o1, Course o2) {
            if (o1.department.equals(o2.department)) return Integer.compare(o1.courseId, o2.courseId);
            return o1.department.compareTo(o2.department);
        }
    }

    // Problem 1-3-4: Sort search result by Ay
    static class AyComparator implements Comparator<Course>{
        @Override
        public int compare(Course o1, Course o2) {
            if (o1.academicYear == o2.academicYear) return Integer.compare(o1.courseId, o2.courseId);
            return Integer.compare(o1.academicYear, o2.academicYear);
        }
    }


    // Problem 2-2 retrieveBid : use USER CLASS!


    private final String userPath = "data/Users/";
    // Problem 2-2-1 check whether the userId is valid
    private boolean isValidUser(String userId){
        File userDir = new File(userPath);
        File[] subDirs = userDir.listFiles(File::isDirectory);
        if (subDirs == null) return false;
        for (int i = 0; i < subDirs.length; i++){
            if (subDirs[i].getName().equals(userId)) return true;
        }
        return false;
    }


    // Problem 2-1 bid

    // Problem 2-1-2: isValidCourse
    private boolean isValidCourse(int courseId){
        List<Course> courses = retrieveCourseInfo();
        for (Course course:courses){
            if (course.courseId == courseId) return true;
        }
        return false;
    }

    // Problem 2-1-3: isValidMileage
//    private int isValidMileage(int mileage, User user){
//        int error = ErrorCode.SUCCESS;
//        if (mileage + user.totalBid() > Config.MAX_MILEAGE) error = ErrorCode.OVER_MAX_MILEAGE;
//        if (mileage > Config.MAX_MILEAGE_PER_COURSE) error = Math.min(error, ErrorCode.OVER_MAX_COURSE_MILEAGE);
//        if (mileage < 0) error = Math.min(error, ErrorCode.NEGATIVE_MILEAGE);
//
//        return error;
//    }

    // Problem 3-1-1: select students who will be registered
    private Map<Course, List<User>> selectStudents(){
        // 3-1-1-1: scan all courses(reuse 1-1)
        // 3-1-1-2: scan all users
        // 3-1-1-3: collectAllBiddings
        // 3-1-1-4: registerAllStudens
        List<User> users = retrieveAllUsers();
        List<Course> courses = retrieveCourseInfo();

        return null;
    }


    // Problem 3-1-1-1: scan all courses(reuse 1-1)

    // Problem 3-1-1-2: scan all users
    private List<User> retrieveAllUsers(){
        List<User> users = new ArrayList<>();
        File userPathFile = new File(userPath);
        File[] userList = userPathFile.listFiles(File::isDirectory);
        if (userList == null) return null;
        try {
            for (File userFile:userList){
                if (userFile.isDirectory()) {
                    User user = new User(userFile.getName());
                    users.add(user);
                }
            }
        }
        catch (FileNotFoundException e){
                // dno nothing
        }
        return users;
    }

    // Probelm 3-1-1-3: Collect all biddings bet to each course
    private Map<Course, List<Pair<User, Integer>>> collectAllBiddings(List<Course> courses, List<User> users){
        Map<Course, List<Pair<User, Integer>>> courseIdBiddings = new HashMap<>();
        for (Course course:courses){
            courseIdBiddings.put(course, collectBiddings(users, course));
        }
        return courseIdBiddings;
    }

    // Problem 3-1-1-3-1: collect all Biddings for a course
    private List<Pair<User, Integer>> collectBiddings(List<User> users, Course course){
        List<Pair<User, Integer>> courseBiddings = new ArrayList<>();
        for (User user:users){
            for (Bidding bidding:user.biddings()){
                if (bidding.courseId == course.courseId){
                    courseBiddings.add(new Pair<>(user, bidding.mileage));
                }
            }
        }
        courseBiddings.sort(new RegisterComparator());

        return courseBiddings;
    }

    static class RegisterComparator implements Comparator<Pair<User, Integer>>{
        @Override
        public int compare(Pair<User, Integer> o1, Pair<User, Integer> o2) {
            if (o1.value == o2.value) {
                if (o1.key.totalBid() == o2.key.totalBid()){
                    return o1.key.userId().compareTo(o2.key.userId());
                } else{
                    return Integer.compare(o1.key.totalBid(), o2.key.totalBid());
                }
            }
            return Integer.compare(o2.value, o1.value);
        }
    }



    // Problem 3-1-1-4: Register students for all clases
    private void registerAllStudents(List<Course> courses, List<User> users) throws IOException{
        Map<Course, List<Pair<User, Integer>>> courseBiddings = collectAllBiddings(courses, users);
        for (Course course:courseBiddings.keySet()){
            int quota = course.quota;
            if (course.quota < courseBiddings.get(course).size()){
                for (int i = 0; i < course.quota; i++){
                    User user = courseBiddings.get(course).get(i).key;
                    user.updateRegistrationStatus(course, true);
                }
                for (int i = 0; i < courseBiddings.get(course).size();i++){
                    User user = courseBiddings.get(course).get(i).key;
                    user.updateRegistrationStatus(course, false);
                }
            } else {
                for (int i = 0; i < courseBiddings.get(course).size(); i++){
                    User user = courseBiddings.get(course).get(i).key;
                    user.updateRegistrationStatus(course, true);
                }
            }
        }
    }






    // FOR TESTS
    private void printCourse(Course course){
        System.out.printf("(Dept) " + course.department + "|");
        System.out.printf("(Degree) " + course.academicDegree + "|");
        System.out.printf("(Ay) " + course.academicYear + "|");
        System.out.printf("(Name) " + course.courseName + "|");
        System.out.printf("(Credit) " + course.credit + "|");
        System.out.printf("(Loc) " + course.location + "|");
        System.out.printf("(Instr)" + course.instructor + "|");
        System.out.printf("(Quota)" + course.quota + "|");
        System.out.println();
    }

    private void printCourses(List<Course> courses){
        for (Course course:courses){
            printCourse(course);
        }
    }






}