package cpta;

import cpta.environment.Compiler;
import cpta.environment.Executer;
import cpta.exam.ExamSpec;
import cpta.exam.Problem;
import cpta.exam.Student;
import cpta.exam.TestCase;
import cpta.exceptions.CompileErrorException;
import cpta.exceptions.FileSystemRelatedException;
import cpta.exceptions.InvalidFileTypeException;
import cpta.exceptions.RunTimeErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Grader {
    Compiler compiler;
    Executer executer;

    public Grader(Compiler compiler, Executer executer) {
        this.compiler = compiler;
        this.executer = executer;
    }

    public Map<String,Map<String, List<Double>>> gradeSimple(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-1
        Map<String, Map<String, List<Double>>> result = new HashMap<>();
        List<Problem> problems = examSpec.problems;
        List<Student> students = examSpec.students;

        return markAllSimple(students, problems, submissionDirPath);
    }

    private String getStudentDirSimple(Student student, String submissionDirPath){
        File path = new File(submissionDirPath);
        File[] subDirs = path.listFiles(File::isDirectory);
        if (subDirs == null) return null;
        for (File subDir:subDirs){
            if (subDir.getName().equals(student.id)) return subDir.getPath();
        }
        return null;
    }


    // NULL MAKING FUNCTION
    // return null when problem direcotry does not exist under student dir
    private String getProblemDirSimple(Student student, Problem problem, String submissionDirPath){
        String studentDirString = getStudentDirSimple(student, submissionDirPath);
        if (studentDirString == null) return null;
        File studentDir = new File(studentDirString);
        File[] pDirs = studentDir.listFiles(File::isDirectory);
        if (pDirs == null) return null;
        for (File dir:pDirs){
            if (dir.getName().equals(problem.id)) return dir.getPath();
        }
        return null;
    }

    private String getTargetFilePathSimple(Student student, Problem problem, String submissionDirPath){
        String problemDirString = getProblemDirSimple(student, problem, submissionDirPath);
        if (problemDirString == null) return null;
        File problemDir = new File(problemDirString);
        if (!problemDir.isDirectory()) return null;
        File[] files = problemDir.listFiles(File::isFile);
        if (files == null || files.length == 0) return null;
        for (File file:files){
            if (file.getName().equals(problem.targetFileName)) {
                return file.getPath();
            }
        }
        return null;
    }

    private void executeTargetFileSimple(Student student,Problem problem,TestCase testCase, String submissionDirPath)
            throws InvalidFileTypeException, FileSystemRelatedException, RunTimeErrorException {
        // System.out.println(student.id + "targetFilePathString  " + targetFilePathString);
        String targetFilePathString = getTargetFilePathSimple(student, problem, submissionDirPath);
        if (targetFilePathString == null) return;
        targetFilePathString = targetFilePathString.replace(".sugo", ".yo");
        File targetFile = new File(targetFilePathString);
        if (!targetFile.isFile()) return;
        String targetIn = (new File(problem.testCasesDirPath, testCase.inputFileName)).getPath();
        String targetOut = (new File(targetFile.getParent(), testCase.outputFileName)).getPath();
        executer.execute(targetFilePathString, targetIn, targetOut);
    }

    private double markTestCaseSimple(Student student, Problem problem, TestCase testCase, String submissionDirPath){
        String targetFilePathString = getTargetFilePathSimple(student, problem, submissionDirPath);
        if (targetFilePathString == null || !(new File(targetFilePathString).isFile())) return 0.0;
        try {
            executeTargetFileSimple(student, problem, testCase, submissionDirPath);
            File targetFile = new File(targetFilePathString);
            String targetIn = (new File(problem.testCasesDirPath, testCase.inputFileName)).getPath();
            String targetOut = (new File(targetFile.getParent(), testCase.outputFileName)).getPath();
            String testOut = (new File(problem.testCasesDirPath, testCase.outputFileName)).getPath();
            if (isCorrectOut(targetOut,testOut)) return testCase.score;
            else return 0.0;
        } catch (RunTimeErrorException | FileSystemRelatedException
                | InvalidFileTypeException | IOException e) {
            return 0.0;
        }

    }

    private List<Double> markProblemSimple(Student student, Problem problem, String submissionDirPath){
        String targetFilePathString = getTargetFilePathSimple(student, problem, submissionDirPath);
        if (targetFilePathString == null || !(new File(targetFilePathString).isFile())) return markProblemZero(problem);
        try {
            compiler.compile(targetFilePathString);
            Map<String, Double> testIdScore = new HashMap<>();
            List<String> testId = new ArrayList<>();
            for (TestCase testCase:problem.testCases){
                testIdScore.put(testCase.id, markTestCaseSimple(student, problem, testCase, submissionDirPath));
                testId.add(testCase.id);
            }

            Collections.sort(testId);
            List<Double> scores = new ArrayList<>();
            for (String id:testId){
                scores.add(testIdScore.get(id));
            }
            return scores;

        } catch (CompileErrorException | InvalidFileTypeException | FileSystemRelatedException compileErrorException) {
            return markProblemZero(problem);
        }
    }

    private Map<String, List<Double>> markStudentSimple(Student student, List<Problem> problems, String submissionDirPath){
        Map<String, List<Double>> studentScore = new HashMap<>();
        for (Problem problem:problems){
            studentScore.put(problem.id, markProblemSimple(student, problem, submissionDirPath));
        }
        return studentScore;
    }

    private Map<String, Map<String, List<Double>>> markAllSimple(List<Student> students, List<Problem> problems, String submissionDirPath){
        Map<String, Map<String, List<Double>>> result = new HashMap<>();
        for (Student student:students){
            result.put(student.id, markStudentSimple(student, problems, submissionDirPath));
        }
        return result;
    }


    public boolean isCorrectOut(String OUTfilePath, String testOUTFilePath) throws IOException {
        Path outPath = Path.of(OUTfilePath);
        Path testPath = Path.of(testOUTFilePath);

        String out = Files.readString(outPath);
        String test = Files.readString(testPath);

        return out.equals(test);
    }



    public Map<String,Map<String, List<Double>>> gradeRobust(ExamSpec examSpec, String submissionDirPath) {
        // TODO Problem 1-2
        List<Problem> problems = examSpec.problems;
        List<Student> students = examSpec.students;
        return markAllRobust(students, problems, submissionDirPath);
    }


    // creates null
    private String removeTrailingWS(String text){
        if (text == null) return null;
        int len = text.length();
        for (int i = len; i > 0; len--){
            if (!isWhiteSpace(text.charAt(len-1)))
                break;
        }
        return text.substring(0, len);
    }

    private boolean isWhiteSpace(char ch){
        if (Character.compare(ch, ' ') == 0
                || Character.compare(ch, '\t') == 0
                || Character.compare(ch, '\n') == 0) return true;
        else return false;
    }

    private String ignoreWhiteSpace(String text){
        if (text == null) return null;
        text = text.replaceAll(" ", "");
        text = text.replaceAll("\t", "");
        text = text.replaceAll("\n", "");
        return text;
    }

    private boolean compareTextRobust(String text, String answer, Set<String> judgingTypes){

        if (judgingTypes == null || judgingTypes.isEmpty())
            return text.equals(answer);
        if (judgingTypes.contains(Problem.TRAILING_WHITESPACES)){
            text = removeTrailingWS(text);
            answer = removeTrailingWS(answer);
        }
        if (judgingTypes.contains(Problem.IGNORE_WHITESPACES)){
            text = ignoreWhiteSpace(text);
            answer = ignoreWhiteSpace(answer);
        }
        if (judgingTypes.contains(Problem.CASE_INSENSITIVE)){
            return text.equalsIgnoreCase(answer);
        }
//        System.out.printf("[");
//        System.out.println(text); //RMV
//        System.out.println("]");
//        System.out.printf("[");
//        System.out.printf(answer); // RMV
//        System.out.println("]");
        return text.equals(answer);
    }

    private String retrieveContent(String filePath) throws IOException {
        Path path = Path.of(filePath);
        return Files.readString(path);
    }



    // NULL MAKING FUNCTION
    // no folders under submissionDirPath -> null
    private String getStudentDirRobust(Student student, String submissionDirPath){
        File path = new File(submissionDirPath);
        File[] subDirs = path.listFiles(File::isDirectory);
        if (subDirs == null) return null;
        for (File subDir:subDirs){
            if (subDir.getName().startsWith(student.id)) return subDir.getPath();
        }
        return null;
    }


    // NULL MAKING FUNCTION
    // return null when problem direcotry does not exist under student dir
    private String getProblemDirRobust(Student student, Problem problem, String submissionDirPath){
        String studentDirString = getStudentDirRobust(student, submissionDirPath);
        if (studentDirString == null) return null;
        File studentDir = new File(studentDirString);
        File[] pDirs = studentDir.listFiles(File::isDirectory);
        if (pDirs == null) return null;
        for (File dir:pDirs){
            if (dir.getName().equals(problem.id)) return dir.getPath();
        }
        return null;
    }

    private void getSubDirFiles(Student student, Problem problem, String submissionDirPath) throws IOException{
        String problemDirString = getProblemDirRobust(student, problem, submissionDirPath);
        if (problemDirString == null) return;
        File problemDir = new File(problemDirString);
        File[] subDirs = problemDir.listFiles(File::isDirectory);

        // No subdirectories
        if (subDirs == null || subDirs.length == 0) return;
        // Subdirectory exists
        if (subDirs.length == 1) {
            moveFilestoParentFolder(subDirs[0].getPath());
        }

    }



    // THROWS IOEXCEPTION
    private void moveFilestoParentFolder(String childFolder) throws IOException{
        if (childFolder == null) return;
        File childFolderFile = new File(childFolder);
        File[] files = childFolderFile.listFiles(File::isFile);
        if (files != null && files.length != 0){
            for (File file:files){
                Path from = file.toPath();
                Path to = (new File(file.getParentFile().getParentFile(), file.getName())).toPath();
                Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void getWrapperFiles(Student student, Problem problem, String submissionDirPath) throws IOException{
        if (problem.wrappersDirPath == null) return;

        // get Problem Directory
        String problemDirString = getProblemDirRobust(student, problem, submissionDirPath);
        if (problemDirString == null) return;
        File problemDir = new File(problemDirString);
        if (!problemDir.isDirectory()) return;

        File wrappersDir = new File(problem.wrappersDirPath);
        if (!wrappersDir.isDirectory()) return;
        File[] wrapperFiles = wrappersDir.listFiles(File::isFile);
        if (wrapperFiles != null && wrapperFiles.length != 0){
            for (File wrapperFile:wrapperFiles){
                if (wrapperFile.getName().endsWith(".sugo")){
                    Path from = wrapperFile.toPath();
                    Path to = (new File(problemDir, wrapperFile.getName())).toPath();
                    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private void collectFilesBeforeCompile(Student student, Problem problem, String submissionDirPath) throws IOException {
        getSubDirFiles(student, problem, submissionDirPath);
        getWrapperFiles(student, problem, submissionDirPath);
    }

    private void compileNonTargetFiles(Student student, Problem problem, String submissionDirPath)
            throws InvalidFileTypeException, FileSystemRelatedException, CompileErrorException {
        String problemDirString = getProblemDirRobust(student, problem, submissionDirPath);
        if (problemDirString == null) return;
        File problemDir = new File(problemDirString);
        if (problemDir.isDirectory()){
            File[] files = problemDir.listFiles(File::isFile);
            for (File file:files){
                if (!file.getName().equals(problem.targetFileName) && file.getName().endsWith(".sugo"))
                    compiler.compile(file.getPath());
            }
        }
    }

    // null -> no submission
    private String getTargetFilePathRobust(Student student, Problem problem, String submissionDirPath){
        String problemDirString = getProblemDirRobust(student, problem, submissionDirPath);
        if (problemDirString == null) return null;
        File problemDir = new File(problemDirString);
        if (!problemDir.isDirectory()) return null;
        File[] files = problemDir.listFiles(File::isFile);
        if (files == null || files.length == 0) return null;
        for (File file:files){
            if (file.getName().equals(problem.targetFileName)) {
                return file.getPath();
            }
        }
        return null;
    }

    // true when submission is fine
    private String compileAllFiles(Student student, Problem problem, String submissionDirPath)
            throws IOException, InvalidFileTypeException, FileSystemRelatedException, CompileErrorException {
        collectFilesBeforeCompile(student, problem, submissionDirPath);
        String targetFilePath = getTargetFilePathRobust(student, problem, submissionDirPath);
        compileNonTargetFiles(student, problem, submissionDirPath);
        if (targetFilePath == null) return null;
        compiler.compile(targetFilePath);
        return targetFilePath.replace(".sugo", ".yo");
    }

    private boolean onlyYoFiles(Student student, Problem problem, String submissionDirPath){
        String problemDirString = getProblemDirRobust(student, problem, submissionDirPath);
        if (problemDirString == null) return false;
        File problemDir = new File(problemDirString);
        if (!problemDir.isDirectory()) return false;

        File[] files = problemDir.listFiles(File::isFile);
        List<String> YOs = new ArrayList<>();
        List<String> SUGOs = new ArrayList<>();

        if (files == null) return true;
        for (File file:files){
            String fileName = file.getName();
            if (fileName.endsWith(".sugo")) SUGOs.add(fileNameNoExtension(fileName));
            if (fileName.endsWith(".yo")) YOs.add(fileNameNoExtension(fileName));
        }

        for (String yoFile:YOs){
            if (!SUGOs.contains(yoFile)) {
                return true;
            }
        }

        return false;
    }

    private String fileNameNoExtension(String filename){
        int pos = filename.lastIndexOf(".");
        if (pos > 0) return filename.substring(0, pos);
        else return null;
    }

    private void executeTargetFile(Student student,Problem problem,TestCase testCase, String targetFilePathString)
            throws InvalidFileTypeException, FileSystemRelatedException, RunTimeErrorException {
        // System.out.println(student.id + "targetFilePathString  " + targetFilePathString);
        File targetFile = new File(targetFilePathString);
        if (!targetFile.isFile()) return;
        String targetIn = (new File(problem.testCasesDirPath, testCase.inputFileName)).getPath();
        String targetOut = (new File(targetFile.getParent(), testCase.outputFileName)).getPath();
        executer.execute(targetFilePathString, targetIn, targetOut);
    }


    private double markTestCaseRobust(Student student, Problem problem, TestCase testCase, String targetFilePathString){
        try {
            executeTargetFile(student, problem, testCase, targetFilePathString);
            File targetYoFile = new File(targetFilePathString);
            String studentOutPath = (new File(targetYoFile.getParent(), testCase.outputFileName)).getPath();
            String answerOutPath = (new File(problem.testCasesDirPath, testCase.outputFileName)).getPath();
            String studentOut = retrieveContent(studentOutPath);
            String answerOut = retrieveContent(answerOutPath);


            if (compareTextRobust(studentOut, answerOut, problem.judgingTypes)){
                return testCase.score;
            } else{
                return 0.0;
            }

        } catch (InvalidFileTypeException | FileSystemRelatedException | RunTimeErrorException | IOException e){
            return 0.0;
        }
    }

    private List<Double> markProblemRobust(Student student, Problem problem, String submissionDirPath) {
        String submissionCorrect = null;
        try {
            submissionCorrect = compileAllFiles(student, problem, submissionDirPath);
            if (submissionCorrect == null){
                String problemDirString = getProblemDirRobust(student, problem, submissionDirPath);
                if (problemDirString != null){
                    File filePath = new File(problemDirString, problem.targetFileName.replace(".sugo", ".yo"));
                    if (filePath.isFile()) submissionCorrect = filePath.getPath();
                }
            }
            boolean filesNotMatching = onlyYoFiles(student, problem, submissionDirPath);

            if (submissionCorrect == null) return markProblemZero(problem);
            else {
                List<Double> scores = new ArrayList<>();
                List<String> testId = new ArrayList<>();
                Map<String, Double> testIdScore = new HashMap<>();
                for (TestCase testCase: problem.testCases){
                    testId.add(testCase.id);
                    if (filesNotMatching) {
                        testIdScore.put(testCase.id, 0.5* markTestCaseRobust(student, problem, testCase, submissionCorrect));
                    }
                    else testIdScore.put(testCase.id, markTestCaseRobust(student, problem, testCase, submissionCorrect));
                }
                Collections.sort(testId);
                for (String id:testId){
                    scores.add(testIdScore.get(id));
                }
                return scores;
            }
        } catch (IOException | InvalidFileTypeException | FileSystemRelatedException | CompileErrorException e) {
            return markProblemZero(problem);
        }
    }

    private List<Double> markProblemZero(Problem problem){
        List<Double> zeros = new ArrayList<>();
        for (TestCase testCase:problem.testCases){
            zeros.add(0.0);
        }
        return zeros;
    }

    private Map<String, List<Double>> markStudentRobust(Student student, List<Problem> problems, String submissionDirPath){
        Map<String, List<Double>> studentScore = new HashMap<>();
        for (Problem problem:problems){
            studentScore.put(problem.id, markProblemRobust(student, problem, submissionDirPath));
        }
        return studentScore;
    }

    private Map<String, Map<String, List<Double>>> markAllRobust(List<Student> students, List<Problem> problems, String submissionDirPath){
        Map<String, Map<String, List<Double>>> result = new HashMap<>();
        for (Student student:students){
            result.put(student.id, markStudentRobust(student, problems, submissionDirPath));
        }
        return result;
    }







}

