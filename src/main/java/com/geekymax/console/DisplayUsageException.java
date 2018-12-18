package com.geekymax.console;

public final class DisplayUsageException extends Exception {
    private static final long serialVersionUID = -7009963711233684636L;

    /**
     * Displays the program usage.
     */
    @Override
    public String getMessage() {
        return "usage: java -jar Markdown2HTML.jar [markdownFile] [- header headerFile.html] [-footer footerFile.html] [-out [file.html]]";
    }
}
