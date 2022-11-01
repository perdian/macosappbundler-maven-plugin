# macOS app bundler Maven plugin

Maven plugin for creating a native [macOS bundle](https://developer.apple.com/library/archive/documentation/CoreFoundation/Conceptual/CFBundles/BundleTypes/BundleTypes.html#//apple_ref/doc/uid/10000123i-CH101-SW19) containing all dependencies declared by a Maven project.

[![Maven Central](https://img.shields.io/maven-central/v/de.perdian.maven.plugins/macosappbundler-maven-plugin.svg)](https://mvnrepository.com/artifact/de.perdian.maven.plugins/macosappbundler-maven-plugin)
[![License](http://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Build](https://img.shields.io/circleci/build/github/perdian/macosappbundler-maven-plugin/master)](https://circleci.com/gh/perdian/macosappbundler-maven-plugin)

## Requirements

- Java 9 or newer

## Usage

### Minimum example

```xml
...
    <plugin>
        <groupId>de.perdian.maven.plugins</groupId>
        <artifactId>macosappbundler-maven-plugin</artifactId>
        <version>1.90.0</version>
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
        <version>1.19.0</version>
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
            <codesign>
                <identity>3rd Party Mac Developer Application: MyName (MyNumber)</identity>
            </codesign>
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

### Example with Java Module Path

```xml
...
    <plugin>
        <groupId>de.perdian.maven.plugins</groupId>
        <artifactId>macosappbundler-maven-plugin</artifactId>
        <version>1.19.0</version>
        <configuration>
            <plist>
                <CFBundleIconFile>src/bundle/test.icns</CFBundleIconFile>
                <CFBundleDisplayName>My supercool application</CFBundleDisplayName>
                <CFBundleDevelopmentRegion>English</CFBundleDevelopmentRegion>
                <CFBundleURLTypes>
                    <string>msa</string>
                </CFBundleURLTypes>
                <JVMMainModuleName>de.perdian.somemodule/de.perdian.test.YourApplication</JVMMainModuleName>
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

The plugin will detect whether the project is a Java module by checking if the `plist` property `JVMMainModuleName` is present. If that's the case the launcher will use the *modulepath*. Otherwise the regular *classpath* will be used.

## Configuration

### Property list Configuration

The values within the `plist` element are directly transferred to the [`Info.plist`](https://developer.apple.com/documentation/bundleresources/information_property_list) file within the application bundle. To keep the usage within the code consistent they use the same keys within the `pom.xml` configuration as they do within the `Info.plist`.

The following values can be configured:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `CFBundleDevelopmentRegion` | String | No | `English` | The default language and region for the bundle, as a [language ID](https://developer.apple.com/library/archive/documentation/MacOSX/Conceptual/BPInternational/LanguageandLocaleIDs/LanguageandLocaleIDs.html). |
| `CFBundleDisplayName` | String | No | `${project.name}` | The published name of your application. |
| `CFBundleExecutable` | String | No | `JavaLauncher` | The name of the executable within the application bundle. No regular user will ever see this but you may want to change it for debugging purposes when analyzing your application. |
| `CFBundleIconFile` | File | No | | The `icns` file that should be used as main icon for the application. The location must be entered relatively to the root of the project in which the plugin is used. |
| `CFBundleIdentifier` | String | No | `${groupId}.${artifactId}` | The [macOS bundle identifier](https://developer.apple.com/documentation/bundleresources/information_property_list/cfbundleidentifier) of your application. |
| `CFBundleName` | String | No | `${project.name}` | The internal name of your application. |
| `CFBundlePackageType` | String | No | `APPL` | A four-letter code specifying the bundle type. For apps, the code is `APPL`, for frameworks, it' `FMWK`, and for bundles, it's `BNDL` ([Details](https://developer.apple.com/documentation/bundleresources/information_property_list/cfbundlepackagetype)) |
| `CFBundleShortVersionString` | String | No | `${version}` | The version of your application. |
| `CFBundleTypeExtensions` | Array of Strings | No |  | A list of file extensions this application can handle ([Details](https://developer.apple.com/library/archive/documentation/General/Reference/InfoPlistKeyReference/Articles/CoreFoundationKeys.html)). |
| `CFBundleURLTypes` | Array of Strings | No | | A list of URL schemes (`http`, `ftp`, etc.) supported by the application. |
| `JVMArguments` | Array of Strings | No | | Additional arguments to be passed to the Java runtime. |
| `JVMLogLevel` | String | No | `INFO` | The amount of details the launcher will print to the console if called directly from the command line. Possible values: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`. |
| `JVMMainClassName` | String | Yes (if the application is a classic classpath based application) | | The main class whose `main` method should be invoked when starting your application. |
| `JVMMainModuleName` | String | Yes (if the application is a module based application) | | The main module that should be invoked when starting your application. |
| `JVMOptions` | Array of Strings | No | | Additional parameters (`-D` parameters) to be passed to the Java runtime. |
| `JVMRuntimePath` | String | No | | The exact location of the Java runtime. |
| `JVMVersion` | String | No | | The Java version your application needs to work. Can either be an explicit version String like `11.0.1`, a major version like `11` (signalizing that *any* Java 11 runtime is sufficient) or a value like `11+` (signalizing that *any* Java 11 *or higher* runtime is sufficient). |
| `LSUIElement` | Boolean | No | | Declares if the application is an agent app that runs in the background and doesn't appear in the Dock ([Details](https://developer.apple.com/documentation/bundleresources/information_property_list/lsuielement)). |
| `NSAppSleepDisabled` | Boolean | No | | Declares if the app is allowed to nap or not. |
| `NSCameraUsageDescription` | String | No | | A message that tells the user why the app is requesting access to the device's camera ([Details](https://developer.apple.com/documentation/bundleresources/information_property_list/nscamerausagedescription)). |
| `NSHighResolutionCapable` | Boolean | No | `true` | Declares if the application supports rendering in HiDPI (Retina) ([Details](https://developer.apple.com/documentation/bundleresources/information_property_list/nshighresolutioncapable)). |
| `NSHumanReadableCopyright` | String | No | | A human-readable copyright notice for the bundle ([Details](https://developer.apple.com/documentation/bundleresources/information_property_list/nshumanreadablecopyright/)). |
| `NSMicrophoneUsageDescription` | String | No | | A message that tells the user why the application is requesting access to the device's microphone ([Details](https://developer.apple.com/documentation/bundleresources/information_property_list/nsmicrophoneusagedescription)). |
| `NSSupportsAutomatic` `GraphicsSwitching` | Boolean | No | `true` | Declares whether an OpenGL app may utilize the integrated GPU ([Details](https://developer.apple.com/documentation/bundleresources/information_property_list/nssupportsautomaticgraphicsswitching)). |

### DMG configuration

The following other properties can be added to the `dmg` element configuring the generation of the [DMG file](https://www.howtogeek.com/362166/what-is-a-dmg-file-and-how-do-i-use-one/) at the end of the build:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `generate` | Boolean | No | `false` | Whether or not to create a `DMG` archive. |
| `additionalResources` | List&lt;Fileset&gt; | No | | List of additional files to be copied into the archive. |
| `createApplicationsSymlink` | Boolean | No | `true` | Whether or not to include a link to the Applications folder inside the archive. |
| `useGenIsoImage` | Boolean | No | `false` | Whether or not to use `genisoimage` to create the archive. Default is `hdiutil`. |
| `autoFallback` | Boolean | No | `false` | If `true`, try the other archive generation method when the first one fails. (e.g. run `hdiutil` when `genisoimage` fails and vice-versa) |
| `appendVersion` | Boolean | No | `true` | If `true`, append the version to the `.dmg` name |
| `dmgFileName` | String | No | `null` | If not `null` and not empty, the supplied string will be used as the file name (`.dmg` will be appended). |

### APP configuration

The following other properties can be added to the `app` element configuring additional files to be included in the app bundle:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `additionalResources` | List&lt;Fileset&gt; | No | | Additional files to be copied into the app bundle. |

```xml
...
    <configuration>
        <app>
            <additionalResources>
                <resource>
                    <directory>${project.basedir}/src/main/resources</directory>
                    <outputDirectory>Contents/Resources</outputDirectory>
                    <includes>
                        <include>**</include>
                    </includes>
                </resource>
            </additionalResources>
        </app>
    </configuration>
...
```

### Code signing

The plugin can automatically sign the created application bundle if a codesign identiy is given:

```
...
    <configuration>
        <codesign>
            <identity>3rd Party Mac Developer Application: MyName (MyNumber)</identity>
        </codesign>
    </configuration>
...
```

The following other properties can be added to the `codesign` element configuring additional options for signing:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `enable` | Boolean | No | `true` | Whether or not to sign the created application bundle. |
| `identity` | String | Yes | | The identity of the signer. Required if the `codesign` element is present. |
| `preserveMetadata` | List&lt;String&gt; | No | `entitlements` | |

### JDK inclusion

Usually the application bundle built by the plugin will depend upon a Java runtime being available on the machine where the application is executed. To be completely self-sustaining, the plugin supports including the runtime into the target application. That runtime will then be used to launch the application, so there are no dependencies to a JDK being installed locally.

```xml
...
    <configuration>
        <jdk>
            <include>true</include>
            <location>/where/your/jdk/is/installed</location>
        </jdk>
    </configuration>
...
```

The following parameters can be set below the `jdk` configuration element:

| Key | Type | Required? | Default | Description |
| --- | ---- | --------- | ------- | ----------- |
| `include` | Boolean | No | `false` | Whether or not to include the JDK in the generated application bundle. |
| `location` | String | No | | The location of the JDK to be included. If no location is provided then the *currently used JDK* (which is the JDK that is used by the Maven binary) will be added to the application. |

### Dependencies exclusion

By default all declared dependencies (both direct dependencies as well as transient dependencies) are included in the generated application bundle.

If you only want to include the direct application JAR file without any dependencies (e.g. because you've already included the dependencies into the application JAR itself) then you can set the `includeDependencies` flag of the `app` configuration to `false`:

```xml
...
    <configuration>
        <app>
            <includeDependencies>false</includeDependencies>
        </app>
    </configuration>
...
```

### Native binary selection

By default the launcher contains a [universal binary](https://en.wikipedia.org/wiki/Universal_binary#Universal_2) that allows running the application on both the classic x86_64 as well as the new arm64 architecture.

In case any problems occur with the universal binary (or if you want to support only a specific architecture) you can select which binary should be bundled with your application via the `nativeBinary` setting:

```xml
 ...
    <configuration>
        <nativeBinary>X86_64</nativeBinary>
    </configuration>
 ...
```

The available values are:
- `UNIVERSAL` (the default if no explicit value is given)
- `X86_64`
- `ARM_64`

## Development

Changes are documented in the [`CHANGELOG.md`](CHANGELOG.md) file.

The project consists of two main parts: The regular *Maven plugin* (written in Java) and the *native macOS launcher* (written in Objective C).

Building the native part is fully integrated into the Maven lifecycle, so all you need to do to build the plugin is:

    $ git clone https://github.com/perdian/macosappbundler-maven-plugin.git
    $ mvn clean install

*I am aware that my understanding of Objective C is very basic - I'm not an Objective C developer by heart and going back to using pointers and (somewhat) manual memory management feels pretty strange. So a lot of what's in the code is highly cargo culted from tutorials and answers on Stackoverflow, but hey: It works!*

## Authors

- [**Christian Seifert**](http://www.perdian.de)

[![Donate](https://www.paypalobjects.com/en_US/DK/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=P94BEUKLPMEFJ)

See also the [list of contributors](https://github.com/perdian/macosappbundler-maven-plugin/contributors) who participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

I originally used and have been highly influenced by the [`appbundle-maven-plugin`](https://github.com/federkasten/appbundle-maven-plugin) from [`federkasten`](https://github.com/federkasten). Unfortunately the plugin stopped working with Java versions 10 and above (and didn't provide support for Java 9+ module projects).
