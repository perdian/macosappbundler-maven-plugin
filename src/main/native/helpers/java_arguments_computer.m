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
        @throw [NSException exceptionWithName:@"InvalidApplicationConfigurationException" reason:@"Invalid application configuration" userInfo:@{@"description": @"No JVMMainModuleName value has been defined in the Info.plist file.\nA main module is required for a module based application."}];
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
    NSString* mainClassName = [dictionary valueForKey:@"JVMMainClassName"];
    if ([mainClassName length] <= 0) {
        @throw [NSException exceptionWithName:@"InvalidApplicationConfigurationException" reason:@"Invalid application configuration" userInfo:@{@"description": @"No JVMMainClassName value has been defined in the Info.plist file.\nA main class is required for a classpath based application."}];
    } else {
        NSString *classpath = [self createClasspathValue:classpathDirectory];
        log_trace(@"Appending classpath application arguments");
        log_debug(@"Computed classpath directory: %@", classpathDirectory);
        log_info(@"Computed main class name: %@", mainClassName);
        [argumentsArray addObject:@"-classpath"];
        [argumentsArray addObject:classpath];
        [argumentsArray addObject:mainClassName];
    }
}

+(NSString*)createClasspathValue:(NSString*)classpathDirectory {
    return [self appendDirectoryToClasspath:classpathDirectory target:[NSMutableString string]];
}

+(NSMutableString*)appendDirectoryToClasspath:(NSString*)directory target:(NSMutableString*)classpath {
    NSArray* entries = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:directory error:NULL];
    for (id entry in entries) {
        NSString *entryFile = [directory stringByAppendingPathComponent:entry];
        BOOL entryIsDirectory = NO;
        if ([[NSFileManager defaultManager] fileExistsAtPath:entryFile isDirectory:&entryIsDirectory]) {
            if (entryIsDirectory) {
                [self appendDirectoryToClasspath:entryFile target:classpath];
            } else {
                if ([classpath length] > 0) {
                    [classpath appendString:@":"];
                }
                [classpath appendString:entryFile];
            }
        }
    }
    return classpath;
}

+(void)appendCommonSystemArguments:(NSMutableArray*)argumentsArray dictionary:(NSDictionary*)dictionary {
    NSArray* options = [dictionary valueForKey:@"JVMOptions"];
    if (options != nil && [options count] > 0) {
        for (id option in options) {
            [argumentsArray addObject:option];
        }
    }
}

+(void)appendCommonApplicationArguments:(NSMutableArray*)argumentsArray dictionary:(NSDictionary*)dictionary {
    NSArray* arguments = [dictionary valueForKey:@"JVMArguments"];
    if (arguments != nil && [arguments count] > 0) {
        for (id argument in arguments) {
            [argumentsArray addObject:argument];
        }
    }
}

@end
