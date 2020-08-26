package nfp121.Model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JComboBox;
import nfp121.View.Memento;
import nfp121.View.SystemView;
// The Model performs all the calculations needed
// and that is it. It doesn't know the View
// exists

public class SystemModel {
// Holds the value of the data
// entered in the view

    private ArrayList<String[]> st = null;
    private static int article ;  // Sets the value for the article
    public static List<String> allFiles = new ArrayList<String>();
    public static List<String> txtFiles = new ArrayList<String>();
    public static List<String> javaFiles = new ArrayList<String>();
    public static SystemView view = new SystemView();

    public void saveArrToCriteriaList(String[] criteria, int n) {
        if (CriteriaListSingelton.getInstance().getArray().isEmpty()) {
            addEmptyToCriteriaList();
        }
        try {
            CriteriaListSingelton.getInstance().getArray().set(n, criteria);
            // CriteriaListSingelton.getInstance().addToArray(criteria);
        } catch (Exception e) {
            System.out.print(e);
        }
        //String[] criteria ={ DescriptionTextField , KeywordsTextArea , JokerTextField};
    }

    public void addEmptyToCriteriaList() {
        //criteria[id,description,Keywords,joker,operator,isJoker,typeFile)
        String[] toAdd = {"", "", "", "%", "", "", ""};
        CriteriaListSingelton.getInstance().addToArray(toAdd);
    }

    public List<String[]> getCriteriaList() {
        return CriteriaListSingelton.getInstance().getArray();
    }


    public String Run(String DirectoryTextField) {
        String s = "";
        List<String[]> list = new ArrayList<String[]>();
        for (int i = 0; i < getCriteriaList().size(); i++) {

            if (getCriteriaList().get(i)[1] != "") {
                list.add(getCriteriaList().get(i));
            }

        }
//            List<String> filteredList = getCriteriaList().newArrayList(getCriteriaList().filter(
//    list, Predicates.containsPattern("How")));
        if (list.isEmpty()) {
            s = "The criteria list is empty!!!";
        } else {
            Boolean[] bool = runAllFiles(list, DirectoryTextField);

//        for (Boolean a : bool) {
//            System.out.println(a);
//        }
            int i = 0;

            for (Boolean a : bool) {
             String ss="Not Available in Files";
                if (a) ss ="Available in Files";
                s += list.get(i)[2] + " : " + ss + "\n";
                i++;
            }
        }
        return s;
    }

    public static String makeFileString(String path) {
        String s = "";
        try {
            s = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.out.println(path);

            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("problem here");

            e.printStackTrace();
        }
        return s;
    }

    public static List getDirectory(String path, String ext) {
        //get directory of all .ext files for search
        List<String> Directories = new ArrayList<>();
        try ( Stream<Path> walk = Files.walk(Paths.get(path))) {

            Directories = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(ext)).collect(Collectors.toList());

//	return Directories;
        } catch (Exception e) {
            System.out.println("bad directory");

            e.printStackTrace();
        }

        return Directories;
    }

    public static String[] splitKeywordsWithJoker(String keywords, String joker) {
        return keywords.split(joker);

    }

    public static boolean wildCardSearch(String s, String[] keywords) {

        Queue<String> q = new LinkedList<String>();

        for (String a : keywords) {
            q.offer(a);
        }

        // String[] words = s.split("\\W+");
        //System.out.println("inside words"+ );
        int lastIndex = 0;
        for (int i = 0; i < s.length(); i++) {
            if (q.isEmpty()) {
                return true;
            } else {
                String element = q.peek();
                lastIndex = s.indexOf(element, lastIndex);
                if (lastIndex < 0) {
                    return false; //that means the last ine didnt find it
                }
                q.poll();
            }
        }
        if (q.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean searchForCriteria(String[] criteria, String path) {
        //criteria[id,description,Keywords,joker,operator,isJoker,typeFile)
        //only run it on the file that suits its typeFile
        boolean isJoker = Boolean.parseBoolean(criteria[5]);
        Context context = isJoker ? new Context(new SearchCriteriaWithJoker()) : new Context(new SearchCriteria());
        return context.executeStrategy(criteria, path);

    }

    public static Boolean[] scoreForFile(List<String[]> criterias, String path) { //a list of string arrays 
        Boolean[] score = new Boolean[criterias.size()];
        for (int i = 0; i < criterias.size(); i++) {
            score[i] = searchForCriteria(criterias.get(i), path);
        }
        return score;
    }

    public static Boolean[] runAllFiles(List<String[]> criterias, String path) {

        //List<String[]> criterias = criteria.stream().collect(Collectors.toList());//turning it to list because of changes
        Boolean[] all = new Boolean[criterias.size()];
        Arrays.fill(all, Boolean.FALSE);
        try {
            List<String> txtFiles = getDirectory(path, "txt");
            List<String> javaFiles = getDirectory(path, "java");
            allFiles = Stream.concat(txtFiles.stream(), javaFiles.stream())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.out.print("fired here");
            e.printStackTrace();

        }
        //run score for file on allfiles
        for (String filePath : allFiles) {
            Boolean[] single = scoreForFile(criterias, filePath);
//make a function to check if already true dont change if not do change to true 
            for (int i = 0; i < all.length || i < single.length; i++) {
                if (single[i] == true) {
                    all[i] = true;
                } else {
                    if (all[i] == true) {
                        continue;
                    }
                    continue;
                }
            }
        }

        return all;
    }

    public void set(int newArticle) {
        System.out.println("From Originator: Current Version of Article\n" + newArticle + "\n");
         this.article = newArticle;
    }     // Creates a new Memento with a new article

    public Memento storeInMemento() {
//        System.out.println("From Originator: Saving to Memento");
        return new Memento(article);
    }   // Gets the article currently stored in memento

    public int restoreFromMemento(Memento memento) {
        article = memento.getSavedArticle();
//        System.out.println("From Originator: Previous Article Saved in Memento\n"+st + "\n");
        return article;
    }

}
