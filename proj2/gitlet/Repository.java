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
        if(Objects.equals(id, "") || id == null) return null;
        File Lst_commit_file = join(COMMITS_DIR,id);
        if(!Lst_commit_file.exists()) return null;
        return readObject(Lst_commit_file,Commit.class);
    }
    private static Commit get_lst_Commit(Commit now) {
        if(Objects.equals(now.getlst().get(0), "")) return null;
        return current_Commit(now.getlst().get(0));
    }
    private static void createnewfile(File... files) {
        for(File file : files) {
            try{
                file.createNewFile();
            }catch (IOException e ){
                e.printStackTrace();
            }
        }
    }
    private static String get_abbre_commit_id(String commit_id) {
        List<String> all_commit_file = plainFilenamesIn(COMMITS_DIR);
        if (all_commit_file != null) {
            for(String commit_file : all_commit_file) {
                Commit now = current_Commit(commit_file);
                //System.err.println(now.getHash());
                if(now.getHash().startsWith(commit_id)) {
                    return now.getHash();
                }
            }
        }
        return null;
    }
    private static void check_untracked(File f) {
        if(f.exists()) {
            throw error("There is an untracked file in the way; delete it, or add and commit it first.");
        }
    }
    private static void conflict(String head,String merge_in,File target_file,String file_name) {
        writeContents(target_file,"<<<<<<< HEAD\n",head==null?null:readContentsAsString(join(BLOBS_DIR,head)),"=======\n",merge_in==null?null:readContentsAsString(join(BLOBS_DIR,merge_in)),">>>>>>>\n");
        add(file_name);
    }
    public static boolean init() {
        if(GITLET_DIR.exists()) return false;
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        createnewfile(Branches_FILE,HEAD_FILE,Tracked_FILE,Removed_FILE);
        HEAD = "master";
        Commit fir_commit = new Commit("initial commit", time(0),new HashMap<>(),"");
        branches.put(HEAD,fir_commit.getHash());
        writeall();
        writeObject(newCommitFile(fir_commit.getHash()),fir_commit);
        return true;
    }
    public static boolean exists() {
        return GITLET_DIR.exists();
    }
    public static Commit commit(String message,String timestamp,String... lst) {
        //core part is handle the addstage and removestage
        readall();
        for(String key : tracked.keySet()) {
            if(removed.containsKey(key)) {
                tracked.remove(key);
            }
        }
        Commit lst_commit = current_Commit(lst[0]);
        Map<String,String > lst_tracked = new HashMap<>(lst_commit.getFiles());
        Map<String,String> llst_tracked = new HashMap<>(lst_tracked);
        for(String key : llst_tracked.keySet()) {
            if(removed.containsKey(key)) {
                lst_tracked.remove(key);
            }
        }
        Map<String,String> res = new HashMap<>(lst_tracked);
        for(String key : tracked.keySet()) {
            res.put(key,tracked.get(key));
        }
        removed.clear();
        tracked.clear();
        writeall();
        return new Commit(message,timestamp,res,lst);
    }
    public static boolean add (String file_to_add) {
        File ADD_FILE = join(CWD,file_to_add);
        if(!ADD_FILE.exists()) return false;
        readall();
        String hash_val = sha1(readContents(ADD_FILE));
        Commit now_commit = current_Commit(branches.get(HEAD));
        //if(Objects.equals(file_to_add, "m.txt"))System.err.println(now_commit.getHash());
        if(now_commit.getFiles().containsKey(file_to_add) && Objects.equals(now_commit.getFiles().get(file_to_add), hash_val)) {
            if(tracked.containsKey(file_to_add)) {
                tracked.remove(file_to_add);
            }
        } else {
            tracked.put(file_to_add, hash_val);
            //store the contents of file
            File NewBlob = join(BLOBS_DIR, hash_val);
            createnewfile(NewBlob);
            writeContents(NewBlob, readContentsAsString(ADD_FILE));
        }
        if(removed.containsKey(file_to_add)) {
            removed.remove(file_to_add);
        }
        writeall();
        //if(Objects.equals(file_to_add, "m.txt")) log();
        return true;
    }
    public static boolean make_commit(String messege,String... lsts) {
        //core : handle the logic of nothing changed and moved the HEAD point
        readall();
        if(tracked.isEmpty()  && removed.isEmpty()) return false;
        //move HEAD pointer
        String lst = new String(branches.get(HEAD));
        writeall();
        String[] newLsts = new String[lsts.length + 1];
        newLsts[0] = lst;
        System.arraycopy(lsts, 0, newLsts, 1, lsts.length);
        Commit now = commit(messege,time(1), newLsts);
        readall();
        branches.put(HEAD , now.getHash() ) ;
        File now_commit_file = join(COMMITS_DIR, now.getHash());
        createnewfile(now_commit_file);
        writeObject(now_commit_file,now);
        writeall();
//        if (Objects.equals(messege, "Add h.txt and remove g.txt")) {
//            System.err.println(now.getHash());
//            System.err.println(now_commit_file.toPath());
//            log();
//        }
        return true;
    }
    public static boolean rm (String file_to_remove) {
        // core : check if the file has been removed
        readall();
        // get last commit
        Commit lst_commit = current_Commit(branches.get(HEAD));
        if(removed.containsKey(file_to_remove)) return false;
        else if(!tracked.containsKey(file_to_remove) && !lst_commit.getFiles().containsKey(file_to_remove)) return false;
        if(tracked.containsKey(file_to_remove)) {
            tracked.remove(file_to_remove);
        }else {
            removed.put(file_to_remove, "");
            restrictedDelete(file_to_remove);
        }
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
            System.out.println();
        }
        writeall();
    }
    public static void global_log () {
        List<String> all_commit_file = plainFilenamesIn(COMMITS_DIR);
        for(String commit_file : all_commit_file) {
            Commit now = current_Commit(commit_file);
            now.printMessege();
            System.out.println();
        }
    }
    public static boolean find(String message) {
        boolean flag = false;
        List<String> all_commit_file = plainFilenamesIn(COMMITS_DIR);
        for(String commit_file : all_commit_file) {
            Commit now = current_Commit(commit_file);
            if(Objects.equals(now.getMessage(), message)) {
                System.out.println(now.getHash());
                flag = true;
            }
        }
        return flag;
    }
    public static void status () {
        readall();
        //  branches
        System.out.println("=== Branches ===");
        List<String> keys = new ArrayList<>(branches.keySet());
        Collections.sort(keys);
        for(String key : keys) {
            if(Objects.equals(key, HEAD)) {
                System.out.println("*"+HEAD);
            }else {
                System.out.println(key);
            }
        }
        System.out.println();
        // staged files
        System.out.println("=== Staged Files ===");
        keys = new ArrayList<>(tracked.keySet());
        Collections.sort(keys);
        for(String key : keys) {
            if(!removed.containsKey(key)) {
                System.out.println(key);
            }
        }
        System.out.println();
        // removed files
        System.out.println("=== Removed Files ===");
        keys = new ArrayList<>(removed.keySet());
        Collections.sort(keys);
        for(String key : removed.keySet()) {
            System.out.println(key);
        }
        System.out.println();
        //
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        //
        System.out.println("=== Untracked Files ===");
        System.out.println();
        //
        writeall();
    }
    public static void checkout(String file_name) {
        readall();
        String lst = branches.get(HEAD);
        writeall();
        checkout(file_name,lst);
    }
    public static void checkout(String file_name,String commit_id) {
        // overwrite the content of file
        readall();
        commit_id = get_abbre_commit_id(commit_id);
        Commit now_commit = current_Commit(commit_id);
        if(now_commit == null) {
            throw error("No commit with that id exists.");
        }
        if(!now_commit.getFiles().containsKey(file_name)) {
            throw error("File does not exist in that commit.");
        }
        File file = join(CWD,file_name);
        createnewfile(file);
        writeContents(file,readContentsAsString(join(BLOBS_DIR, now_commit.getFiles().get(file_name))));
        writeall();
    }
    public static void checkout_branch(String branch) {
        // 3 cases to discuss
        readall();
        if(!branches.containsKey(branch)) {
            throw error("No such branch exists.");
        }
        if(Objects.equals(branch, HEAD)) {
            throw error("No need to checkout the current branch.");
        }
        Commit now = current_Commit(branches.get(HEAD));
        Commit check = current_Commit(branches.get(branch));
        Set<String> keys = new HashSet<>(now.getFiles().keySet());
        keys.addAll(check.getFiles().keySet());
        for(String key:keys) {
            if(!now.getFiles().containsKey(key) && check.getFiles().containsKey(key)) {
                check_untracked(join(CWD,key));
            }
        }
        for(String key:keys) {
            if(now.getFiles().containsKey(key) && check.getFiles().containsKey(key)) {
                if(!Objects.equals(now.getFiles().get(key), check.getFiles().get(key))) {
                    writeContents(join(CWD,key),readContentsAsString(join(BLOBS_DIR,check.getFiles().get(key))));
                }
            } else if(now.getFiles().containsKey(key)) {
                restrictedDelete(key);
            }else {
                File now_file = join(CWD,key);
                createnewfile(now_file);
                writeContents(now_file,readContentsAsString(join(BLOBS_DIR,check.getFiles().get(key))));
            }
        }
        tracked.clear();
        removed.clear();
        HEAD = branch;
        writeall();
        //if(Objects.equals(branch, "other")) global_log();
    }
    public static boolean createnewbranch (String branch_name) {
        //create a new branch but don't switch the HEAD instantly
        readall();
        if(branches.containsKey(branch_name)) return false;
        branches.put(branch_name,branches.get(HEAD));
        writeall();
        return true;
    }
    public static void remove_branch (String branch_name) {
        readall();
        if(Objects.equals(branch_name, HEAD)) {
            throw error("Cannot remove the current branch.");
        }
        if(!branches.containsKey(branch_name)) {
            throw error("A branch with that name does not exist.");
        }
        branches.remove(branch_name);
        writeall();
    }
    public static void reset(String commit_id) {
        readall();
        //System.err.println(commit_id);
        commit_id = get_abbre_commit_id(commit_id);
        //System.err.println(commit_id);
        Commit now = current_Commit(branches.get(HEAD));
        Commit check = current_Commit(commit_id);
        //System.err.println(check);
        if(check == null ) {
            throw error("No commit with that id exists.");
        }
        Set<String> keys = new HashSet<>(now.getFiles().keySet());
        keys.addAll(check.getFiles().keySet());
        for(String key:keys) {
            if(!now.getFiles().containsKey(key) && check.getFiles().containsKey(key)) {
                check_untracked(join(CWD,key));
            }
        }
        for(String key:keys) {
            if(now.getFiles().containsKey(key) && check.getFiles().containsKey(key)) {
                if(!Objects.equals(now.getFiles().get(key), check.getFiles().get(key))) {
                    writeContents(join(CWD,key),readContentsAsString(join(BLOBS_DIR,check.getFiles().get(key))));
                }
            } else if(now.getFiles().containsKey(key)) {
                restrictedDelete(key);
            }else {
                File now_file = join(CWD,key);
                createnewfile(now_file);
                writeContents(now_file,readContentsAsString(join(BLOBS_DIR,check.getFiles().get(key))));
            }
        }
        tracked.clear();
        removed.clear();
        branches.put(HEAD,commit_id);
        writeall();
    }
    public static void merge(String branch_name) {
        readall();
        //check failed cases
        if(!tracked.isEmpty() || !removed.isEmpty()) {
            throw error("You have uncommitted changes.");
        }
        if(!branches.containsKey(branch_name)) {
            throw error("A branch with that name does not exist.");
        }
        if(Objects.equals(branch_name, HEAD)) {
            throw error("Cannot merge a branch with itself.");
        }
        //find lca
        Commit lca = current_Commit(branches.get(HEAD));
        Set<String>path = new HashSet<>();
        Stack<Commit> stk = new Stack<>();
        stk.push(lca);
        //if(Objects.equals(branch_name, "B2")) global_log();
        while(!stk.isEmpty()) {
            Commit now = stk.peek();stk.pop();
            if(now == null || path.contains(now.getHash())) continue;
            path.add(now.getHash());
            //if(Objects.equals(branch_name, "B2")) System.err.println(now.getMessage());
            ArrayList<String>lsts = new ArrayList<>(now.getlst());
            for(String lst:lsts) {
                //if(Objects.equals(branch_name, "B2") && !Objects.equals(lst, "")) System.err.println("*"+ current_Commit(lst).getMessage());
                stk.push(current_Commit(lst));
            }
        }
        lca = current_Commit(branches.get(branch_name));
        while(lca!=null) {
            if(path.contains(lca.getHash())) break;
            lca = get_lst_Commit(lca);
        }
        //if(Objects.equals(branch_name, "B2")) System.err.println(lca.getMessage());
        //two special cases
        if(Objects.equals(lca.getHash(), branches.get(branch_name))) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return ;
        } else if(Objects.equals(lca.getHash(), branches.get(HEAD))) {
            System.out.println("Current branch fast-forwarded.");
            checkout_branch(branch_name);
            return ;
        }
        //8 cases then
        Set<String> keys = new HashSet<>(lca.getFiles().keySet());
        Map<String,String>head_map = new HashMap<>(current_Commit(branches.get(HEAD)).getFiles());
        Map<String,String>add_map = new HashMap<>(current_Commit(branches.get(branch_name)).getFiles());
        keys.addAll(head_map.keySet());
        keys.addAll(add_map.keySet());
        //check untracked files first !!!
        for(String key : keys) {
                if(!lca.getFiles().containsKey(key) && !head_map.containsKey(key) && add_map.containsKey(key)) {
                    check_untracked(join(CWD,key));
                }
        }
        boolean have_conflicts = false;
        for(String key : keys) {
            if(lca.getFiles().containsKey(key)) {
                if (head_map.containsKey(key) && add_map.containsKey(key)) {
                    if(Objects.equals(head_map.get(key), lca.getFiles().get(key)))  {
                        if(!Objects.equals(head_map.get(key), add_map.get(key))) {
                            writeContents(join(CWD,key),readContentsAsString(join(BLOBS_DIR, add_map.get(key))));
                            add(key);
                        }
                    } else {
                        if(!Objects.equals(head_map.get(key), add_map.get(key)) ) {
                            if(!Objects.equals(add_map.get(key),lca.getFiles().get(key)) ){
                                conflict(head_map.get(key),add_map.get(key),join(CWD,key),key);
                                have_conflicts = true;
                            }
                        }
                    }
                } else if (head_map.containsKey(key) && !add_map.containsKey(key)) {
                    if(!Objects.equals(head_map.get(key), lca.getFiles().get(key))) {
                        conflict(head_map.get(key),null,join(CWD,key),key);
                        have_conflicts = true;
                    } else {
                        restrictedDelete(join(CWD,key));
                        removed.put(key,"");
                    }
                } else if (!head_map.containsKey(key) && add_map.containsKey(key)) {
                    if (! Objects.equals(add_map.get(key), lca.getFiles().get(key))) {
                        conflict(null, add_map.get(key),join(CWD, key),key);
                        have_conflicts = true;
                    }
                }
            }else {
                if(head_map.containsKey(key) && add_map.containsKey(key)) {
                    if(!Objects.equals(head_map.get(key), add_map.get(key))) {
                        conflict(head_map.get(key),add_map.get(key),join(CWD,key),key);
                        have_conflicts = true;
                    }
                } else if(add_map.containsKey(key)) {
                    File add = join(CWD,key);
                    //check_untracked(add);
                    createnewfile(add);
                    writeContents(add,readContentsAsString(join(BLOBS_DIR, add_map.get(key))));
                    tracked.put(key, add_map.get(key));
                }
            }
        }
        if(have_conflicts) {
            System.out.println("Encountered a merge conflict.");
        }
        writeall();
        make_commit(String.format("Merged %s into %s.",branch_name,HEAD),branches.get(branch_name));
        readall();
        String bef = branches.get(HEAD);
        Commit merge_commit = current_Commit(bef);
        merge_commit.add_merge_info(String.format("Merge: %s %s",get_lst_Commit(merge_commit).getHash().substring(0,7),current_Commit(branches.get(branch_name)).getHash().substring(0,7)));
        writeObject(join(COMMITS_DIR,bef),merge_commit);
        writeall();
    }
}
