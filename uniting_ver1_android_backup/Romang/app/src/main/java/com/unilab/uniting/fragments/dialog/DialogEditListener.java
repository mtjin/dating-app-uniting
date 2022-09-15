package com.unilab.uniting.fragments.dialog;


import java.util.ArrayList;

public interface DialogEditListener {
    void updateProfile(String from, String checkedProfile);
    void updatePersonality(ArrayList<String> checkedPersonalityList);
    void changeRepresentPhoto();
    void removePhoto();

}
