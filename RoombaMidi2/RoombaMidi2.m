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

#import "RoombaMidi2.h"
#import "roombalib.h"

@implementation RoombaMidi2

static NSString* noPortSelected = @"-choose port-";
static NSString* midiRecvStr = @"@";

- (id)init
{
    self = [super init];
    if(self)
        records = [[NSMutableArray arrayWithCapacity:16] retain];
    return self;
}

- (void)dealloc
{
    int i;
    for(i=0;i<16;i++)
        roomba_free(roombas[i]);
    [records release];
    [super dealloc];
}

- (void)awakeFromNib
{
    int i;
    for(i=0;i<16;i++)
        midiRecv[i] = NO;  // no midi right now
    
    midiHandler = [[MidiHandler alloc] initWithName:@"RoombaMidi2" portName:@"RoombaMidi2 In"];
    [midiHandler setOwner: self];
    
    NSMutableArray *ports = [NSMutableArray arrayWithCapacity:10];
    [ports addObject:noPortSelected];
    
    NSLog(@"finding serial ports...\n");
    NSString *file;
    NSDirectoryEnumerator *dirEnum = 
        [[NSFileManager defaultManager] enumeratorAtPath:@"/dev"];
    while (file = [dirEnum nextObject]) {
        if( [file hasPrefix: @"tty."] ) {
            [ports addObject:file];
            NSLog(@"found file: %@\n",file);
        }
    }
    NSArray *sortedPorts =
        [ports sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
    
    for(i=0; i<16; i++) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithCapacity:3];
        [dic setObject:[NSString stringWithFormat:@"%d",i+1] forKey:@"ch"];
        //[dic setObject: forKey:@"ch"];
        [dic setObject:sortedPorts forKey:@"combov"];
        [dic setObject:[sortedPorts objectAtIndex:0] forKey:@"combo"];
        [dic setObject:[NSNumber numberWithInt:NSOffState] forKey:@"connect"];
        [records addObject:dic];
    }

    // set to first row
    [tableView selectRowIndexes:[NSIndexSet indexSetWithIndex:0] byExtendingSelection:NO];
    
    uiUpdateTimer = [NSTimer scheduledTimerWithTimeInterval:0.2 target:self 
        selector:@selector(updateUI:) userInfo:nil repeats:YES];
    [uiUpdateTimer retain];
}

// When the window closes, exit (even if prefs still open.)
- (BOOL)applicationShouldTerminateAfterLastWindowClosed:(NSApplication *)a
{
    return YES;
}

- (IBAction)showHelp:(id)sender
{
    [[NSWorkspace sharedWorkspace]
        openFile:[[NSBundle mainBundle] pathForResource:@"Help" ofType:@"html"]];
}

// gui actions
- (IBAction)reset:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) {
        roomba_stop(roomba);
        const char* portpath = roomba_get_portpath(roomba);
        roomba_free(roomba);
        roomba = roomba_init(portpath);
        roombas[row] = roomba;
    }
}
- (IBAction)forward:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_forward(roomba);
}
- (IBAction)backward:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_backward(roomba);
}

- (IBAction)spinleft:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_spinleft(roomba);
}

- (IBAction)spinright:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_spinright(roomba);
}

- (IBAction)stop:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_stop(roomba);
}

- (IBAction)beep:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) {
        int note = (rand() % 36) + 48;
        roomba_play_note(roomba, note, 10);
    }
}

- (IBAction)vacuumOn:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_vacuum(roomba, 1);
}
- (IBAction)vacuumOff:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_vacuum(roomba, 0);
}
- (IBAction)ledsOn:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_set_leds(roomba, 1,1,1,1,1,1, 255,255);
}
- (IBAction)ledsOff:(id)sender
{
    int row = [tableView selectedRow];
    Roomba* roomba = roombas[row];
    if(roomba!=NULL) roomba_set_leds(roomba, 0,0,0,0,0,0, 0,0);
}

// where the magic happens
- (void)playMidiNote:(int)notenum withVelocity:(int)velocity onChannel:(int)channel
{
    NSLog(@"playing roomba #%d with note %d @ %d\n",channel+1, notenum, velocity);
    midiRecv[channel]=YES;
    
    Roomba* roomba = roombas[channel];    
    if( !roomba_valid(roomba) ) {
        NSLog(@"received message for null roomba at channel %d\n",channel+1);
        return;
    }
    if( notenum >= 31 ) {                // G and above
        if( velocity == 0 ) return;
        if( velocity < 4 ) velocity = 4; // has problems at lower durations
        else velocity = velocity/2;
        roomba_play_note(roomba, notenum, velocity);
    }
    else if( notenum == 24 ) {           // C
        roomba_vacuum(roomba, !(velocity==0) );
    }
    else if( notenum == 25 ) {           // C#
        uint8_t lon = (velocity!=0);
        uint8_t inten = (lon) ? 255:128; // either full bright or half bright   
        roomba_set_leds( roomba, lon,lon,lon,lon,lon,lon, velocity*2, inten );
    }
    else if( notenum == 27 ) {           // D#
        if( velocity!=0 ) 
            roomba_backward_at( roomba, velocity*3.9 ); // 3.9 == 500mmps/127
        else roomba_stop(roomba);
    }
    else if( notenum == 28 ) {           // E
        if( velocity!=0 ) 
            roomba_spinleft_at( roomba, velocity*3.9 );
        else roomba_stop(roomba);
    }
    else if( notenum == 29 ) {           // F
        if( velocity!=0 ) 
            roomba_spinright_at( roomba, velocity*3.9 );
        else roomba_stop(roomba);
    }
    else if( notenum == 30 ) {           // F#
        if( velocity!=0 ) 
            roomba_forward_at( roomba, velocity*3.9 );
        else roomba_stop(roomba);
    }
}

