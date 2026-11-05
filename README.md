# Digitz

Digitz is a small Java project for building a desktop calculator with Swing. The project is in its initial setup stage, with calculator controls and arithmetic still to be implemented.

## Run in IntelliJ IDEA

1. Open the `Digitz` folder in IntelliJ IDEA.
2. If prompted for a project SDK, select JDK 27 under **File → Project Structure → Project**.
3. The **Digitz** run configuration expects `digitz.Main`. Add that entry point, or update the configuration to point to the class that contains `main`, then click **Run**.

The project uses IntelliJ IDEA's built-in Java build and Swing from the JDK. You only need IntelliJ IDEA and the JDK.

## Project layout

- `src/digitz/` holds the Java source code.
- `.run/Digitz.run.xml` defines the shared IntelliJ run configuration.

## Inspiration

[Simple Java Calculator](https://github.com/pH-7/Simple-Java-Calculator) by Pierre-Henry Soria and contributors inspired this project.
