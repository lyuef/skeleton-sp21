package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    private static void check (int args_len,int need_len ) {
        if(!Repository.exists()) {
            throw Utils.error("Not in an initialized Gitlet directory.");
        }
        if(args_len != need_len) {
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
                    if(args.length == 1) {
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
