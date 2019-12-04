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
#import "logger.h"

#define LOG_LEVEL_TRACE 1
#define LOG_LEVEL_DEBUG 2
#define LOG_LEVEL_INFO 3
#define LOG_LEVEL_WARN 4
#define LOG_LEVEL_ERROR 5

int log_level = LOG_LEVEL_INFO;
void log_init(NSDictionary *dictionary) {
    NSString* logLevelRequested = [dictionary valueForKey:@"JVMLogLevel"];
    if ([@"TRACE" compare:logLevelRequested options:NSCaseInsensitiveSearch] == NSOrderedSame) {
        log_level = LOG_LEVEL_TRACE;
    } else if ([@"DEBUG" compare:logLevelRequested options:NSCaseInsensitiveSearch] == NSOrderedSame) {
        log_level = LOG_LEVEL_DEBUG;
    } else if ([@"INFO" compare:logLevelRequested options:NSCaseInsensitiveSearch] == NSOrderedSame) {
        log_level = LOG_LEVEL_INFO;
    } else if ([@"WARN" compare:logLevelRequested options:NSCaseInsensitiveSearch] == NSOrderedSame) {
        log_level = LOG_LEVEL_WARN;
    } else if ([@"ERROR" compare:logLevelRequested options:NSCaseInsensitiveSearch] == NSOrderedSame) {
        log_level = LOG_LEVEL_ERROR;
    }
}

void log_with_level(NSString *formattedMessage, int level, NSString *levelString) {
    if (level >= log_level) {
        NSLog(@"%@ %@", levelString, formattedMessage);
    }
}

void log_trace(NSString *message, ...) {
    va_list args;
    va_start(args, message);
    NSString* formattedMessage = [[[NSString alloc] initWithFormat:message arguments:args] autorelease];
    va_end(args);
    log_with_level(formattedMessage, LOG_LEVEL_TRACE, @"[TRACE]");
}

void log_debug(NSString *message, ...) {
    va_list args;
    va_start(args, message);
    NSString* formattedMessage = [[[NSString alloc] initWithFormat:message arguments:args] autorelease];
    va_end(args);
    log_with_level(formattedMessage, LOG_LEVEL_DEBUG, @"[DEBUG]");
}

void log_info(NSString *message, ...) {
    va_list args;
    va_start(args, message);
    NSString* formattedMessage = [[[NSString alloc] initWithFormat:message arguments:args] autorelease];
    va_end(args);
    log_with_level(formattedMessage, LOG_LEVEL_INFO, @"[INFO ]");
}

void log_warn(NSString *message, ...) {
    va_list args;
    va_start(args, message);
    NSString* formattedMessage = [[[NSString alloc] initWithFormat:message arguments:args] autorelease];
    va_end(args);
    log_with_level(formattedMessage, LOG_LEVEL_WARN, @"[WARN ]");
}

void log_error(NSString *message, ...) {
    va_list args;
    va_start(args, message);
    NSString* formattedMessage = [[[NSString alloc] initWithFormat:message arguments:args] autorelease];
    va_end(args);
    log_with_level(formattedMessage, LOG_LEVEL_ERROR, @"[ERROR]");
}
