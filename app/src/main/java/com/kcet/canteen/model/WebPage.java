package com.kcet.canteen.model;

public class WebPage {

    private int id;
    private int icon;
    private String title;
    private String pageUrlAddress;

    public WebPage(int id, int icon, String title, String pageUrlAddress) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.pageUrlAddress = pageUrlAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public String getPageUrlAddress() {
        return pageUrlAddress;
    }

}
