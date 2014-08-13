MoparClassic
======
MoparClassic is an emulator for an old Java MMORPG.

Setup
======
Install the Java 7 JRE and JDK<br>
Install Apache Ant and Apache Ivy

Ensure all the paths are set correctly.

In each config file (world.xml for GS and Config.xml for LS), you need to specify the login server password, which is used to help secure the login server.

Go to LoginServer in the terminal/command prompt and type--
```
ant run
```
Go to GameServer in the terminal/command prompt and type--
```
ant run
```
You're now ready to start accept connections.

You may want to run the ConfigGenerator if you're not familiar with XML. It will ask you a series of questions to help customize the config files for you. It will then replace the current config files with the one's you've generated.

Modules
======
To compile modules go in the module's directory and run the following command--
```
ant build
```

To use the module copy the newly created JAR into the modules folder in the respective server.  You may need to change some configuration files for the module to be used.

If you change data stores, it's suggested that you use the data store conversion utility.

GameServer
======
Here is how to do various things in the GS.

Adding NPC dialog
------
Just look for an NPC that behaves similar to what you want (in the org.moparscape.msc.gs.npchandler package), then copy that class and modify it as needed. Once you're done, open up the DialogService (found in the org.moparscape.msc.gs.service package) and add the ID mappings.

Adding properties to the Player class, with saving
------
Create a class that holds the data you want to store, if appropriate. For the example, we'll use the following class:
```
public class SomeDefaultObject {
  public String example0 = "some value here"; // Will save.
  private String example1 = "some private value here"; // Will save.
  public transient String example2 = "some other value here"; // Will not save.
  public static String example3 = "some value here"; // Will not save.
}
```
Then, go to the constructor of the Player class, and do:
```
this.setProperty("uniquename", new SomeDefaultObject);
```
This will do everything needed for you. It will automatically pass all objects in there to the LS and save them, then send them back. To get the value back, all you need to do is this for Java:
```
SomeDefaultObject sdo = player.getProperty("uniquename");
System.out.println(sdo.example0);
```
And in Scala, you can do it like this or in similar manner to the above:
```
val sdo = player.getProperty[SomeDefaultObject]("uniquename")
println(sdo.example0)
```
Adding properties to the Player class, with saving
------
You can do the same thing as with saving, but for the class you're saving, just add the @Transient annotation to the class.

So, SomeDefaultObject would become:
```
import org.moparscape.msc.gs.util.annotation.Transient;

@Transient
public class SomeDefaultObject {
  public String example0 = "some value here";
  private String example1 = "some private value here";
  public transient String example2 = "some other value here";
  public static String example3 = "some value here";
}
```
So, with a simple annotation, we can make an entire property transient.

Adding commands
------
Go to the command handler (in the org.moparscape.msc.gs.phandler.client package) and look for the large match statement with a bunch of cases. Add a new case, with a lowercase name and no spaces, that will call the method you'll make. Make sure that you pass the arguments needed to the function. Once you do that, create the function, in the area the other functions are. Finally, go to the config folder and open up command-config.xml and add a permission entry and a comment above it, explaining what it does.

Adding ObjectActions
------
Just look for an ObjectAction that behaves similar to what you want (in the org.moparscape.msc.gs.event.handler.objectaction.impl package), then copy that class and modify as needed. Once you're done, open up the ObjectActionManager (in the org.moparscape.msc.gs.event.handler.objectaction package) and add the bindings.

Adding Quests
------
Create a class that extends Quest (in the org.moparscape.msc.gs.quest package) and put it in the org.moparscape.msc.gs.quest.impl package. All you need to do in the class is supply constructor arguments. Then, in the Quests class (in the org.moparscape.msc.gs.model.player.attribute package) add an instance of the quest you made to the list of quests. Now, go to the NPC handlers, object action handlers, etc. to add the quest content. You can increment the stages by doing the following:
```
player.quests.set(idOfNewQuest, player.quests.get(idOfNewQuest) + 1);
```
