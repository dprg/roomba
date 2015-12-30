/*
 * RoombaMidi2 -- MIDI to Roomba adapter
 * -----------
 * Creates a virtual MIDI destination that can be accessed by any CoreMIDI
 * applications like Ableton Live, Apple Logic, etc.
 * 
 * This is a Cocoa/C update to the original Java "RoombaMidi".
 *
 * Created 14 December 2006 
 * Copyright (c) 2006 Tod E. Kurt. tod@todbot.com http://hackingroomba.com/
 */

#import <Cocoa/Cocoa.h>
#import "MidiHandler.h"
#import "roombalib.h"

@interface RoombaMidi2 : NSWindowController
{
    MidiHandler*    midiHandler;
    Roomba*         roombas[16];
    NSMutableArray* records;       // model data for config table
    BOOL            midiRecv[16];  // which midi channels are receiving data
    NSTimer*        uiUpdateTimer;
    IBOutlet NSTableView*    tableView;
    IBOutlet NSTextView*     status;
    IBOutlet NSTextField*    currentChannel;
    IBOutlet NSTextField*    currentPort;
}
- (void)awakeFromNib;
- (BOOL)applicationShouldTerminateAfterLastWindowClosed:(NSApplication *)a;

- (IBAction)showHelp:(id)sender;

- (IBAction)backward:(id)sender;
- (IBAction)beep:(id)sender;
- (IBAction)forward:(id)sender;
- (IBAction)reset:(id)sender;
- (IBAction)spinleft:(id)sender;
- (IBAction)spinright:(id)sender;
- (IBAction)stop:(id)sender;
- (IBAction)vacuumOn:(id)sender;
- (IBAction)vacuumOff:(id)sender;
- (IBAction)ledsOn:(id)sender;
- (IBAction)ledsOff:(id)sender;

// midihandler callback
- (void)handleMIDIMessage:(Byte*)message ofSize:(int)size;

-(id)comboBoxCell:(NSComboBoxCell*)cell objectValueForItemAtIndex:(int)index;
-(int)numberOfItemsInComboBoxCell:(NSComboBoxCell*)cell;
-(int)comboBoxCell:(NSComboBoxCell*)cell indexOfItemWithStringValue:(NSString*)st;

-(int)numberOfRowsInTableView:(NSTableView*)tableView;
-(id)tableView:(NSTableView*)tableView objectValueForTableColumn:(NSTableColumn*)tableColumn row:(int)index;
-(void)tableView:(NSTableView*)tableView setObjectValue:(id)value forTableColumn:(NSTableColumn*)tableColumn row:(int)index;

-(void)tableView:(NSTableView*)tableView willDisplayCell:(id)cell forTableColumn:(NSTableColumn*)tableColumn row:(int)index;
//- (BOOL)tableView:(NSTableView *)aTableView shouldSelectRow:(int)rowIndex;

@end
