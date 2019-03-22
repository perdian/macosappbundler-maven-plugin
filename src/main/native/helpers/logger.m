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
