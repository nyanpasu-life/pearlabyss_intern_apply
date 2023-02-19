package com.example.pinball.Engine.Network;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class NetInputSet {


    LinkedHashMap LocalInput = new LinkedHashMap<>();
    LinkedHashMap IngInput= new LinkedHashMap<>();
    LinkedHashMap PacedInput= new LinkedHashMap<>();


    public LinkedHashMap takeOutLocal() {
        backupLocal(LocalInput);
        LinkedHashMap tempPointer = LocalInput;
        LocalInput = new LinkedHashMap();
        return tempPointer;
    }

    private void backupLocal(LinkedHashMap localInput) { //TODO 구현 필요
    }

    public LinkedHashMap takeOutPacing() {
        backupPacing(IngInput);
        LinkedHashMap tempPointer = IngInput;
        IngInput = new LinkedHashMap();
        return tempPointer;
    }

    private void backupPacing(LinkedHashMap ingInput) {
    }


    public LinkedHashMap takeOutPaced_nullBefore() {
        backupPaced(PacedInput);
        LinkedHashMap tempPointer = PacedInput;
        PacedInput = null;
        return tempPointer;
    }

    private void backupPaced(LinkedHashMap pacedInput) {
    }

    public void mergePacing(LinkedHashMap addList){
        IngInput.putAll(addList);
    }


    public LinkedHashMap getPacedInput(){
        return PacedInput;
    }
    public LinkedHashMap getLocalInput() {
        return LocalInput;
    }
}
