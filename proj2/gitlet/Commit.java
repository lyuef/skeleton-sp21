package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String lst;
    private final String message;
    private Map<String,String> files ;
    private final String timestamp;
    Commit(String _message , String  _timestamp , Map<String,String>_files,String _lst) {
        message = _message;
        timestamp = _timestamp;
        files = new HashMap<>(_files);
        lst = _lst;
    }
    /* TODO: fill in the rest of this class. */
    public String getHash() {
        return Utils.sha1(message,timestamp,lst,files.toString());
    }
    public  String getMessage() {
        return message;
    }

    public String  getTimestamp() {
        return timestamp;
    }
    public Map<String,String> getFiles() {
        return files;
    }
}
