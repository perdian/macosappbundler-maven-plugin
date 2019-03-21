#import <Cocoa/Cocoa.h>
#import <jni.h>
#import "helpers/logger.h"

int main(int argc, char *argv[]) {

    NSBundle* applicationBundle = [NSBundle mainBundle];
    NSDictionary* applicationDictionary = [applicationBundle infoDictionary];
    log_init(applicationDictionary);

    log_debug(@"Evaluating dictionary values");
    log_trace(@"Dictionary:\n%@", applicationDictionary);

}
