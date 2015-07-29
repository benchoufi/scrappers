package org.parser;

/**
 * Created by mehdibenchoufi on 24/06/15.
 */
public class ProjectConf {

    private int number_tags;
    private String[] toparse_tags;

    public int getNumber_tags() {
        return number_tags;
    }

    public void setNumber_tags(int number_tags) {
        this.number_tags = number_tags;
    }


    public String[] getToparse_tags() {
        return toparse_tags;
    }

    public void setToparse_tags(String[] toparse_tags) {
        this.toparse_tags = toparse_tags;
    }

    public ProjectConf(String[] stringArray){
        number_tags = stringArray.length;
        toparse_tags = new String[number_tags];
        for(int index=0;index<number_tags;index++){
            toparse_tags[index] = stringArray[index];
        }
    }
}
