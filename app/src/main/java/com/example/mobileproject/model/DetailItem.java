package com.example.mobileproject.model;

public class DetailItem {
    private String title;
    private String contents;

    private  String downloadUrl;

    public DetailItem(){

    }

    public DetailItem(String title, String contents, String downloadUrl) {
        this.title = title;
        this.contents = contents;
        this.downloadUrl = downloadUrl;
    }

    public DetailItem(String title, String contents){
        String downloadUrl =
                "https://firebasestorage.googleapis.com/v0/b/mobileproject-e978a.appspot.com/o/Chrysanthemum.jpg?alt=media&token=e9570d16-8569-4f43-9d54-0fb68c9e6391";
        new DetailItem(title, contents, downloadUrl);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }



    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CardItem{");
        sb.append("title='").append(title).append('\'');
        sb.append(", contents='").append(contents).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
