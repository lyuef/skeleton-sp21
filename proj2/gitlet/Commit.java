package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.*;

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
    private ArrayList<String>lst =new ArrayList<>();
    private final String message;
    private Map<String,String> files ;
    private final String timestamp;
    private String  merge_message = null;
    private String hash_val = null;
    Commit(String _message , String  _timestamp , Map<String,String>_files,String... _lsts) {
        message = _message;
        timestamp = _timestamp;
        files = new HashMap<>(_files);
        for(String _lst:_lsts) {
            lst.add(_lst);
        }
        hash_val = Utils.sha1(message,timestamp,lst.toString(),files.toString());
    }
    /* TODO: fill in the rest of this class. */
    public String getHash() {
        return hash_val;
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
    public ArrayList<String> getlst() {
        return lst;
    }
    public void printMessege() {
        System.out.println("===");
        //TODO : add merge info
        System.out.println("commit " + getHash());
        if(merge_message != null) {
            System.out.println(merge_message);
        }
        System.out.println("Date: " + getTimestamp());
        System.out.println(getMessage());
    }
    public void add_merge_info(String s) {
        merge_message = s;
    }
}
