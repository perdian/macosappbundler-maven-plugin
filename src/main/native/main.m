/*
 * The MIT License
 *
 * Copyright (c) 2019 Christian Robert
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

        log_debug(@"Computing Java arguments from data directory: %@", javaDataDirectory);
        NSArray *javaArguments = [JavaArgumentsComputer computeArguments:javaDataDirectory dictionary:applicationDictionary];
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
