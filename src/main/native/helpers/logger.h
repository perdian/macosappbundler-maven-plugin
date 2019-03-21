#import <Cocoa/Cocoa.h>

void log_init(NSDictionary*);

void log_trace(NSString*, ...);
void log_debug(NSString*, ...);
void log_info(NSString*, ...);
void log_warn(NSString*, ...);
void log_error(NSString*, ...);
