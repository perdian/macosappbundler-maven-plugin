Maven plugin for creating a native [macOS bundle](https://developer.apple.com/library/archive/documentation/CoreFoundation/Conceptual/CFBundles/BundleTypes/BundleTypes.html#//apple_ref/doc/uid/10000123i-CH101-SW19) containing all dependencies required by a Maven project.

# Usage

The plugin will automatically detect whether or not the project is a Java 9+ module by checking if a source file `module-info.java` can be found in the projects classpath. If that's the case the launcher will use the *modulepath*. Otherwise the regular *classpath* will be set.

The plugin will automatically be executed during the `package` phase when it is included in the `pom.xml` of your project.

## Minimum example

```xml
 ...
    <plugin>
        <groupId>de.perdian.maven.plugins</groupId>
        <artifactId>macosappbundler-maven-plugin</artifactId>
        <version>0.1.0</version>
        <configuration>
            <plist>
                <JVMMainClassName>de.perdian.test.YourApplication</JVMMainClassName>
            </plist>
        </configuration>
    </plugin>
 ...
```

## Extended example

```xml
 ...
    <plugin>
        <groupId>de.perdian.maven.plugins</groupId>
        <artifactId>macosappbundler-maven-plugin</artifactId>
        <version>0.1.0</version>
        <configuration>
            <plist>
                <CFBundleIconFile>src/bundle/test.icns</CFBundleIconFile>
                <CFBundleDisplayName>My supercool application</CFBundleDisplayName>
                <JVMMainClassName>de.perdian.test.YourApplication</JVMMainClassName>
                <JVMVersion>11+</JVMVersion>
                <JVMOptions>
                    <string>-Dfoo=bar</string>
                    <string>-Dx=y</string>
                </JVMOptions>
                <JVMArguments>
                    <string>-example</string>
                    <string>${someProperty}</string>
                </JVMArguments>
            </plist>
            <dmg>
                <generate>true</generate>
            </dmg>
        </configuration>
    </plugin>
 ...
```


## Property list Configuration

The values within the `plist` element are directly transfered to the `Info.plist` file within the application bundle. To keep the usage within the code consistent they use the same keys within the `pom.xml` configuration as they do within the `Info.plist`.

The keys configuring the behaviour of the JVM are (as best as possibe) compatible with the keys used by the [`appbundle-maven-plugin`](https://github.com/federkasten/appbundle-maven-plugin) from [`federkasten`](https://github.com/federkasten) (which in turn uses the same keys as the original and meanwhile abandoned Java launcher from Oracle).

The following values can be configured:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `CFBundleIconFile` | File | No | | The `icns` file that should be used as main icon for the application. The location must be entered relatively to the root of the project in which the plugin is used. |
| `CFBundleIdentifier` | String | No | `${groupId}.${artifactId}` | The macOS identifier of your application bundle. |
| `CFBundleDisplayName` | String | No | `${project.name}` | The published name of your application. |
| `CFBundleName` | String | No | `${project.name}` | The internal name of your application. |
| `CFBundleShortVersionString` | String | No | `${version}` | The version of your application. |
| `CFBundleExecutable` | String | No | `JavaLauncher` | The name of the executable within the application bundle. No user will ever see this but you may want to change it for debugging purposes when analyzing your application. |
| `JVMMainClassName` | String | Yes (if the application is a classic classpath based application) | | The main class whose `main` method should be invoked when starting your application. |
| `JVMMainModuleName` | String | Yes (if the application is a module based application) | | The main module that should be invoked when starting your application. |
| `JVMVersion` | String | No | | The Java version your application needs to work. Can either be an explicit version String like `11.0.1`, a major version like `11` (signalizing that *any* Java 11 runtime is sufficient) or a value like `11+` (signalizing that *any* Java 11 *or higher* runtime is sufficient). |
| `JVMOptions` | Array of Strings | No | | Additional parameters (`-D` parameters) to be passed to the runtime. |
| `JVMArguments` | Array of Strings | No | | Additional arguments to be passed to the runtime. |
| `JVMRuntimePath` | String | No | | The exact location of the JVM. |

## DMG configuration

The following other properties can be added to the `dmg` element configuring the generation of the DMG file at the end of the build:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `generate` | Boolean | No | `false` | Whether or not to create a `DMG` file. |

# Development

The project consists of two main parts: The regular *Maven plugin* (written in Java) and the *native macOS launcher* (written in Objective C).

Building the native part is fully integrated into the Maven lifecycle, so all you need to build the plugin is:

    $ git clone https://github.com/perdian/macosappbundler-maven-plugin.git
    $ mvn clean install

I am aware that my understanding of Objective C is very basic - I'm a Java developer by heart and going back to using and pointers and (somehow) manual memory management feels pretty strange. So a lot of what's in the code is highly cargo culted from tutorials and answers on Stackoverflow, but hey: It works!

# Motivation and credits

I originally used and have been highly influenced by the [`appbundle-maven-plugin`](https://github.com/federkasten/appbundle-maven-plugin) from [`federkasten`](https://github.com/federkasten). Unfortunately the plugin stopped working with Java versions 10 and above plus it didn't provide support for Java 9+ module projects.
