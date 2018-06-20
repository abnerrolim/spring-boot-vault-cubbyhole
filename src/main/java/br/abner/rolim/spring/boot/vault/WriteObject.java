package br.abner.rolim.spring.boot.vault;

public class WriteObject {

    private final String word;

    public WriteObject(String word){
        this.word = word;
    }

    public String getWord(){
        return this.word;
    }

}
