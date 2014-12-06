/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.sphinx.demo.IVRSystem;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author eldoc
 */
public class Interface {
   /*
    
    Dialog
    begin(fields)
    freetts
    say = interact(kalam)
    
    
    IVRSystem
    parse
    ArrayList<Field>
    
    */
    
    
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter vxml file name to parse:");
           
        String vxmlFile;
        vxmlFile = sc.nextLine();
        vxmlFile = "dialog.vxml";
       
        Parser parser = new Parser(vxmlFile);
        ArrayList<Field> fields = null; 
        fields = parser.parse();
        
        VoiceManager vm = VoiceManager.getInstance();
        Voice voice = vm.getVoices()[2];
        voice.allocate();
        
        ConfigurationManager cm;
        cm = new ConfigurationManager(Interface.class.getResource("ivrsystem.config.xml"));

        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        Microphone microphone = (Microphone) cm.lookup("microphone");
        if (!microphone.startRecording()) {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            voice.deallocate();
            System.exit(1);
        }
        
        String toBeSpoken="";
        Dialog dialog = new Dialog(fields);
        //dialog.loadScript(parser.extractScript());
        toBeSpoken = dialog.begin();
        voice.speak(toBeSpoken);

        while(!dialog.isOver()){
            System.out.println(toBeSpoken);
            System.out.println("Enter 0 to answer using the Mic or type your answer:");
            toBeSpoken = sc.nextLine();
            if(toBeSpoken.trim().equals("0")){
                toBeSpoken = recognizer.recognize().getBestFinalResultNoFiller();;
            }
            toBeSpoken = dialog.interact(toBeSpoken);
            voice.speak(toBeSpoken);
        }
        voice.deallocate();
        recognizer.deallocate();
    }
}
