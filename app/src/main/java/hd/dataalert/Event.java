package hd.dataalert;

/**
 * Created by Hayde on 03-Apr-17.
 */

public class Event {
    private int id;
    private String date;
    private String description;

    public Event(){
    }

    public Event(int id, String date, String description){
        this.id = id;
        this.date = date;
        this.description = description;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
