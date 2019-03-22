#import "java_arguments_computer.h"
#import "logger.h"

@implementation JavaArgumentsComputer

+(NSArray*)computeArguments:(NSString*)javaDirectory dictionary:(NSDictionary*)dictionary {
    NSMutableArray *resultArray = [NSMutableArray new];
    [self appendCommonSystemArguments:resultArray dictionary:dictionary];
    NSString *modulesDirectory = [javaDirectory stringByAppendingPathComponent:@"modules"];
    NSString *classpathDirectory = [javaDirectory stringByAppendingPathComponent:@"classpath"];
    if ([[NSFileManager defaultManager] fileExistsAtPath:modulesDirectory isDirectory:NULL]) {
        [self appendModulesApplicationArguments:resultArray modulesDirectory:modulesDirectory dictionary:dictionary];
    } else if ([[NSFileManager defaultManager] fileExistsAtPath:classpathDirectory isDirectory:NULL]) {
        [self appendClasspathApplicationArguments:resultArray classpathDirectory:classpathDirectory dictionary:dictionary];
    } else {
        @throw [NSException exceptionWithName:@"InvalidApplicationConfigurationException" reason:@"Invalid application configuration" userInfo:@{@"description": @"Neither a 'classpath' nor a 'modules' directory could be found inside the applications 'Java' folder."}];
    }
    [self appendCommonApplicationArguments:resultArray dictionary:dictionary];
    return resultArray;
}

+(void)appendModulesApplicationArguments:(NSMutableArray*)argumentsArray modulesDirectory:(NSString*)modulesDirectory dictionary:(NSDictionary*)dictionary {
    NSString* mainModuleName = [dictionary valueForKey:@"JVMMainModuleName"];
    if ([mainModuleName length] <= 0) {
        @throw [NSException exceptionWithName:@"InvalidApplicationConfigurationException" reason:@"Invalid application configuration" userInfo:@{@"description": @"No JVMMainModule value has been defined in the Info.plist file.\nA main module is required for a module based application."}];
    } else {
        log_trace(@"Appending module application arguments");
        log_debug(@"Computed modules directory: %@", modulesDirectory);
        log_info(@"Computed main module name: %@", mainModuleName);
        [argumentsArray addObject:@"--module-path"];
        [argumentsArray addObject:modulesDirectory];
        [argumentsArray addObject:@"--module"];
        [argumentsArray addObject:mainModuleName];
    }
}

+(void)appendClasspathApplicationArguments:(NSMutableArray*)argumentsArray classpathDirectory:(NSString*)classpathDirectory dictionary:(NSDictionary*)dictionary {
    log_info(@"Appending classpath application arguments based on classoath directory at: %@", classpathDirectory);
}

+(void)appendCommonSystemArguments:(NSMutableArray*)argumentsArray dictionary:(NSDictionary*)dictionary {
}

+(void)appendCommonApplicationArguments:(NSMutableArray*)argumentsArray dictionary:(NSDictionary*)dictionary {
}

@end
