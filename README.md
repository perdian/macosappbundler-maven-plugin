# macOS app bundler Maven plugin

Maven plugin for creating a native [macOS bundle](https://developer.apple.com/library/archive/documentation/CoreFoundation/Conceptual/CFBundles/BundleTypes/BundleTypes.html#//apple_ref/doc/uid/10000123i-CH101-SW19) containing all dependencies required by a Maven project.

[![Maven Central](https://img.shields.io/maven-central/v/de.perdian.maven.plugins/macosappbundler-maven-plugin.svg)](https://mvnrepository.com/artifact/de.perdian.maven.plugins/macosappbundler-maven-plugin)
[![License](http://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Build](https://img.shields.io/circleci/build/github/perdian/macosappbundler-maven-plugin/master)](https://circleci.com/gh/perdian/macosappbundler-maven-plugin)

## Usage

### Minimum example

```xml
 ...
    <plugin>
        <groupId>de.perdian.maven.plugins</groupId>
        <artifactId>macosappbundler-maven-plugin</artifactId>
        <version>1.8.0</version>
        <configuration>
            <plist>
                <JVMMainClassName>de.perdian.test.YourApplication</JVMMainClassName>
            </plist>
        </configuration>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>bundle</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
 ...
```

### Extended example

```xml
 ...
    <plugin>
        <groupId>de.perdian.maven.plugins</groupId>
        <artifactId>macosappbundler-maven-plugin</artifactId>
        <version>1.8.0</version>
        <configuration>
            <plist>
                <CFBundleIconFile>src/bundle/test.icns</CFBundleIconFile>
                <CFBundleDisplayName>My supercool application</CFBundleDisplayName>
                <CFBundleDevelopmentRegion>English</CFBundleDevelopmentRegion>
                <CFBundleURLTypes>
                    <string>msa</string>
                </CFBundleURLTypes>
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
                <additionalResources>
                    <additionalResource>
                        <directory>src/bundle/macos/distribution</directory>
                    </additionalResource>
                </additionalResources>
            </dmg>
        </configuration>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>bundle</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
 ...
```

## Features

After executing the goal during (e.g. during the `package` phase as shown in the example above) the macOS application bundle will be located in the `PROJECT_NAME.app` directory inside the `target` directory, where `PROJECT_NAME` equals the bundle name entered within the `CFBundleName` setting inside the `plist` configuration, or the name of the Maven project (`${project.name}`) if the value is not present inside the `plist` configuration.

The plugin will detect whether or not the project is a Java 9+ module by checking if the `plist` property `JVMMainModuleName` is present. If that's the case the launcher will use the *modulepath*. Otherwise the regular *classpath* will be used.

## Configuration

### Property list Configuration

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
| `CFBundleDevelopmentRegion` | String | No | `English` | The default language and region for the bundle, as a language ID. |
| `CFBundleURLTypes` | Array of Strings | No | | A list of URL schemes (http, ftp, and so on) supported by the app. |
| `JVMMainClassName` | String | Yes (if the application is a classic classpath based application) | | The main class whose `main` method should be invoked when starting your application. |
| `JVMMainModuleName` | String | Yes (if the application is a module based application) | | The main module that should be invoked when starting your application. |
| `JVMVersion` | String | No | | The Java version your application needs to work. Can either be an explicit version String like `11.0.1`, a major version like `11` (signalizing that *any* Java 11 runtime is sufficient) or a value like `11+` (signalizing that *any* Java 11 *or higher* runtime is sufficient). |
| `JVMOptions` | Array of Strings | No | | Additional parameters (`-D` parameters) to be passed to the runtime. |
| `JVMArguments` | Array of Strings | No | | Additional arguments to be passed to the runtime. |
| `JVMRuntimePath` | String | No | | The exact location of the JVM. |
| `JVMLogLevel` | String | No | `INFO` | The amount of details the launcher will print to the console if called directly from the command line. Possible values: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`. |
| `NSHighResolutionCapable` | Boolean | No | | Declares if the application supports rendering in HiDPI (Retina).

### DMG configuration

The following other properties can be added to the `dmg` element configuring the generation of the DMG file at the end of the build:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `generate` | Boolean | No | `false` | Whether or not to create a `DMG` file. |
| `additionalResources` | List<Fileset> | No | | Additional files to be copied into the archive. |
| `createApplicationsSymlink` | Boolean | No | `true` | Whether or not to include a link to the Applications folder inside the archive. |
| `useGenIsoImage` | Boolean | No | `false` | Whether or not to use `genisoimage` to create the archive. Default is `hdiutil`. |
| `autoFallback` | Boolean | No | `false` | If `true`, try the other archive generation method when the first one fails. (e.g. run `hdiutil` when `genisoimage` fails and vice-versa) |
| `appendVersion` | Boolean | No | `true` | If `true`, append version to `.dmg` name
| `dmgFileName` | String | No | `null` | If not `null` or empty, the supplied string will be used as the name (`.dmg` will be appended).

### JDK inclusion

To be completely self-sustaining, the plugin supports including a JDK into the target application.
That JDK will then be used to launch the application, so there are not dependencies to a JDK being installed locally.

```xml
 ...
    <configuration>
      <jdk>
        <include>true</include>
        <location>/where/your/jdk/is/instlled</location>
      </jdk>
    </configuration>
 ...
```

The following parameters can be set below the `jdk` configuration element:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `include` | Boolean | No | `false` | Whether or not to include the JDK in the application. |
| `location` | String | No | | The location of the JDK to be included. If no location is provided then the *currently used JDK* will be added to the application, which is the JDK that is used by the Maven script executing the build. |

## Development

The project consists of two main parts: The regular *Maven plugin* (written in Java) and the *native macOS launcher* (written in Objective C).

Building the native part is fully integrated into the Maven lifecycle, so all you need to do to build the plugin is:

    $ git clone https://github.com/perdian/macosappbundler-maven-plugin.git
    $ mvn clean install

I am aware that my understanding of Objective C is very basic - I'm a Java developer by heart and going back to using pointers and (somewhat) manual memory management feels pretty strange. So a lot of what's in the code is highly cargo culted from tutorials and answers on Stackoverflow, but hey: It works!

## Restrictions

As the file structure of the JDK structure has changed beginning with Java 9, the native launcher will most likely fail if you plan to run your application with Java 8 or lower.
However as Java 8 is end of life anyway, this shouldn't be a big issue.

## Authors

- [**Christian Seifert**](http://www.perdian.de)

See also the [list of contributors](https://github.com/perdian/macosappbundler-maven-plugin/contributors) who participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

I originally used and have been highly influenced by the [`appbundle-maven-plugin`](https://github.com/federkasten/appbundle-maven-plugin) from [`federkasten`](https://github.com/federkasten). Unfortunately the plugin stopped working with Java versions 10 and above plus it didn't provide support for Java 9+ module projects.