// callback from midimanager
- (void)handleMIDIMessage:(Byte*)message ofSize:(int)size
{
    NSLog(@"handleMIDIMessage: %x %x %x\n", message[0], message[1], message[2]);
    int command  = message[0] &  0xF0;
    int channel  = message[0] & ~0xF0;
    int notenum  = message[1];
    int velocity = message[2];
    if (command == 0x80) { // NOTE_OFF
        command = 0x90; velocity = 0;  // convert to NOTE_ON with zero velocity
    }
    if (command == 0x90) { // NOTE_ON
        [self playMidiNote:notenum withVelocity:velocity onChannel:channel];
    }
}

// periodic update to keep status refreshed
- (void) updateUI:(NSTimer*)timer
{
    [tableView reloadData];
    int index = [tableView selectedRow];
    if (index==-1) return;
    NSString* s = [[records objectAtIndex:index] objectForKey:@"combo"];
    [currentChannel setStringValue: [NSString stringWithFormat:@"%d",index+1]];
    [currentPort setStringValue: s];
}

/* data source for the NSComboBoxCell
it reads the data from the representedObject
the cell is responsible to display and manage the list of options
(we set representedObject in tableView:willDisplayCell:forTableColumn:row:)
this is optional, the alternative is to enter a list of values in interface builder */
-(id)comboBoxCell:(NSComboBoxCell*)cell objectValueForItemAtIndex:(int)index
{
    NSArray *values = [cell representedObject];
    if(values == nil)
        return @"";
    else
        return [values objectAtIndex:index];
}

-(int)numberOfItemsInComboBoxCell:(NSComboBoxCell*)cell
{
    NSArray *values = [cell representedObject];
    if(values == nil)
        return 0;
    else
        return [values count];
}

-(int)comboBoxCell:(NSComboBoxCell*)cell indexOfItemWithStringValue:(NSString*)st
{
    NSArray *values = [cell representedObject];
    if(values == nil)
        return NSNotFound;
    else
        return [values indexOfObject:st];
}

/* data source for the NSTableView
the table is responsible to display and record the user selection
(as opposed to the list of choices)
this is required */
-(int)numberOfRowsInTableView:(NSTableView*)tableView
{
    return [records count];
   //return 16;
}

-(id)tableView:(NSTableView*)tableView objectValueForTableColumn:(NSTableColumn*)tableColumn row:(int)index
{
    return [[records objectAtIndex:index] objectForKey:[tableColumn identifier]];
}

-(void)tableView:(NSTableView*)aTableView setObjectValue:(id)value 
  forTableColumn:(NSTableColumn*)tableColumn row:(int)index
{
    if(nil == value)
        value = @"";
    if([[tableColumn identifier] isEqual:@"combo"]) {
        [[records objectAtIndex:index] setObject:value forKey:@"combo"];
    }
    else if([[tableColumn identifier] isEqual:@"connect"]) {        
        NSMutableDictionary *dic = [records objectAtIndex:index];
        NSNumber* v = [dic objectForKey:@"connect"];
        NSLog(@"connect button :%@ %@ %d\n",value, v, NSOffState);
        
        if ([v intValue] == NSOffState) {
            NSString* portname = [dic objectForKey:@"combo"];
            if([portname isEqual:noPortSelected]) return; // gotta select a port
            // try to connect
            NSString* portpath = [NSString stringWithFormat:@"/dev/%@",portname];
            const char* cportpath = [portpath UTF8String];
            Roomba* roomba = roomba_init(cportpath);
            if (roomba != NULL) {
                roombas[index] = roomba;
                [dic setObject:[NSNumber numberWithInt:NSOnState] forKey:@"connect"];
                roomba_play_note(roomba, 64,10);
                NSLog(@"connected\n");
            }
            else {
                NSRunAlertPanel([NSString stringWithFormat:
                    @"The roomba port '%s' could not be opened",cportpath], 
                                [NSString stringWithFormat:
                                    @"%s",strerror(errno)], @"OK",nil,nil);
                NSLog(@"failed to connect to Roomba on %s\n", cportpath);
            }
        } else { // connected, so disconnect
            Roomba* roomba = roombas[index];
            roomba_free(roomba);
            [dic setObject:[NSNumber numberWithInt:NSOffState] forKey:@"connect"];
            NSLog(@"disconnected");
        }
    }
}


/* delegate for the NSTableView
since there's only one combo box for all the lines, we need to populate it with
the proper values for the line as set its selection, etc.
this is optional, the alternative is to set a list of values in interface builder  */
-(void)tableView:(NSTableView*)tableView willDisplayCell:(id)cell 
  forTableColumn:(NSTableColumn*)tableColumn row:(int)index
{
    NSDictionary *dic = [records objectAtIndex:index];
    NSString* colid = [tableColumn identifier];
    if ([colid isEqual:@"combo"] && [cell isKindOfClass:[NSComboBoxCell class]]) {
        [cell setRepresentedObject:[dic objectForKey:@"combov"]];
        [cell reloadData];
        [cell selectItemAtIndex:[self comboBoxCell:cell indexOfItemWithStringValue:[dic objectForKey:@"combo"]]];
        [cell setObjectValue:[dic objectForKey:@"combo"]];
    }
    else if ([colid isEqual:@"ch"]) {
        if (midiRecv[index]) { 
            NSLog(@"midireceived!\n");
            [cell setObjectValue:midiRecvStr];
            midiRecv[index]=NO;
        }
    }
}
//- (void)tableViewSelectionDidChange:(NSNotification *)aNotification
//{
//}
//- (BOOL)tableView:(NSTableView *)aTableView shouldSelectRow:(int)rowIndex
//{
//    return YES;
//}
    

@end
