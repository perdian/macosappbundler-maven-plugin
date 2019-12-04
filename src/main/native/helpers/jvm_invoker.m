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
