/**
 * Digitz: a desktop calculator for everyday and scientific maths, built with Java Swing.
 *
 * <h2>Where to start reading</h2>
 * <table class="striped">
 *   <caption>The classes in this package</caption>
 *   <thead><tr><th scope="col">Class</th><th scope="col">Role</th></tr></thead>
 *   <tbody>
 *     <tr><td>{@link digitz.Digitz}</td>
 *         <td>Entry point. Checks for a desktop, sets the look-and-feel and opens the window.</td></tr>
 *     <tr><td>{@link digitz.CalculatorWindow}</td>
 *         <td>The user interface: display, keypad and keyboard shortcuts. Contains no maths.</td></tr>
 *     <tr><td>{@link digitz.CalculatorEngine}</td>
 *         <td>All calculator state and logic. Contains no user interface code.</td></tr>
 *     <tr><td>{@link digitz.BinaryOp}</td>
 *         <td>Operations on two numbers: {@code + − × ÷ ^}.</td></tr>
 *     <tr><td>{@link digitz.UnaryOp}</td>
 *         <td>Operations on one number: {@code √}, {@code sin}, {@code log}, {@code ±} and more.</td></tr>
 *   </tbody>
 * </table>
 *
 * <h2>How a key press flows through the app</h2>
 * <ol>
 *   <li>The user clicks a button or presses a key in the {@link digitz.CalculatorWindow}.</li>
 *   <li>The window calls the matching method on its {@link digitz.CalculatorEngine},
 *       such as {@link digitz.CalculatorEngine#binary(digitz.BinaryOp)}.</li>
 *   <li>The engine updates its state, running a {@link digitz.BinaryOp} or
 *       {@link digitz.UnaryOp} if a result is needed.</li>
 *   <li>The window reads {@link digitz.CalculatorEngine#displayText()} and
 *       {@link digitz.CalculatorEngine#historyText()} and redraws itself.</li>
 * </ol>
 *
 * <h2>Design choices</h2>
 * <ul>
 *   <li><b>Logic and UI are separate.</b> The engine can be tested without a window,
 *       and the look can change without touching the maths.</li>
 *   <li><b>Operations are enums with lambdas.</b> Running any operation is a single
 *       {@code apply} call; there are no string comparisons or large {@code switch}
 *       statements.</li>
 *   <li><b>Errors are values, not exceptions.</b> Undefined results such as {@code 1 ÷ 0}
 *       are {@link java.lang.Double#NaN}, which the engine shows as
 *       {@link digitz.CalculatorEngine#ERROR_TEXT}.</li>
 *   <li><b>Results are rounded for display</b> to 12 significant digits, so
 *       {@code 0.1 + 0.2} shows {@code 0.3}.</li>
 *   <li><b>Angles are in degrees</b> for {@code sin}, {@code cos} and {@code tan}.</li>
 * </ul>
 *
 * <h2>Requirements</h2>
 * <p>Java 22 or newer, and a graphical desktop.
 */
package digitz;
