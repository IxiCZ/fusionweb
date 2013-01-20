package cz.ixi.fusionweb.web.util;


/**
 * Simple ENUM to centralize strings for common navigation destinations.
 */
public enum PageNavigation {
    CREATE("Create"),
    LIST("List"),
    EDIT("Edit"),
    VIEW("View"),
    INDEX("/index"),
    SEARCH("Search");

    private String text;

    PageNavigation(final String s) {
        this.text = s;
    }

    public String getText() {
        return this.text;
    }

    public String toString() {
        return this.text;
    }
}
