package com.n1analytics.paillier.cli;


//import org.apache.commons.cli.Option;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.CommandLineParser;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A simple command line interface to Javallier using the public API.
 *
 * This is not meant to be a full featured command line interface or shell but to
 * illustrate how to do some simple operations and to be used for testing integration
 * between different Paillier cryptosystem implementations.
 */
public class JavallierCLI {

  private static final Logger log = Logger.getLogger(JavallierCLI.class.getName());

  private String[] args = null;
  private Options options = new Options();

  public JavallierCLI(String[] args) {
    this.args = args;

    // create the global Options
    this.options.addOption("h", "help", false, "HELP");
    options.addOption("v", "verbose", false, "Enable logging");
    options.addOption("o", "output", true, "Output to given file instead of stdout");

  }

  public void parse() {

    // Create a list of commands that are supported. Each
    // command defines a run method and some methods for
    // printing help.
    // See the definition of each command below.
    HashMap<String, Command> commands = new HashMap<String, Command>();

    commands.put("genpkey", new GenerateKeyPairCommand("genpkey"));
    commands.put("extract", new ExtractCommand("extract"));
    commands.put("encrypt", new EncryptCommand("encrypt"));
    commands.put("decrypt", new DecryptCommand("decrypt"));
    commands.put("add", new AddCommand("add"));
    commands.put("addenc", new AddEncCommand("addenc"));

    Command command = null;
    List<String> argsList = Arrays.asList(args);
    if (argsList.size() > 0) {
      command = commands.get(argsList.get(0));
    }

    // create the arg parser
    CommandLineParser parser = new DefaultParser();

    // parse the command line arguments
    try {
      CommandLine line = parser.parse(options, args);

      if (line.hasOption("help")) {

        // If there is a command listed (e.g. genpkey --help)
        // then show the help for that command
        if (command == null) {
          help(commands.values());
        } else {
          help(command);
        }
        System.exit(0);

      }

      if (line.hasOption("v")) {
        log.log(Level.INFO, "Using cli argument -v=" + line.getOptionValue("v"));
      }

    } catch (ParseException exp) {
      // oops, something went wrong
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
      // print the list of available options
      help(commands.values());
    }

  }

  /**
   * Print the usage for a specific command.
   * @param command The command whose usage you want to print.
   */
  public static void usage(Command command) {
    System.out.println("Usage: pheutil " + command.getName() + " " + command.getOptions());
  }

  /**
   * Prints the help for a specific command.
   * @param command The command whose help you want to print.
   */
  public static void help(Command command) {
    usage(command);
    System.out.println();
    System.out.println(command.getDescription());
  }

  private void help(Collection<Command> commands) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("pheutil", options);

    System.out.println("");
    System.out.println("Commands:");
    System.out.println("");
    for (Command command : commands) {
      System.out.println("    " + command.getName() + ": " + command.getBlurb());
    }
    System.out.println("");
    System.out.println("Try pheutil COMMAND --help for command usage.");

    System.exit(0);
  }


  /**
   * An exception that is thrown when invalid arguments are
   * passed to a command.
   */
  protected static class InvalidArgsException extends Exception {
    private List<String> args;

    public InvalidArgsException(List<String> args) {
      this.args = args;
    }

    public List<String> getArgs() {
      return this.args;
    }
  }

  /**
   * Defines the interface for commands that can be run by the CLI
   */
  protected static abstract class Command {
    private String name;

    public Command(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    /**
     * Run the command.
     * @param args A list of args to the command.
     */
    public abstract void run(List<String> args) throws InvalidArgsException, IOException;

    /**
     * Gets a string describing command line arguments of the command.
     * Used when printing usage and help.
     */
    public abstract String getOptions();

    /**
     * Gets a short string describing what the command does.
     * Used when printing usage and help.
     */
    public abstract String getBlurb();

    /**
     * Gets a verbose string describing how to use the command.
     * Used when printing command specific usage and help.
     */
    public abstract String getDescription();
  }


  /**
   * This command creates a new paillier keypair
   */
  protected static class GenerateKeyPairCommand extends Command {

    public GenerateKeyPairCommand(String name) {
      super(name);
    }

    public void run(List<String> args) {
      System.out.println("Running the create command");
      System.out.println(args);
    }

    public String getOptions() {
      return "[KEYSIZE]";
    }

    public String getBlurb() {
      return "Create a new paillier keypair";
    }

    public String getDescription() {
      return "Generate a new public/private keypair for use in\n" +
              "paillier operations. Output in JSON Web Key format.";
    }
  }


  /**
   * This command encrypts a plaintext value with a paillier
   * public key.
   */
  protected static class EncryptCommand extends Command {

    public EncryptCommand(String name) {
      super(name);
    }

    public void run(List<String> args) {
      System.out.println("Running the encrypt command");
      System.out.println(args);
    }

    public String getOptions() {
      return "PUBLICKEY";
    }

    public String getBlurb() {
      return "Encrypt a value with the given public key";
    }

    public String getDescription() {
      return "Encrypt a value with the given public key\n" +
             "Value can be an integer or float.";
    }
  }


  /**
   * This command decrypts a ciphertext value with a paillier
   * private key.
   */
  protected static class DecryptCommand extends Command {

    public DecryptCommand(String name) {
      super(name);
    }

    public void run(List<String> args) {
      System.out.println("Running the decrypt command");
      System.out.println(args);
    }

    public String getOptions() {
      return "PRIVATEKEY ENCRYPTED";
    }

    public String getBlurb() {
      return "Decrypt ENCRYPTED using PRIVATEKEY";
    }

    public String getDescription() {
      return "Decrypted value could be an integer or float.";
    }
  }

  /**
   * This command extracts the public key from a private key.
   */
  protected static class ExtractCommand extends Command {

    public ExtractCommand(String name) {
      super(name);
    }

    public void run(List<String> args) {
      System.out.println("Running the extract command");
      System.out.println(args);
    }

    public String getOptions() {
      return "PRIVATEKEY";
    }

    public String getBlurb() {
      return "Extract the public key from a PRIVATE";
    }

    public String getDescription() {
      return "Extract the public key from a private key";
    }
  }

  /**
   * This command adds a plaintext number to an encrypted
   * number.
   */
  protected static class AddCommand extends Command {

    public AddCommand(String name) {
      super(name);
    }

    public void run(List<String> args) {
      System.out.println("Running the add command");
      System.out.println(args);
    }

    public String getOptions() {
      return "PUBLICKEY ENCRYPTED PLAINTEXT";
    }

    public String getBlurb() {
      return "Add ENCRYPTED to PLAINTEXT";
    }

    public String getDescription() {
      return "Add ENCRYPTED and PLAINTEXT numbers together \n" +
              "producing a new encrypted number.";
    }
  }

  /**
   * This command adds two encrypted numbers together.
   */
  protected static class AddEncCommand extends Command {

    public AddEncCommand(String name) {
      super(name);
    }

    public void run(List<String> args) {
      System.out.println("Running the addenc command");
      System.out.println(args);
    }

    public String getOptions() {
      return "PUBLICKEY ENCRYPTED1 ENCRYPTED2";
    }

    public String getBlurb() {
      return "Add ENCRYPTED1 to ENCRYPTED2";
    }

    public String getDescription() {
      return "Add two encrypted numbers together \n" +
              "producing a new encrypted number.";
    }
  }

}