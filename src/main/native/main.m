#import <Cocoa/Cocoa.h>
#import <jni.h>
#import "helpers/logger.h"
#import "helpers/jvm_resolver.h"

int exit_with_error(NSString *message, NSString *informativeText) {
    NSAlert *alert = [NSAlert new];
    [alert setMessageText:message];
    [alert setInformativeText:informativeText];
    [alert setAlertStyle:NSAlertStyleCritical];
    [alert runModal];
    return 1;
}

int main(int argc, char *argv[]) {
    @try {

        NSBundle *applicationBundle = [NSBundle mainBundle];
        NSDictionary *applicationDictionary = [applicationBundle infoDictionary];
        log_init(applicationDictionary);

        NSString *applicationDirectory = [applicationBundle bundlePath];
        log_debug(@"Application directory: %@", applicationDirectory);
        chdir([applicationDirectory UTF8String]);

        NSString *javaDataDirectory = [applicationDirectory stringByAppendingPathComponent:@"Contents/Java"];
        NSString *javaVersionRequested = [applicationDictionary valueForKey:@"JVMVersion"];
        NSString *jvmDirectory = [JvmResolver resolveJvmDirectory:javaVersionRequested dictionary:applicationDictionary];
        NSString *jvmDylib = [JvmResolver resolveJvmDylibLocation:jvmDirectory];

        if (![[NSFileManager defaultManager] fileExistsAtPath:javaDataDirectory isDirectory:NULL]) {
            log_error(@"No 'Java' directory existing inside application bundle at: %@", javaDataDirectory);
            return exit_with_error(@"No 'Java' directory existing inside application bundle", [NSString stringWithFormat:@"Java directory expected at: %@", javaDataDirectory]);
        } else if (jvmDirectory == nil) {
            return exit_with_error(@"Cannot determine the location of a Java Virtual Machine sufficient for executing the application", [NSString stringWithFormat:@"Requested Java version: %@", [javaVersionRequested length] == 0 ? @"<ANY>" : javaVersionRequested]);
        } else if (jvmDylib == nil) {
            return exit_with_error(@"Cannot determine the location of the dynmic library inside the Java Virtual Machine sufficient for executing the application", [NSString stringWithFormat:@"Virtual Machine Location:\n%@", jvmDirectory]);
        } else {
            
        }

        log_trace(@"Java data directory: %@", javaDataDirectory);

    } @catch (NSException *exception) {
        NSDictionary *userInfo = [exception userInfo];
        NSAlert *alert = [NSAlert new];
        [alert setMessageText:[exception reason]];
        if (userInfo != nil) {
            [alert setInformativeText:[userInfo valueForKey:@"description"]];
        }
        [alert setAlertStyle:NSAlertStyleCritical];
        [alert runModal];
        return 1;
    }
}
