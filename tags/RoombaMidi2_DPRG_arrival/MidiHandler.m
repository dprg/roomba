/*
 * MidiHandler -- MIDI virtual source creation
 * -----------
 *
 * Borrows heavily from Peter Yandell's code posted to CoreAudio mailing list.
 *
 * Created 14 December 2006 
 * Copyleft (c) 2006 Tod E. Kurt. tod@todbot.com http://hackingroomba.com/
 */

#import "MidiHandler.h"


@implementation MidiHandler

static void midiReadProc (const MIDIPacketList* packetList, void* createRefCon,
                          void* connectRefConn);
static void midiNotifyProc (const MIDINotification* message, void* refCon);


+ (MidiHandler*)sharedInstance
{
    static MidiHandler* sharedInstance = nil;
    if (sharedInstance == nil) sharedInstance = [[MidiHandler alloc] init];
    return sharedInstance;
}

- (MidiHandler*)initWithName:(NSString*)name portName:(NSString*)portname
{
    MIDIClientCreate((CFStringRef)name, midiNotifyProc, (void*)self, &midiClientRef);
    
    MIDIDestinationCreate(midiClientRef, (CFStringRef)portname, midiReadProc, 
                           self, &newEndpoint);
    return self;
}

- (MidiHandler*)init
{
    return self;
}

- (void)dealloc
{
    [super dealloc];
}

- (void)setOwner:(id)theowner
{
    owner = theowner;
}

//- (void)handleMIDIMessage:(Byte*)message ofSize:(int)size
//{
//    NSLog(@"handleMIDIMessage: %x %x %x\n", message[0], message[1], message[2]);
//}

// borrowed heavily from Peter Yandell's SimpleSynth, http://pete.yandell.com/
- (void)processMIDIPacketList:(const MIDIPacketList*)packetList sender:(id)sender
{
    NSAutoreleasePool* pool = [[NSAutoreleasePool alloc] init];

    int i,j;
    const MIDIPacket* packet;
    Byte message[256];
    int messageSize = 0;
    // Step through each packet
    packet = packetList->packet;
    for (i = 0; i < packetList->numPackets; i++) {
        for (j = 0; j < packet->length; j++) {
            if (packet->data[j] >= 0xF8) continue; // skip over real-time
            // Hand off the packet when the next one starts
            if ((packet->data[j] & 0x80) != 0 && messageSize > 0) {
                [owner handleMIDIMessage:message ofSize:messageSize];
                messageSize = 0;
            }
            // push the data into the message
            message[messageSize++] = packet->data[j];
        }
        packet = MIDIPacketNext(packet);
    }
    if (messageSize > 0)
        [owner handleMIDIMessage:message ofSize:messageSize];

    [pool release];
}

static void midiReadProc (const MIDIPacketList* packetList, void* createRefCon,
                          void* connectRefConn)
{
    MidiHandler* handler = (MidiHandler*)createRefCon;
    [handler processMIDIPacketList:packetList sender:handler];
}

static void midiNotifyProc (const MIDINotification* message, void* refCon)
{
    NSLog(@"midiNotifyProc!\n");
}

@end
