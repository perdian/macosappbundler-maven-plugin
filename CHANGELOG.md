# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.17.1...HEAD)

## [1.17.1](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.17.0...v1.17.1) - 2022-04-27

### Changed

- Embedded JDK or JRE is now stored inside the bundle in the `Contents/Java/runtime` directory (https://github.com/perdian/macosappbundler-maven-plugin/pull/36)

## [1.17.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.16.0...v1.17.0) - 2022-01-23

### Added

- Allow exclusion of dependencies when creating the application bundle.

## [1.16.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.15.0...v1.16.0) - 2022-01-23

### Added

- Support [`NSHumanReadableCopyright`](https://developer.apple.com/documentation/bundleresources/information_property_list/NSHumanReadableCopyright) configuration property.
- Support [`NSAppSleepDisabled`](https://developer.apple.com/documentation/bundleresources/information_property_list/NSAppSleepDisabled) configuration property.


## [1.15.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.14.0...v1.15.0) - 2021-12-06

### Added

- Support [`NSMicrophoneUsageDescription`](https://developer.apple.com/documentation/bundleresources/information_property_list/NSMicrophoneUsageDescription) configuration property.
- Support [`NSCameraUsageDescription`](https://developer.apple.com/documentation/bundleresources/information_property_list/NSCameraUsageDescription) configuration property.


## [1.14.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.13.0...v1.14.0) - 2021-10-17

### Added

- Support [`CFBundlePackageType`](https://developer.apple.com/documentation/bundleresources/information_property_list/CFBundlePackageType) configuration property.


## [1.13.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.12.0...v1.13.0) - 2021-10-03

### Added

- Support [`NSSupportsAutomaticGraphicsSwitching`](https://developer.apple.com/documentation/bundleresources/information_property_list/NSSupportsAutomaticGraphicsSwitching) configuration property.


## [1.12.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.11.0...v1.12.0) - 2021-07-22

### Fixed

- Attributes of additional resources copied into the generated bundle were not set.


## [1.11.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.10.2...v1.11.0) - 2021-06-02

### Added

- Allow adding additional resources into the generated bundle (`app` and `app/additionalResources` configuration elements).


## [1.10.2](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.10.1...v1.10.2) - 2021-04-04

### Added

- Made JDK lookup more lenient by checking `Contents/Home/` subdirectory.


## [1.10.1](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.10.0...v1.10.1) - 2021-03-12

### Changed

- Minimum Java version bumped to Java 9.


## [1.10.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.9.0...v1.10.0) - 2020-12-16

### Added

- Support for ARM64 processor architecture, both directly and via a universal binary (`nativeBinary` configuration element).


## [1.9.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.8.0...v1.9.0) - 2020-06-20

### Added

- Support [`LSUIElement`](https://developer.apple.com/documentation/bundleresources/information_property_list/LSUIElement) configuration property.

### Changed

- Ensure [`NSHighResolutionCapable`](https://developer.apple.com/documentation/bundleresources/information_property_list/NSHighResolutionCapable) is used as boolean configuration property.


## [1.8.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.7.0...v1.8.0) - 2020-06-07

### Added

- Example applications.

### Fixed

- Command line arguments were not correctly forwarded to native launcher binary.


## [1.7.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.6.0...v1.7.0) - 2020-02-20

### Fixed

- Executable flag on native launcher binary was only set for the owner, not for the group.


## [1.6.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.5.1...v1.6.0) - 2020-02-12

### Added

- Support [`CFBundleDevelopmentRegion`](https://developer.apple.com/documentation/bundleresources/information_property_list/CFBundleDevelopmentRegion) configuration property.
- Support [`CFBundleURLTypes`](https://developer.apple.com/documentation/bundleresources/information_property_list/CFBundleURLTypes) configuration property.


## [1.5.1](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.5.0...v1.5.1) - 2020-02-03

### Added

- Support [`NSHighResolutionCapable`](https://developer.apple.com/documentation/bundleresources/information_property_list/NSHighResolutionCapable) configuration property.


## [1.5.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.4.1...v1.5.0) - 2019-12-04

### Added

- Allow copying the complete JDK into the generated bundle (`jdk` configuration element).


## [1.4.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.3.0...v1.4.0) - 2019-12-01

### Added

- Clear working directory after build.

### Changed

- Reduce minimum macOS version to `10.12` (was `10.14`)

## [1.3.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.2.0...v1.3.0) - 2019-10-18

### Added

- Make DMG file name configurable (configuration option `dmgFileName`).
- Allow adding the version to the DMG file name (configuration option `appendVersion`).


## [1.2.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.1.0...v1.2.0) - 2019-10-13

### Added

- Allow usage of `genisoimage` for image generation (configuration options `useGenIsoImage` and `autoFallback`).


## [1.1.0](https://github.com/perdian/macosappbundler-maven-plugin/compare/v1.0.0...v1.1.0) - 2019-03-24

### Added

- Support for `JVMLogLevel`.

### Changed

- Use Apache License 2.0 instead of MIT License


## [1.0.0](https://github.com/perdian/macosappbundler-maven-plugin/releases/tag/v1.0.0) - 2019-03-22

Initial release.
