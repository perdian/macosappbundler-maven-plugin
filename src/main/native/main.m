/*
 * macOS app bundler Maven plugin
 * Copyright 2019 Christian Seifert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#import <Cocoa/Cocoa.h>
#import <jni.h>
#import "helpers/logger.h"
#import "helpers/jvm_resolver.h"
#import "helpers/jvm_invoker.h"
#import "helpers/java_arguments_computer.h"

int exit_with_error(NSString *message, NSString *informativeText) {
    NSAlert *alert = [NSAlert new];
    [alert setMessageText:message];
    [alert setInformativeText:informativeText];
    [alert setAlertStyle:NSAlertStyleCritical];
    [alert runModal];
    return 1;
}

static NSArray *javaArguments;
static int javaArgumentsSupplied = 0;

int main(int argc, char *argv[]) {
    @try {

        NSBundle *applicationBundle = [NSBundle mainBundle];
        NSDictionary *applicationDictionary = [applicationBundle infoDictionary];
        log_init(applicationDictionary);

        NSString *applicationDirectory = [applicationBundle bundlePath];
        log_debug(@"Application directory: %@", applicationDirectory);
        chdir([applicationDirectory UTF8String]);

        NSString *javaVersionRequested = [applicationDictionary valueForKey:@"JVMVersion"];
        NSString *jvmDirectory = [JvmResolver resolveJvmDirectory:javaVersionRequested dictionary:applicationDictionary];
        NSString *jvmDylib = [JvmResolver resolveJvmDylibLocation:jvmDirectory];
        if (jvmDirectory == nil) {
            return exit_with_error(@"Cannot determine the location of a Java Virtual Machine sufficient for executing the application", [NSString stringWithFormat:@"Requested Java version: %@", [javaVersionRequested length] == 0 ? @"<ANY>" : javaVersionRequested]);
        } else if (jvmDylib == nil) {
            return exit_with_error(@"Cannot determine the location of the dynmic library inside the Java Virtual Machine sufficient for executing the application", [NSString stringWithFormat:@"Virtual Machine Location:\n%@", jvmDirectory]);
        }

        NSString *javaDataDirectory = [applicationDirectory stringByAppendingPathComponent:@"Contents/Java"];
        if (![[NSFileManager defaultManager] fileExistsAtPath:javaDataDirectory isDirectory:NULL]) {
            log_error(@"No 'Java' directory existing inside application bundle at: %@", javaDataDirectory);
            return exit_with_error(@"No 'Java' directory existing inside application bundle", [NSString stringWithFormat:@"Java directory expected at: %@", javaDataDirectory]);
        }

        // For some reasons the main function is called multiple times. I didn't look too much into this but it seems to be
        // related to multiple threads. (https://github.com/jitsi/jitsi/blob/master/src/native/macosx/launcher/launcher.m)
        // However we're really only interested for the parameters that are computed the very first time so that's all we'll use.
        if (javaArgumentsSupplied < 1) {
            NSMutableArray *commandLineArguments = [NSMutableArray array];
            for (int i = 1; i < argc; i++) {
                NSString *commandLineArgument = [[NSString alloc] initWithCString:argv[i] encoding:NSUTF8StringEncoding];
                [commandLineArguments addObject:commandLineArgument];
            }
            log_debug(@"Computing Java arguments from data directory: %@", javaDataDirectory);
            javaArguments = [JavaArgumentsComputer computeArguments:javaDataDirectory dictionary:applicationDictionary commandLineArguments:commandLineArguments];
            javaArgumentsSupplied++;
            log_debug(@"Computed Java arguments: %@", javaArguments);
        }

        return [JvmInvoker invoke:jvmDylib arguments:javaArguments];

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
