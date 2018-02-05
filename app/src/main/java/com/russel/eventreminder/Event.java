package com.russel.eventreminder;

/**
 * Created by Russel on 1/9/2018.
 */

public class Event {
    private int id;
    private String eventName;
    private String eventDate;
    private String eventDescription;
    private String eventReminder;
    private String eventRepeat;
    private byte[] eventImage;


    public Event(int id, String eventName, String eventDate, String eventDescription, String eventReminder, String eventRepeat, byte[] eventImage) {
        this.id = id;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventReminder = eventReminder;
        this.eventRepeat = eventRepeat;
        this.eventImage = eventImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventReminder() {
        return eventReminder;
    }

    public void setEventReminder(String eventReminder) {
        this.eventReminder = eventReminder;
    }

    public String getEventRepeat() {
        return eventRepeat;
    }

    public void setEventRepeat(String eventRepeat) {
        this.eventRepeat = eventRepeat;
    }

    public byte[] getEventImage() {
        return eventImage;
    }

    public void setEventImage(byte[] eventImage) {
        this.eventImage = eventImage;
    }
}
