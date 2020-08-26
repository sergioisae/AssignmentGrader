package nfp121.Controller;

import nfp121.Model.SystemModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import nfp121.View.CareTaker;
import nfp121.View.SystemView;

public class SystemController {

    static public SystemView theView;
    static public SystemModel theModel;
    public CareTaker CareTaker = new CareTaker();
    int saveFiles = 0, currentArticle = 0;
    static int idNum = 0;
    static int selectedId = 1;
    static int numberOfCriterias=1;
    static int idValue=0;
    static int idSelected= 0;
    

    public SystemController(SystemView theView, SystemModel theModel, CareTaker caretaker) {
        this.theView = theView;
        this.theModel = theModel;
        this.CareTaker = caretaker;

        // Tell the SystemView that when ever the search button
        // is clicked to execute the actionPerformed method
        // in the SaveButtonSystemListener inner class
        
        this.theView.addSaveButtonSystemListener(new SaveButtonSystemListener());
        this.theView.addGradeButtonSystemListener(new GradeButtonSystemListener());
        this.theView.addAddTabButtonSystemListener(new AddTabButtonSystemListener());
        this.theView.addSearchButtonSystemListener(new SearchButtonSystemListener());
        this.theView.addUndoButtonSystemListener(new UndoButtonSystemListener());
        this.theView.addRedoButtonSystemListener(new RedoButtonSystemListener());
        this.theView.addCriteriaBoxSystemListener(new CriteriaBoxSystemListener());
        this.theView.addDeleteButtonSystemListener(new DeleteButtonSystemListener());
    }

    class SaveButtonSystemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String DescriptionTextField, KeywordsTextArea, JokerTextField, Joker, isJoker = "";

            // Surround interactions with the view with
            // a try block in case data isn't
            // properly entered
            
            try {

                //criteria[id,description,Keywords,joker,operator,isJoker,typeFile)
//                DirectoryTextField = theView.getDirectoryLocation();
                KeywordsTextArea = theView.getKeywords();
                JokerTextField = theView.getJoker();
                DescriptionTextField = theView.getDescription();
                Joker = theView.getJoker();
                isJoker = String.valueOf(theView.isJoker());

//                if (!theView.isJoker()) {
//                    Joker = "";
//                }
                boolean keywordEmpty = KeywordsTextArea.isEmpty();
                boolean descriptionEmpty = DescriptionTextField.isEmpty();

                if (keywordEmpty || descriptionEmpty) {
                    theView.displayErrorMessage("All fields are mandatory");

                } else {
                    idSelected = (int) Integer.parseInt( theView.getCriteriaBox().getSelectedItem().toString().split("#")[1]);//get selected id
                    theView.getCriteriaBox().removeActionListener(new CriteriaBoxSystemListener());
                    String[] criteria = {String.valueOf(idSelected), DescriptionTextField, KeywordsTextArea, Joker, "", isJoker, ""};
                    theModel.saveArrToCriteriaList(criteria,idSelected-1);
                    theView.displayMessage("Saved and added to list" +idSelected);
                    

                    theView.getCriteriaBox().addActionListener(new CriteriaBoxSystemListener());
                }
//                theView.setSystemSolution(theModel.getKeyWords());;

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("no bhere");
                theView.displayErrorMessage(ex.toString());

            }
        }
    }

    class GradeButtonSystemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String DirectoryTextField = "";

            DirectoryTextField = theView.getDirectoryLocation();
            try {

                if (DirectoryTextField.isEmpty()) {
                    theView.displayErrorMessage("directroy is invalid");
                } else {

                    // Criteria = theModel.getCriteriaList();
                    String s = theModel.Run(DirectoryTextField);
                    theView.displayMessage(s);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex);
                theView.displayMessage(ex.toString());

            }
        }
    }

    class AddTabButtonSystemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
           
             numberOfCriterias++;
             theView.getCriteriaBox().addItem("Criteria #" + numberOfCriterias);
             theModel.addEmptyToCriteriaList();
             
                    int textInTextArea = theView.getCriteriaBoxLastValue();
                    // Set the value for the current memento
                    theModel.set(textInTextArea);
                    // Add new article to the ArrayList
                    CareTaker.addMemento(theModel.storeInMemento());
                    // saveFiles monitors how many articles are saved
                    // currentArticle monitors the current article displayed
                    saveFiles++;
                    currentArticle++;
                    System.out.println("Save Files " + saveFiles);
                    // Make undo clickable
                    theView.UndoEnableTrue();

        }
    }
    class DeleteButtonSystemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try{
                idSelected = (int) Integer.parseInt( theView.getCriteriaBox().getSelectedItem().toString().split("#")[1]);
                String []toReplace={String.valueOf(idSelected-1), "", "", "%", "", "", ""};
               // theView.displayMessage("Deleting "+idSelected + theModel.getCriteriaList().get(idSelected-1)[0]);
           theModel.getCriteriaList().set(idSelected-1, toReplace);
           theView.getKeywordsTextArea().setText("");
           theView.getDescriptionTextField().setText("");
             
            }catch(Exception ex){
                theView.displayMessage("You can't delete that");
                 System.out.println(selectedId);
            }
        }
    }
    class SearchButtonSystemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getCurrentDirectory();
            String filename = f.getAbsolutePath();
            theView.setDirectoryLocation(filename);

        }
    }

    class UndoButtonSystemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if (currentArticle >= 1) {
                // Decrement to the current article displayed
                currentArticle--;
                // Get the older article saved and display it in JTextArea
                int textBoxString = theModel.restoreFromMemento(CareTaker.getMemento(currentArticle));
//                theView.setArticle(textBoxString);
                theView.setComboBoxR(textBoxString);
                // Make Redo clickable
                theView.RedoEnableTrue();
            } else {
                // Don't allow user to click Undo
                theView.UndoEnableFalse();
            }

        }
    }

    class RedoButtonSystemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if ((saveFiles - 1) > currentArticle) {
                // Increment to the current article displayed
                currentArticle++;
                // Get the newer article saved and display it in JTextArea
                int textBoxString = theModel.restoreFromMemento(CareTaker.getMemento(currentArticle));

//                theView.setArticle(textBoxString);
                theView.setComboBoxA(textBoxString);
                // Make undo clickable
                theView.UndoEnableTrue();
            } else {
                // Don't allow user to click Redo
                theView.RedoEnableFalse();
            }

        }
    }

    class CriteriaBoxSystemListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
//            if (idNum == 1) {//to skip the first add
//                System.out.println("outisde the loop");
//            } else {

               
                Object selected = theView.getCriteriaBox().getSelectedItem();
                System.out.println("Before the selected  " + selected.toString());
                selectedId = (int) Integer.parseInt(selected.toString().split("#")[1]);
               if(selectedId!=0){idNum=selectedId;idNum--;}
                 System.out.println(selectedId+" accesing that id");
                //idNum=selectedId;//getting the idea for the save list
                theView.getDescriptionTextField().setText(theModel.getCriteriaList().get(idNum)[1]);
                theView.getKeywordsTextArea().setText(theModel.getCriteriaList().get(idNum)[2]);;
                for(String s :theModel.getCriteriaList().get(idNum))
                System.out.println(s);
                theView.getJokerTextField().setText(theModel.getCriteriaList().get(idNum)[3]);
                if ((theModel.getCriteriaList().get(idNum)[5]) == "false") {
                    theView.getJokerRadioButtonNo().setSelected(true);
                } else {
                    theView.getJokerRadioButtonNo().setSelected(false);
                }
            }
        }
    }
//}
