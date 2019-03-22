#import <Cocoa/Cocoa.h>

@interface JvmInvoker : NSObject {
}

+(int)invoke:(NSString*)dylibLocation arguments:(NSArray*)arguments;

@end
