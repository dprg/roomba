/*
 * roombalib -- Roomba C API
 *
 * http://hackingroomba.com/
 *
 * Copyright (C) 2006, Tod E. Kurt, tod@todbot.com
 * Updates for Vex by Paul Bouchier
 *
 * Updates:
 * 14 Dec 2006 - added more functions to roombalib
 * 24 Nov 2010 - ported to Vex
 * 11 March 2011 - updated for BumpTurn on RoombaVex
 */

#define uint8_t unsigned byte

#define MAX_10MS 1000			// the highest we let the 10ms counter go before clearing it
#define normalize10ms(x) ((x) > MAX_10MS ? (x) - MAX_10MS : (x))

#define COMMANDPAUSE_MILLIS 200
#define SENSOR_DELAY 20
#define STOP_TIME 500
#define STOP_TIME10 50
#define BACKUP_TIME 200
#define BACKUP_TIME10 20
#define SURVEILLANCE_PAUSE 2000

// compiling for Vex hardware
#if defined(VEX)
#define DEFAULT_VELOCITY 200
#define SPIN_MIN 10
#define ANGLE_TO_TIME 4
#define FWD_MAX_TIME10 300

typedef struct {
    TUARTs portpath;
    int velocity;
    TUARTs fd;
    TBaudRate baud_rate;
} Roomba;
int roomba_send(TUARTs uartNum, int len );
void roomba_init();

#endif

// compiling for planet H99 simulator
#if defined(Simulator)
#define DEFAULT_VELOCITY 100
#define Btn6U 0
#define SONAR_THRESHOLD 40
#define SPIN_MIN 90
#define ANGLE_TO_TIME 5
#define FWD_MAX_TIME10 500

typedef struct {
    int velocity;
    int fd;
} Roomba;

int roomba_send(int uartNum, int len);  // fake the function out for the simulator

#endif

 // some simple macros of bit manipulations
#define bump_right(b)           ((b & 0x01)!=0)
#define bump_left(b)            ((b & 0x02)!=0)
#define wheeldrop_right(b)      ((b & 0x04)!=0)
#define wheeldrop_left(b)       ((b & 0x08)!=0)
#define wheeldrop_caster(b)     ((b & 0x10)!=0)

#define motorover_sidebrush(b)  ((b & 0x01)!=0)
#define motorover_vacuum(b)     ((b & 0x02)!=0)
#define motorover_mainbrush(b)  ((b & 0x04)!=0)
#define motorover_driveright(b) ((b & 0x08)!=0)
#define motorover_driveleft(b)  ((b & 0x10)!=0)



// Move Roomba with low-level DRIVE command
void roomba_drive( int velocity, int radius );

// run an autonomous pattern
#define MAX_PATTERNS 2
void pattern1();
void bumpTurn();
void autonomous_go(int pattern);

// play a little tune
void play_giddyup();

// stop the Roomba
void roomba_stop();

// Move Roomba forward at current velocity
void roomba_forward();
void roomba_forward_at( int velocity );

// Move Roomba backward at current velocity
void roomba_backward();
void roomba_backward_at( int velocity );

// Spin Roomba left at current velocity
void roomba_spinleft();
void roomba_spinleft_at( int velocity );
void roomba_spinleft_degrees( int degrees );

// Spin Roomba right at current velocity
void roomba_spinright();
void roomba_spinright_at( int velocity );
void roomba_spinright_degrees( int degrees );

// Set current velocity for higher-level movement commands
void roomba_set_velocity( int velocity );

// Get current velocity for higher-level movement commands
int roomba_get_velocity();

// play a musical note
void roomba_play_note( uint8_t note, uint8_t duration );
void roomba_beep (int beep_cnt);

// Turns on/off the non-drive motors (main brush, vacuum, sidebrush).
void roomba_set_motors( uint8_t mainbrush, uint8_t vacuum, uint8_t sidebrush);

// Turns on/off the various LEDs.
void roomba_set_leds( uint8_t status_green, uint8_t status_red,
                      uint8_t spot, uint8_t clean, uint8_t max, uint8_t dirt,
                      uint8_t power_color, uint8_t power_intensity );

// Turn all vacuum motors on or off according to state
void roomba_vacuum( uint8_t state );

// Get the sensor data from the Roomba
// returns -1 on failure
int roomba_read_sensors( );

// utility function
void roomba_delay( int millisecs );
#define roomba_wait roomba_delay
int check_for_button(int button);
void wait_button_release(int button);

    // offsets into sensor_bytes data
int BUMPSWHEELDROPS     = 0;
int WALL_DETECT         = 1;
int CLIFFLEFT           = 2;
int CLIFFFRONTLEFT      = 3;
int CLIFFFRONTRIGHT     = 4;
int CLIFFRIGHT          = 5;

    // bitmasks for various thingems
int BUMP_MASK           = 0x03;
int BUMPRIGHT_MASK      = 0x01;
int BUMPLEFT_MASK       = 0x02;
int WHEELDROPRIGHT_MASK = 0x04;
int WHEELDROPLEFT_MASK  = 0x08;
int WHEELDROPCENT_MASK  = 0x10;

int bumps_wheeldrops();
int cliff_left();
int cliff_frontleft();
int cliff_frontright();
int cliff_right();
int bump();
int bumpLeft();
int bumpRight();
int wheelDropLeft();
int wheelDropRight();
int wheelDropCenter();
int wall();
