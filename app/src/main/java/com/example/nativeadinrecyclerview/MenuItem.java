package com.example.nativeadinrecyclerview;

class MenuItem {

    private final String name;
    private final String url;

    private final String imageName;

    public MenuItem(String name, String url,
                    String imageName) {
        this.name = name;
        this.url = url;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }


    public String getImageName() {
        return imageName;
    }
}