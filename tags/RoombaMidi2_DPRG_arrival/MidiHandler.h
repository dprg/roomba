/*
 * MidiHandler -- MIDI virtual source creation
 * -----------
 *
 * Borrows heavily from Peter Yandell's code posted to CoreAudio mailing list.
 *
 * Created 14 December 2006 
 * Copyleft (c) 2006 Tod E. Kurt. tod@todbot.com http://hackingroomba.com/
 */

#import <Foundation/Foundation.h>
#import <CoreMIDI/CoreMIDI.h>

@interface MidiHandler : NSObject {
    MIDIClientRef   midiClientRef;
    MIDIEndpointRef newEndpoint;
    id owner;
    //RoombaMidi2* owner;  // who owns this midihandler and thus receives the callback  FIXME
}

+ (MidiHandler*)sharedInstance;

- (MidiHandler*)initWithName:(NSString*)name portName:(NSString*)portname;
- (MidiHandler*)init;
- (void)dealloc;

- (void)setOwner:(id)owner;

@end
