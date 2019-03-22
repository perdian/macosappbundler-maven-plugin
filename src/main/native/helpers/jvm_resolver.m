#import "jvm_resolver.h"
#import "logger.h"

@implementation JvmResolver

+(int)executeTaskGuarded:(NSTask*)task {
    @try {
        [task launch];
        [task waitUntilExit];
        return [task terminationStatus];
    } @catch (NSException *exception) {
        log_error(@"System call to determine JVM location failed: %@\n%@", exception, [exception callStackSymbols]);
        return 1;
    }
}

+(NSString*)resolveJvmDirectoryForVersion:(NSString*)javaVersion {

    log_info(@"Looking up JVM directory for Java version: %@", [javaVersion length] == 0 ? @"<ANY>" : javaVersion);
    NSPipe *javaHomeLookupOutput = [NSPipe pipe];
    NSTask *javaHomeLookupTask = [NSTask new];
    [javaHomeLookupTask setStandardError:[NSPipe pipe]];
    [javaHomeLookupTask setStandardOutput:javaHomeLookupOutput];
    [javaHomeLookupTask setLaunchPath:@"/usr/libexec/java_home"];
    if ([javaVersion length] > 0) {
        [javaHomeLookupTask setArguments:[NSArray arrayWithObjects:@"-v", javaVersion, nil]];
    }

    log_trace(@"Executing task to compute supported JVMs supporting version %@: %@", javaVersion, javaHomeLookupTask);
    int javaHomeLookupTerminationStatus = [self executeTaskGuarded:javaHomeLookupTask];
    [javaHomeLookupTask release];
    if (javaHomeLookupTerminationStatus != 0) {
        log_error(@"Error status code returned from JVM home lookup: %i", javaHomeLookupTerminationStatus);
        @throw [NSException exceptionWithName:@"InvalidReturnCodeException" reason:@"Cannot determine location of JVM" userInfo:nil];
    }

    NSFileHandle *javaHomeLookupHandle = [javaHomeLookupOutput fileHandleForReading];
    NSData *javaHomeLookupResultData = [javaHomeLookupHandle readDataToEndOfFile];
    NSString *javaHomeLookupResult = [[NSString alloc] initWithData:javaHomeLookupResultData encoding:NSUTF8StringEncoding];
    log_info(@"System call returned JVM location at: %@", javaHomeLookupResult);

    return [javaHomeLookupResult stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];

}

+(NSString*)resolveJvmDirectory:(NSString*)javaVersion dictionary:(NSDictionary*)dictionary {
    NSString* explictJvmDirectory = [dictionary valueForKey:@"JVMRuntimePath"];
    if ([explictJvmDirectory length] > 0) {
        log_debug(@"Detected explict value for 'JVMRuntimePath' in application dictionary: %@", explictJvmDirectory);
        if (![[NSFileManager defaultManager] fileExistsAtPath:explictJvmDirectory isDirectory:NULL]) {
            log_error(@"Cannot find JVM directory (defined explicitely in application dictionary): %@", explictJvmDirectory);
            @throw [NSException exceptionWithName:@"FileNotFoundException" reason:@"Cannot find JVM directory (defined explicitely in application dictionary)" userInfo:@{@"description": [NSString stringWithFormat:@"Defined location:\n%@", explictJvmDirectory]}];
        } else {
            log_info(@"Using explicit value for 'JVMRuntimePath' set in application dictionary for JVM directory: %@", explictJvmDirectory);
        }
        return explictJvmDirectory;
    } else {
        return [self resolveJvmDirectoryForVersion:javaVersion];
    }
}

+(NSString*)resolveJvmDylibLocation:(NSString*)jvmDirectory {
    NSArray *dylibLocations = [NSArray arrayWithObjects:@"lib/jli/libjli.dylib",@"lib/libjli.dylib",nil];
    log_trace(@"Looking for dynamic library file, trying %i patterns inside JVM directory: %@", [dylibLocations count], jvmDirectory);
    for (id dylibLocation in dylibLocations) {
        log_trace(@"Looking for dynamic library file %@ inside JVM directory: %@", dylibLocation, jvmDirectory);
        NSString *dylibFileLocation = [jvmDirectory stringByAppendingPathComponent:dylibLocation];
        if ([[NSFileManager defaultManager] fileExistsAtPath:dylibFileLocation isDirectory:NULL]) {
            log_debug(@"Resolved JVM dylib to: %@", dylibFileLocation);
            return dylibFileLocation;
        }
    }
    return nil;
}

@end
