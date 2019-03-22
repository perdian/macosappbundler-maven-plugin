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
#import <dlfcn.h>
#import <jni.h>
#import "jvm_invoker.h"
#import "logger.h"

typedef int (JNICALL *JLI_Launch_t)(int argc, char ** argv,
                                    int jargc, const char** jargv,
                                    int appclassc, const char** appclassv,
                                    const char* fullversion,
                                    const char* dotversion,
                                    const char* pname,
                                    const char* lname,
                                    jboolean javaargs,
                                    jboolean cpwildcard,
                                    jboolean javaw,
                                    jint ergo);

@implementation JvmInvoker

+(int)invoke:(NSString*)dylibLocation arguments:(NSArray*)arguments {

    const char *dylibPath = [dylibLocation fileSystemRepresentation];
    log_trace(@"Using dylib file system location: %s", dylibPath);
    void *dylib = dlopen(dylibPath, RTLD_LAZY);
    if (dylib == NULL) {
        log_error(@"Cannot open JVM dynamic library at: %@", dylibLocation);
        @throw [NSException exceptionWithName:@"SystemConfigurationException" reason:@"Cannot open JVM dynamic library" userInfo:@{@"description": [NSString stringWithFormat:@"Dynamic library location:\n%@", dylibLocation]}];
    } else {
        JLI_Launch_t dylibSym = dlsym(dylib, "JLI_Launch");
        if (dylibSym == NULL) {
            log_error(@"Cannot open JVM dynamic library at: %@", dylibLocation);
            @throw [NSException exceptionWithName:@"SystemConfigurationException" reason:@"Cannot open JVM dynamic library" userInfo:@{@"description": [NSString stringWithFormat:@"Dynamic library location:\n%@", dylibLocation]}];
        } else {

            int argc = 1 + [arguments count];
            char *argv[argc];
            int i = 0;
            argv[i++] = "java";
            log_trace(@"Arguments");
            for (NSString *argument in arguments) {
                log_trace(@" [%i] %@", i-1, argument);
                argv[i++] = strdup([argument UTF8String]);
            }

            log_info(@"Invoking JVM at: %@\n---\n", dylibLocation);
            return dylibSym(argc, argv,
                            0, NULL,
                            0, NULL,
                            "",
                            "",
                            "java",
                            "java",
                            FALSE,
                            FALSE,
                            FALSE,
                            0
            );

        }
    }

    return 0;
}

@end
