MyGUI Library
=======================
Created by John Beech
Contact: processing@mkv25.net
Version: 0010
=======================
-Quick installation notes-

Extract contents of this zip file to your processing directory /libraries/

This should create the sub folder called MyGUI/ with MyGUI_readme.txt inside, and a folder
called MyGUI/library/ with the file MyGUI.jar inside.

=======================
-Useage notes-

Please see the Documentation files contained within the MyGUI.jar archive.

Alternatively visit http://mkv25.net/MyGUI/doc/

=======================
-Version History-

ver 0010 - 2005-04-01
-Fixed   Set abstract class MyGUIObject to public (from default) so that it can be extended from within Processing

Ver 0009 - 2005-12-29
-Fixed   Arial-48.vlw moved to sub-folder /data/ inside .JAR, Processing now finds file.
-Fixed   MyGUIButton does not print message "MyGUIStyle has been set!" after setting a specific style.

Ver 0008
-Bug     Arial-48.vlw not found by processing, please include this in your project/sketch folder to use MyGUI library.

Ver 0007
*Note    First release as a library compiled using Eclipse
+Added   Basic documentation written
+Added   MyGUITextInput, which allows a user to enter and edit text in a field. No copy and paste support.
+Added   MyGUICheckbox, check box object.
+Added   MyGUIGroup, groups of objects can be moved/rotated
-Changed MyGUI now inherits properties/methods from MyGUIGroup (see documentation)

Ver 0006 < earlier
*Note    Original classes written from within Processing IDE including MyGUIButton, MyGUIButtonDrag, MyGuiImage, MyGUILabel, MyGUIPinSlider, MyGUIStyle. MyGUIObject used as an interface for compatable objects as well as containing basic box based hit detection code.

=======================
-Latest version-
Please check http://mkv25.net/MyGUI/

=======================
-Credits-
Created and designed by John Beech