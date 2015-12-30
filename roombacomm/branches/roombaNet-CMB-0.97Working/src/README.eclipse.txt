Instructions for running eclipse on windows with Roombacomm
-----------------------------------------------------------
Disclaimer: I'm not an eclipse expert. Please improve these instructions. However, eclipse is a really great
java IDE - once you learn how to use it you won't want to go back to the command-line.

Prerequisite: You have installed eclipse and a jdk, and pointed eclipse to a workspace.

- Create a new project from existing source in the folder you unzipped into. (Do not create it in the directory with the
source files.) 
- Give the project the same name as the directory you downloaded into
- Change project layout: choose "use project folder as root for source & class files"
- Optionally select a project-specific jre that is the same as makeit.bat uses
- Check "create project from existing source"
- click next
- click the libraries tab
- Click the button to add external jars. Navigate to rxtxlib and select RXTXcomm.jar
- Open "Run Configurations" and make a new run configuration called SimpleSerial. Search for the main class and select
SimpleSerial in the popup. In the arguments tab, type the name of the COM port you're using.
- Run the configuration you just created. You should see the same output in the console pane of eclipse as you saw
in the command prompt window if you followed the README.windows instructions.
- Make a new run configuration called RoombaCommTest, and select RoombaCommTest as the main class, as above. Run it and you
should get the GUI for driving Roomba around.