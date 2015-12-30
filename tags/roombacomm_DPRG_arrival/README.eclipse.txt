Instructions for running eclipse on windows with Roombacomm
-----------------------------------------------------------
Disclaimer: I'm not an eclipse expert. Please improve these instructions. However, eclipse is a really great
java IDE - once you learn how to use it you won't want to go back to the command-line.

Prerequisite: You have installed eclipse and a jdk, and pointed eclipse to a workspace.

- Create a new project from existing source in the folder you unzipped into. (Do not create it in the directory with the
source files.) Note the path that eclipse selects for the jre (for example, C:\Program Files\Java\jrel1.6.0_03).
- In the project properties, edit the build path to reference libraries:
  rxtxlib/RXTXcomm.jar and rxtxlib/bluecove-2.1.0.jar
- copy rxtxSerial.dll to the bin folder under the jre folder which eclipse selected
- Open "Run Configurations" and make a new run configuration called SimpleSerial. Search for the main class and select
SimpleSerial in the popup. In the arguments tab, type the name of the COM port you're using.
- Run the configuration you just created. You should see the same output in the console pane of eclipse as you saw
in the command prompt window if you followed the README.windows instructions.
- Make a new run configuration called RoombaCommTest, and select RoombaCommTest as the main class, as above. Run it and you
should get the GUI for driving Roomba around.