package psn.zirconium.features

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.ActionSetting
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import psn.zirconium.ZirconiumEntry

object HeldItemRender : Module(
    name = "Held Item Render",
    description = "changes the held item position",
    category=ZirconiumEntry.ZCON
){
    private val positioncat by DropdownSetting("Item Position")
    val itemX by NumberSetting("x",0.0,-0.5,0.5,0.05,"").withDependency { positioncat }
    val itemY by NumberSetting("y",0.0,-0.5,0.5,0.05,"").withDependency { positioncat }
    val itemZ by NumberSetting("z",0.0,-0.5,0.5,0.05,"").withDependency { positioncat }
    private val rsTrans by ActionSetting("Reset Translation",""){
        settings["x"]?.reset()
        settings["y"]?.reset()
        settings["z"]?.reset()
    }.withDependency { positioncat }
    val itemXrot by NumberSetting("x rot",0,-180,180,1,"").withDependency { positioncat }
    val itemYrot by NumberSetting("y rot",0,-180,180,1,"").withDependency { positioncat }
    val itemZrot by NumberSetting("z rot",0,-180,180,1,"").withDependency { positioncat }
    private val rsRot by ActionSetting("Reset Rotation",""){
        settings["x rot"]?.reset()
        settings["y rot"]?.reset()
        settings["z rot"]?.reset()
    }.withDependency { positioncat }
    val itemWidth by NumberSetting("width",1f,-1,5,0.05,"").withDependency { positioncat }
    val itemHeight by NumberSetting("height",1f,-1,5,0.05,"").withDependency { positioncat }
    val itemLength by NumberSetting("length",1f,-1,5,0.05,"").withDependency { positioncat }
    private val rsScale by ActionSetting("Reset Scale",""){
        settings["width"]?.reset()
        settings["height"]?.reset()
        settings["length"]?.reset()
    }.withDependency { positioncat }
    val swingXrot by NumberSetting("Swing X Rot",-80f,-160,0,16,"").withDependency { positioncat }
    val swingYrot by NumberSetting("Swing Y Rot",-20f,-40,0,4,"").withDependency { positioncat }
    val swingZrot by NumberSetting("Swing Z Rot",-20f,-40,0,4,"").withDependency { positioncat }
    val swingOrot by NumberSetting("Swing Offset Rot",-45f,-90,0,5,"").withDependency { positioncat }
    
    val translateSwing by BooleanSetting("Translate Swing",true,"").withDependency { positioncat }
    val swingx by NumberSetting("Swing X",1f,0,4,0.25,"").withDependency { positioncat && translateSwing }
    val swingy by NumberSetting("Swing Y",1f,0,4,0.25,"").withDependency { positioncat && translateSwing }
    val swingz by NumberSetting("Swing Z",1f,0,4,0.25,"").withDependency { positioncat && translateSwing }
    
    private val rsSwing by ActionSetting("Reset Swing Transform",""){
        settings["Swing X Rot"]?.reset()
        settings["Swing Y Rot"]?.reset()
        settings["Swing Z Rot"]?.reset()
        settings["Swing Offset Rot"]?.reset()
        settings["Translate Swing"]?.reset()
        settings["Swing X"]?.reset()
        settings["Swing Y"]?.reset()
        settings["Swing Z"]?.reset()
    }.withDependency { positioncat }
    
    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//
    
    private val swingcat by DropdownSetting("Swing")
    private val swingWhileUsing by BooleanSetting("Swing While Using",false,"left click while drawing a bow, drinking a potion, etc").withDependency {swingcat}

    private val customSwingDuration by BooleanSetting("Custom Swing Duration",false,"").withDependency { swingcat }
    private val ignoreHaste by BooleanSetting("Ignore Haste",false,"").withDependency { swingcat && customSwingDuration }
    @JvmStatic val swingDuration by NumberSetting("Swing Duration",7,2,20,1,"").withDependency {swingcat&&customSwingDuration}
    @JvmStatic fun doUsing():Boolean {
        return enabled&&swingWhileUsing
    }
    @JvmStatic fun doSwingDur():Boolean {
        return enabled&&customSwingDuration
    }
    @JvmStatic fun doHaste():Boolean {
        return enabled&&ignoreHaste&&customSwingDuration
    }

    //--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//--//

    private val presets by DropdownSetting("Presets")
    private val vanilla by ActionSetting("Vanilla",""){
        rsTrans.invoke()
        rsRot.invoke()
        rsScale.invoke()
        rsSwing.invoke()
        settings["Custom Swing Duration"]?.reset()
        settings["Swing Duration"]?.reset()
        settings["Swing While Using"]?.reset()
    }.withDependency { presets }
    private val small by ActionSetting("Small",""){
        setSettings(Preset(
            trans=listOf(0.1,0.2,0.0),
            rot=listOf(0,0,0),
            scale=listOf(0.35f,0.35f,0.35f),
            swing=listOf(-80f,-20f,-20f,-45f,0f,0f,0f),
            doSwingDur=true,
            swingDur=4,
            haste=true,
            using=true,
        ))
    }.withDependency { presets }
    private val horizontal by ActionSetting("Horizontal",""){
        setSettings(Preset(
            trans=listOf(-0.3,0.25,-0.05),
            rot=listOf(11,-22,92),
            scale=listOf(1f,1f,1f),
            swing=listOf(0f,0f,0f,0f,0.75f,0f,0f),
            doSwingDur=true,
            swingDur=12,
            haste=true,
            using=true,
        ))
    }.withDependency { presets }
    private val poison by ActionSetting("Poison",""){
        setSettings(Preset(
            trans=listOf(0.1,0.1,0.0),
            rot=listOf(0,0,0),
            scale=listOf(0.75f,0.75f,0.75f),
            swing=listOf(-80f,-20f,-20f,-45f,0f,0f,0f),
            doSwingDur=true,
            swingDur=6,
            haste=true,
            using=true,
        ))
    }.withDependency { presets }
    data class Preset(
        val trans:List<Double>,
        val rot:List<Int>,
        val scale:List<Float>,
        val swing:List<Float>,
        val doSwingDur:Boolean,
        val swingDur:Int,
        val haste:Boolean,
        val using:Boolean,
    )
    @Suppress("UNCHECKED_CAST")
    private fun setSettings(p: Preset){
        (settings["x"] as NumberSetting<Double>).value = p.trans[0]
        (settings["y"] as NumberSetting<Double>).value = p.trans[1]
        (settings["z"] as NumberSetting<Double>).value = p.trans[2]
        (settings["x rot"] as NumberSetting<Int>).value = p.rot[0]
        (settings["y rot"] as NumberSetting<Int>).value = p.rot[1]
        (settings["z rot"] as NumberSetting<Int>).value = p.rot[2]
        (settings["width"] as NumberSetting<Float>).value = p.scale[0]
        (settings["height"] as NumberSetting<Float>).value = p.scale[1]
        (settings["length"] as NumberSetting<Float>).value = p.scale[2]
        (settings["Swing X Rot"] as NumberSetting<Float>).value = p.swing[0]
        (settings["Swing Y Rot"] as NumberSetting<Float>).value = p.swing[1]
        (settings["Swing Z Rot"] as NumberSetting<Float>).value = p.swing[2]
        (settings["Swing Offset Rot"] as NumberSetting<Float>).value = p.swing[3]
        (settings["Swing X"] as NumberSetting<Float>).value = p.swing[4]
        (settings["Swing Y"] as NumberSetting<Float>).value = p.swing[5]
        (settings["Swing Z"] as NumberSetting<Float>).value = p.swing[6]
        (settings["Translate Swing"] as BooleanSetting).value = p.swing[4] != 0f || p.swing[5] != 0f || p.swing[6] != 0f
        (settings["Custom Swing Duration"] as BooleanSetting).value = p.doSwingDur
        (settings["Swing Duration"] as NumberSetting<Int>).value = p.swingDur
        (settings["Swing While Using"] as BooleanSetting).value = p.using
        (settings["Ignore Haste"] as BooleanSetting).value = p.haste
    }
}

