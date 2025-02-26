package gitlet;

import javax.swing.*;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR,"commits");
    public static final File BLOBS_DIR = join(GITLET_DIR,"blobs");
    public static final File Branches_FILE = join(GITLET_DIR,"branches.ser");
    public static final File HEAD_FILE = join(GITLET_DIR,"HEAD.ser");
    public static final File Tracked_FILE = join(GITLET_DIR,"tracked.ser");
    public static final File Removed_FILE = join(GITLET_DIR,"removed.ser");
    static Map<String,String>branches = new HashMap<>();
    static String HEAD = new String();
    static Map<String,String>tracked = new HashMap<>();
    static Map<String,String>removed = new HashMap<>();
    /* TODO: fill in the rest of this class. */
    public static boolean init() {
        if(GITLET_DIR.exists()) return false;
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        try {
            Branches_FILE.createNewFile();
            HEAD_FILE.createNewFile();
            Tracked_FILE.createNewFile();
            Removed_FILE.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }
        HEAD = "MASTER";
        Commit fir_commit = new Commit("initial commit", time(0),new HashMap<>(),"");
        branches.put(HEAD,fir_commit.getHash());
        writeObject(Branches_FILE, (Serializable) branches);
        writeObject(HEAD_FILE,HEAD);
        writeObject(Tracked_FILE,(Serializable) tracked);
        writeObject(Removed_FILE,(Serializable) removed);
        File FIR_COMMIT_FILE = join(COMMITS_DIR,fir_commit.getHash());
        try {
            FIR_COMMIT_FILE.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeObject(FIR_COMMIT_FILE,fir_commit);
        return true;
    }
    public static boolean exists() {
        return GITLET_DIR.exists();
    }
    public static Commit commit(String message,String timestamp,String lst) {
        //core part is handle the addstage and removestage
        tracked = readObject(Tracked_FILE,HashMap.class);
        removed = readObject(Removed_FILE,HashMap.class);
        for(String key : tracked.keySet()) {
            if(removed.containsKey(key)) {
                tracked.remove(key);
            }
        }
        File Lst_commit_file = join(COMMITS_DIR,lst);
        Commit lst_commit = readObject(Lst_commit_file,Commit.class);
        Map<String,String > lst_tracked = new HashMap<>(lst_commit.getFiles());
        for(String key : lst_tracked.keySet()) {
            if(removed.containsKey(key)) {
                lst_tracked.remove(key);
            }
        }
        Map<String,String> res = new HashMap<>(lst_tracked);
        for(String key : lst_tracked.keySet()) {
            if(tracked.containsKey(key)) {
                res.put(key,tracked.get(key));
            }
        }
        removed.clear();
        tracked.clear();
        writeObject(Tracked_FILE,(Serializable) tracked);
        writeObject(Removed_FILE,(Serializable) removed);
        return new Commit(message,timestamp,res,lst);
    }
    public static boolean add (String file_to_add) {
        File ADD_FILE = join(CWD,file_to_add);
        if(!ADD_FILE.exists()) return false;
        tracked = readObject(Tracked_FILE,HashMap.class);
        tracked.put(file_to_add,sha1(readContents(ADD_FILE)));
        writeObject(Tracked_FILE,(Serializable) tracked);
        return true;
    }
    public static boolean make_commit(String messege) {
        //core : handle the logic of nothing changed and moved the HEAD point
        tracked = readObject(Tracked_FILE,HashMap.class);
        removed = readObject(Removed_FILE,HashMap.class);
        if(tracked.isEmpty()  && removed.isEmpty()) return false;
        writeObject(Tracked_FILE,(Serializable) tracked);
        writeObject(Removed_FILE,(Serializable) removed);
        //move HEAD pointer
        HEAD = readObject(HEAD_FILE,String.class);
        branches = readObject(Branches_FILE,HashMap.class);
        Commit now = commit(messege,time(1),branches.get(HEAD));
        branches.put(HEAD , now.getHash() ) ;
        writeObject(HEAD_FILE,HEAD);
        writeObject(Branches_FILE,(Serializable) branches);
        File NOW_FILE = null;
        try {
            NOW_FILE = join(COMMITS_DIR,now.getHash());
            NOW_FILE.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }
        writeObject(NOW_FILE,now);
        return true;
    }
    public static boolean rm (String file_to_remove) {
        //core : check if the file has been removed
        tracked = readObject(Tracked_FILE,HashMap.class);
        removed = readObject(Removed_FILE,HashMap.class);
        HEAD = readObject(HEAD_FILE,String.class);
        branches = readObject(Branches_FILE,HashMap.class);
        File Lst_commit_file = join(COMMITS_DIR,branches.get(HEAD));
        Commit lst_commit = readObject(Lst_commit_file,Commit.class);
        if(removed.containsKey(file_to_remove)) return false;
        else if(!tracked.containsKey(file_to_remove) || !lst_commit.getFiles().containsKey(file_to_remove)) return false;
        removed.put(file_to_remove,"");
        writeObject(Tracked_FILE,(Serializable) tracked);
        writeObject(Removed_FILE,(Serializable) removed);
        writeObject(HEAD_FILE,HEAD);
        writeObject(Branches_FILE,(Serializable) branches);
        return true;
    }
}
