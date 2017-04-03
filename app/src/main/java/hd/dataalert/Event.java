package hd.dataalert;

/**
 * Created by Hayde on 03-Apr-17.
 */

public class Event {
    private int id;
    private String status;
    private String date;
    private String description;

    public Event(){
    }

    public Event(int id, String status, String date, String description){
        this.id = id;
        this.status = status;
        this.date = date;
        this.description = description;
    }

    public Event(String status,String date, String description){
        this.status = status;
        this.date = date;
        this.description = description;
    }
    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
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