//TODO custom held item render pipeline
//is it possible to merge 2 function objects java/kotlin
//
//Yes, it is entirely possible to merge two function objects in both Java and Kotlin. Depending on what you mean by "merge" (such as executing them sequentially, composing them, or combining their inputs/outputs), both languages provide built-in utilities or straightforward idioms to achieve this.
//Kotlin
//
//Kotlin treats functions as first-class citizens and provides extension functions like andThen (via Java interoperability or custom definitions) and function composition out of the box.
//1. Sequential Execution (Run A, then Run B)
//
//If you have two functions of type () -> Unit (or accepting arguments), you can invoke them sequentially using a custom extension or simple lambda:
//Kotlin
//
//val func1: () -> Unit = { println("Hello from First") }
//val func2: () -> Unit = { println("Hello from Second") }
//
//// Merged function executing both sequentially
//val merged: () -> Unit = {
//    func1()
//    func2()
//}
//
//merged()
//// Output:
//// Hello from First
//// Hello from Second
//
//2. Function Composition (Pipeline: f(g(x)))
//
//To pass the output of one function as the input to another, you can use Kotlin's extension functions or infix operators:
//Kotlin
//
//val addTwo: (Int) -> Int = { it + 2 }
//val multiplyByThree: (Int) -> Int = { it * 3 }
//
//// Composition: multiplyByThree(addTwo(x))
//infix fun <T, R, V> ((T) -> R).andThen(after: (R) -> V): (T) -> V = { t -> after(this(t)) }
//
//val combined = addTwo andThen multiplyByThree
//
//println(combined(4)) // Output: 18 -> (4 + 2) * 3
//
//Java
//
//In Java, function objects are typically represented by functional interfaces from the java.util.function package, such as Function, Consumer, and Predicate. These interfaces come with built-in default methods for merging.
//1. Merging Consumer (Sequential Execution)
//
//If your functions take an argument and return nothing (void), use andThen:
//Java
//
//Consumer<String> consumer1 = s -> System.out.println("First: " + s);
//Consumer<String> consumer2 = s -> System.out.println("Second: " + s);
//
//// Merge using andThen
//Consumer<String> merged = consumer1.andThen(consumer2);
//
//merged.accept("Hello Java");
//// Output:
//// First: Hello Java
//// Second: Hello Java
//
//2. Merging Function (Composition / Pipeline)
//
//If your functions transform data, you can use andThen (execute left, then right) or compose (execute right, then left):
//Java
//
//Function<Integer, Integer> addTwo = x -> x + 2;
//Function<Integer, Integer> multiplyByThree = x -> x * 3;
//
//// Equivalent to multiplyByThree(addTwo(x))
//Function<Integer, Integer> combined = addTwo.andThen(multiplyByThree);
//
//System.out.println(combined.apply(4)); // Output: 18
//
//3. Merging Predicate (Boolean Logic)
//
//If you are combining conditional functions, you can use and, or, or negate:
//Java
//
//Predicate<String> isNotNull = s -> s != null;
//Predicate<String> isNotEmpty = s -> !s.isEmpty();
//
//// Merge using and
//Predicate<String> isValidString = isNotNull.and(isNotEmpty);
//
//System.out.println(isValidString.test("Test")); // Output: true
//
//i want to merge them into a single object that performs both actions upon being called
//
//Yes, you can combine multiple function objects into a single, unified object that wraps them both and executes them sequentially whenever it is called.
//Kotlin
//
//In Kotlin, you can create a single function reference or a custom functional interface instance that calls both actions.
//Using a Standard Lambda (Function Type)
//
//You can assign the merged behavior to a single variable. When invoked, it evaluates both blocks sequentially:
//Kotlin
//
//val action1: () -> Unit = { println("Action 1 executed") }
//val action2: () -> Unit = { println("Action 2 executed") }
//
//// Merged into a single function object
//val combinedAction: () -> Unit = {
//    action1()
//    action2()
//}
//
//// Called as a single object
//combinedAction()
//
//Accepting Arguments
//
//If your functions take inputs, the wrapper passes the arguments down to both targets:
//Kotlin
//
//val logConsole: (String) -> Unit = { println("Log: $it") }
//val saveToDb: (String) -> Unit = { println("Saving $it to database") }
//
//val combined: (String) -> Unit = { input ->
//    logConsole(input)
//    saveToDb(input)
//}
//
//combined("User Login")
//
//Java
//
//In Java, you can merge actions using built-in functional interfaces like Consumer (for procedures) or by defining a custom combination method.
//1. Using Consumer.andThen()
//
//If your functions take an argument and return nothing (void), andThen fuses them into a single Consumer instance:
//Java
//
//Consumer<String> action1 = s -> System.out.println("Action 1: " + s);
//Consumer<String> action2 = s -> System.out.println("Action 2: " + s);
//
//// Merged into a single Consumer object
//Consumer<String> combinedObject = action1.andThen(action2);
//
//// Invoked as a single object
//combinedObject.accept("Test Data");
//
//2. Using a Custom Wrapper for Parameterless Functions (Runnable)
//
//For parameterless functions (like Runnable), you can create a utility method or wrapper that returns a single Runnable object executing both:
//Java
//
//public class FunctionMerger {
//    public static Runnable merge(Runnable r1, Runnable r2) {
//        return () -> {
//            r1.run();
//            r2.run();
//        };
//    }
//
//    public static void main(String[] args) {
//        Runnable task1 = () -> System.out.println("First task");
//        Runnable task2 = () -> System.out.println("Second task");
//
//        // Merged into a single Runnable object
//        Runnable combinedTask = merge(task1, task2);
//
//        // Executed together as a single entity
//        combinedTask.run();
//    }
//}
//
//is there a way to do this automatically, without explicitly writing which 2 functions are being merged
//
//Yes! You can automate this so you don't have to manually write out wrapper lambdas every time.
//
//In Kotlin, you can achieve this elegantly using operator overloading (specifically the + operator). In Java, you can use built-in composition methods or a reusable helper.
//Kotlin: Using Operator Overloading (+)
//
//By defining a custom plus operator extension, you can "add" two functions together seamlessly, and Kotlin will handle the merging behind the scenes.
//1. For Parameterless Functions (() -> Unit)
//Kotlin
//
//// Define the automatic merge rule once globally
//operator fun (() -> Unit).plus(other: () -> Unit): () -> Unit = {
//    this()
//    other()
//}
//
//// --- Usage ---
//val func1 = { println("Task A") }
//val func2 = { println("Task B") }
//
//// Automatically merged without writing a new lambda!
//val combined = func1 + func2
//
//combined()
//// Output:
//// Task A
//// Task B
//
//2. For Functions with Arguments ((T) -> Unit)
//
//You can do the same for functions that accept parameters:
//Kotlin
//
//operator fun <T> ((T) -> Unit).plus(other: (T) -> Unit): (T) -> Unit = { input ->
//    this(input)
//    other(input)
//}
//
//// --- Usage ---
//val log: (String) -> Unit = { println("Logging: $it") }
//val save: (String) -> Unit = { println("Saving: $it") }
//
//// Automatically combined using '+'
//val auditAndSave = log + save
//
//auditAndSave("User_Token_123")
//
//Java: Using Built-in Methods or Helpers
//
//Java doesn't support operator overloading, but you can avoid writing boilerplate by using standard methods or a quick utility helper.
//1. For Consumer (Actions taking an argument)
//
//Java's Consumer interface already automates this via .andThen():
//Java
//
//Consumer<String> log = s -> System.out.println("Logging: " + s);
//Consumer<String> save = s -> System.out.println("Saving: " + s);
//
//// Automatically merged using the built-in method
//Consumer<String> combined = log.andThen(save);
//
//combined.accept("Data");
//
//2. For Runnable (Parameterless actions)
//
//For parameterless actions, you can write a tiny generic helper method once, then reuse it to combine any two runnables automatically:
//Java
//
//public class FunctionUtils {
//    // Reusable helper
//    public static Runnable combine(Runnable... tasks) {
//        return () -> {
//            for (Runnable task : tasks) {
//                task.run();
//            }
//        };
//    }
//}
//
//// --- Usage ---
//Runnable task1 = () -> System.out.println("One");
//Runnable task2 = () -> System.out.println("Two");
//
//// Automatically merged through the helper method
//Runnable combined = FunctionUtils.combine(task1, task2);
//
//combined.run();
//
//
//Gemini is AI and can make mistakes.