package user;

import course.*;
import server.*;
import utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class User {

    private static String USER_DIR = "data/Users/";

    private String userId;
    private List<Bidding> biddings;
    private List<Course> registeredCourses;
    private int totalBid;

    public String userId() {
        return userId;
    }

    public List<Bidding> biddings() {
        return biddings;
    }

    public List<Course> registeredCourses() {
        return registeredCourses;
    }

    public int totalBid() {
        return totalBid;
    }

    public User(String userId) throws FileNotFoundException {
        this.userId = userId;
        this.totalBid = 0;
        this.biddings = retrieveBids();
        this.registeredCourses = new ArrayList<>();
    }


    // Problem 2-2-3: retrieve bid.txt file (utilize User class)
    public List<Bidding> retrieveBids() throws FileNotFoundException {

        List<Bidding> biddings = new ArrayList<>();
        File userPath = new File(USER_DIR + userId + "/bid.txt");
        Scanner scanner = new Scanner(userPath);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] info = line.split("\\|");
            int courseId = Integer.parseInt(info[0]);
            int mileage = Integer.parseInt(info[1]);
            totalBid += mileage;
            biddings.add(new Bidding(courseId, mileage));
        }

        return biddings;
    }


    // Problem 2-1-4: 1) renew biddings 2) renew bid.txt 3) update totalBid
    public void makeBid(int courseId, int mileage) throws FileNotFoundException, IOException {

        // System.out.println("BID CONDITION " + courseId + "|" + mileage);

        biddings = retrieveBids();

        List<Bidding> afterBidding = new ArrayList<>();

        for (Bidding bidding : biddings) {
            if (bidding.courseId != courseId) afterBidding.add(bidding);
        }
        if (mileage != 0) afterBidding.add(new Bidding(courseId, mileage));

        biddings = afterBidding;
        recordBids();


    }

    public int expectedMileage(int courseId, int mileage) {
        int sum = 0;
        for (Bidding bidding:biddings){
            if (bidding.courseId != courseId) sum += bidding.mileage;
        }
        sum += mileage;
        return sum;
    }



    private void recordBids() throws IOException {
        String fName = USER_DIR + userId + "/bid.txt";
        FileWriter fileWriter = new FileWriter(fName, false);
        if (biddings.size() != 0){
            for (Bidding bidding : biddings) {
                String record;
                if (biddings.get(biddings.size()-1).equals(bidding)){
                    record = bidding.courseId + "|" + bidding.mileage;
                } else {
                    record = bidding.courseId + "|" + bidding.mileage + "\n";
                }
                fileWriter.write(record);
            }
        }
        fileWriter.close();
    }

    // problem 3-1-2-1-1: update user registration status
    public void updateRegistrationStatus(Course course, boolean success) throws IOException{

        File file = new File(USER_DIR + userId + "/registration.txt");
        retrieveBids();
        if (file.isFile()) retrieveRegisteredCourse();
        if (success){
            if (registeredCourses == null){
                registeredCourses = new ArrayList<>();
            }
            registeredCourses.add(course);
        }
        biddings.removeIf(bidding -> bidding.courseId == course.courseId);
        recordBids();
        recordRegistration();


    }

    private void recordRegistration() throws IOException {
        String fName = USER_DIR + userId + "/registration.txt";
        FileWriter fileWriter = new FileWriter(fName, false);
        if (registeredCourses != null && registeredCourses.size() != 0){
            for (Course course : registeredCourses) {
                String record;
                if (registeredCourses.get(registeredCourses.size()-1).equals(course)){
                    record = course.courseId + "|"+ course.college +"|"+ course.department+"|"+course.academicDegree
                            +"|"+ course.academicYear +"|"+ course.courseName +"|"+ course.credit
                            +"|"+course.location +"|"+ course.instructor +"|"+ course.quota;
                } else {
                    record = course.courseId + "|"+ course.college +"|"+ course.department+"|"+course.academicDegree
                            +"|"+ course.academicYear +"|"+ course.courseName +"|"+ course.credit
                            +"|"+course.location +"|"+ course.instructor +"|"+ course.quota + "\n";
                }

                fileWriter.write(record);
            }
        }
        fileWriter.close();
    }

    // problem 3-2: retrieveRegisteredCourse <- USER CLASS
    public List<Course> retrieveRegisteredCourse() throws IOException{
        List<Course> courses = new ArrayList<>();

        String fName = USER_DIR + userId + "/registration.txt";
        File userPath = new File(fName);
        Scanner scanner = new Scanner(userPath);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] info = line.split("\\|");
            Course course = new Course(Integer.parseInt(info[0]), info[1], info[2], info[3], Integer.parseInt(info[4]),
                    info[5], Integer.parseInt(info[6]), info[7], info[8], Integer.parseInt(info[9]));
            courses.add(course);
        }

        registeredCourses = courses;
        return courses;
    }

    public void printBiddingList() {
        if (biddings == null) return;
        System.out.println();
        for (Bidding bidding : biddings) {
            printBidding(bidding);
        }
        System.out.println();
    }

    public void printBidding(Bidding bidding) {
        System.out.println(bidding.courseId + "|" + bidding.mileage);

    }
}
