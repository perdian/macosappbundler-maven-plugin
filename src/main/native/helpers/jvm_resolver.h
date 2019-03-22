#import <Cocoa/Cocoa.h>

@interface JvmResolver : NSObject {
}

+(NSString*)resolveJvmDirectory:(NSString*)javaVersion dictionary:(NSDictionary*)dictionary; 
+(NSString*)resolveJvmDylibLocation:(NSString*)jvmDirectory;

@end
