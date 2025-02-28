package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.net.URI;
import java.util.Objects;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    private static void check(int args_len, int... need_len) {
        if (!Repository.exists()) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        boolean validLength = false;
        for (int len : need_len) {
            if (args_len == len) {
                validLength = true;
                break;
            }
        }
        if (!validLength) {
            throw Utils.error("Incorrect operands.");
        }
    }
    public static void main(String[] args) {
        // TODO: what if args is empty?
        try {
            if(args.length == 0) {
                throw new GitletException("Please enter a command.");
            }
            String firstArg = args[0];
            switch(firstArg) {
                case "init":
                    // TODO: handle the `init` command
                    if(!Repository.init() ) {
                        throw Utils.error("A Gitlet version-control system already exists in the current directory.");
                    }
                    if(args.length != 1) {
                        throw Utils.error("Incorrect operands.");
                    }
                    break;
                case "add":
                    // TODO: handle the `add [filename]` command
                    check(args.length,2);
                    if(!Repository.add(args[1])) {
                        throw Utils.error("File does not exist.");
                    }
                    break;
                // TODO: FILL THE REST IN
                case "commit" :
                    if(!Repository.exists()) {
                        throw Utils.error("Not in an initialized Gitlet directory.");
                    }
                    if(args.length == 1 || Objects.equals(args[1], "")) {
                        throw Utils.error("Please enter a commit message.");
                    }
                    if(args.length > 2) {
                        throw Utils.error("Incorrect operands.");
                    }
                    if(!Repository.make_commit(args[1])) {
                        throw Utils.error("No changes added to the commit.");
                    }
                    break;
                case "rm" :
                    check(args.length,2);
                    if(!Repository.rm(args[1])) {
                        throw Utils.error("No reason to remove the file.");
                    }
                    break;
                case "log" :
                    check(args.length,1);
                    Repository.log();
                    break;
                case "global-log" :
                    check((args.length),1);
                    Repository.global_log();
                    break;
                case "find" :
                    check(args.length,2);
                    if(!Repository.find(args[1])) {
                        throw Utils.error("Found no commit with that message.");
                    }
                    break;
                case "status" :
                    check(args.length,1);
                    Repository.status();
                    break;
                case "checkout" :
                    check(args.length,2,3,4);
                    if(args.length == 3) {
                        if(!Objects.equals(args[1], "--")) {
                            throw Utils.error("Incorrect operands.");
                        }
                        Repository.checkout(args[2]);
                    }
                    if(args.length == 4) {
                        if(!Objects.equals(args[2], "--")) {
                            throw Utils.error("Incorrect operands.");
                        }
                        Repository.checkout(args[3],args[1]);
                    }
                    if(args.length == 2 ) {
                        Repository.checkout_branch(args[1]);
                    }
                    break;
                case "branch" :
                    check(args.length,2);
                    if(!Repository.createnewbranch(args[1])) {
                        throw Utils.error("A branch with that name already exists.");
                    }
                    break;
                case "rm-branch" :
                    check(args.length,2) ;
                    Repository.remove_branch(args[1]);
                    break;
                case "reset" :
                    break;
                case "merge" :
                    break;
                default:
                    throw new GitletException("No command with that name exists.");
            }
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}
