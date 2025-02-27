package capers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = join(CWD,"capers"); // TODO Hint: look at the `join`
                                            //      function in Utils
    static final File DOGS_FOLDER = new File(CAPERS_FOLDER, "dogs");
    static final File STORY_FILE = new File(CAPERS_FOLDER, "story");
    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() throws IOException {
        // TODO
        if(!CAPERS_FOLDER.exists()) {
            CAPERS_FOLDER.mkdir();
        }
        if(!DOGS_FOLDER.exists()) {
            DOGS_FOLDER.mkdir();
        }
        if(!STORY_FILE.exists())  {
            STORY_FILE.createNewFile();
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) throws IOException {
        // TODO
        try (FileWriter fw = new FileWriter(STORY_FILE, true)) {
            fw.write(text);
            fw.write(System.lineSeparator());
        }
        List<String> lines = Files.readAllLines(STORY_FILE.toPath());
        for (String line : lines) {
            System.out.println(line);
        }
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) throws IOException {
        // TODO
        Dog now = new Dog(name,breed,age);
        now.saveDog();
        System.out.println(now);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) throws IOException {
        // TODO
        Dog now = Dog.fromFile(name);
        now.haveBirthday();
        now.saveDog();
    }
}
