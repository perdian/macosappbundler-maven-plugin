#import <Cocoa/Cocoa.h>

@interface JavaArgumentsComputer : NSObject {
}

+(NSArray*)computeArguments:(NSString*)javaDirectory dictionary:(NSDictionary*)dictionary; 

@end
