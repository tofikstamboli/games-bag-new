package com.example.nativeadinrecyclerview;

class MenuItem {

    private final String name;
    private final String url;
    private final String file_name;
    private final String imageName;
    private final String folderName;
    private final String discription;


    public MenuItem(String name,String discription, String url,String file_name,String folderName,
                    String imageName) {
        this.name = name;
        this.url = url;
        this.imageName = imageName;
        this.file_name = file_name;
        this.folderName = folderName;
        this.discription = discription;
    }

    public String getName() {
        return name;
    }

    public String getDiscription() {
        return discription;
    }

    public String getUrl() {
        return url;
    }

    public String getFile_name(){return file_name;}

    public String getImageName() {
        return imageName;
    }

    public String getFolderName() { return folderName; }
}