package gitlet;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

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
    private static void writeall() {
        writeObject(Branches_FILE, (Serializable) branches);
        writeObject(HEAD_FILE,HEAD);
        writeObject(Tracked_FILE,(Serializable) tracked);
        writeObject(Removed_FILE,(Serializable) removed);
    }
    private static void readall() {
        tracked = readObject(Tracked_FILE,HashMap.class);
        removed = readObject(Removed_FILE,HashMap.class);
        HEAD = readObject(HEAD_FILE,String.class);
        branches = readObject(Branches_FILE,HashMap.class);
    }
    private static File newCommitFile(String name) {
        File COMMIT_FILE = join(COMMITS_DIR,name);
        try {
            COMMIT_FILE.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return COMMIT_FILE;
    }
    private static Commit current_Commit(String id) {
        File Lst_commit_file = join(COMMITS_DIR,id);
        return readObject(Lst_commit_file,Commit.class);
    }
    private static Commit get_lst_Commit(Commit now) {
        if(now.getlst() == "") return null;
        return current_Commit(now.getlst());
    }
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
        writeall();
        writeObject(newCommitFile(fir_commit.getHash()),fir_commit);
        return true;
    }
    public static boolean exists() {
        return GITLET_DIR.exists();
    }
    public static Commit commit(String message,String timestamp,String lst) {
        //core part is handle the addstage and removestage
        readall();
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
        writeall();
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
        readall();
        if(tracked.isEmpty()  && removed.isEmpty()) return false;
        //move HEAD pointer
        String lst = new String(branches.get(HEAD));
        writeall();
        Commit now = commit(messege,time(1),lst);
        readall();
        branches.put(HEAD , now.getHash() ) ;
        writeall();
        return true;
    }
    public static boolean rm (String file_to_remove) {
        //core : check if the file has been removed
        readall();
        // get last commit
        Commit lst_commit = current_Commit(branches.get(HEAD));
        if(removed.containsKey(file_to_remove)) return false;
        else if(!tracked.containsKey(file_to_remove) || !lst_commit.getFiles().containsKey(file_to_remove)) return false;
        removed.put(file_to_remove,"");
        restrictedDelete(file_to_remove);
        writeall();
        return true;
    }
    public static void log() {
        // jump to root commit
        readall();
        Commit now = current_Commit(branches.get(HEAD));
        while(now!=null) {
            now.printMessege();
            now = get_lst_Commit(now);
        }
        writeall();
    }
    public static void global_log () {
        List<String> all_commit_file = plainFilenamesIn(COMMITS_DIR);
        for(String commit_file : all_commit_file) {
            Commit now = current_Commit(commit_file);
            now.printMessege();
        }
    }
    public static boolean find(String message) {
        boolean flag = false;
        List<String> all_commit_file = plainFilenamesIn(COMMITS_DIR);
        for(String commit_file : all_commit_file) {
            Commit now = current_Commit(commit_file);
            if(now.getMessage() == message) {
                System.out.println(now.getHash());
                flag = true;
            }
        }
        return flag;
    }
    public static void status () {
        readall();
        //  branches
        System.out.println("=== Braches ===");
        for(String key : branches.keySet()) {
            if(key == HEAD) {
                System.out.println("*"+HEAD);
            }else {
                System.out.println(key);
            }
        }
        System.out.println();
        // staged files
        System.out.println("=== Staged Files ===");
        for(String key : tracked.keySet()) {
            if(!removed.containsKey(key)) {
                System.out.println(key);
            }
        }
        System.out.println();
        // removed files
        System.out.println("=== Removed Files ===");
        for(String key : removed.keySet()) {
            System.out.println(key);
        }
        System.out.println();
        //
        writeall();
    }
}
