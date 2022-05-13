# OpenSteganography
This repository contains the project of my bachelor thesis and allows the use of steganography in the image
formats PNG, BMP and JPEG. The library offers different techniques which are explained in the thesis,
available [here](someDayAWorkingLink)(german), and in javaDoc and tests.

The library can produce images which keep the hidden information intact after being published
on several social networks.

## Deployment
This is a java library. For most application purposes downloading the [latest release file](https://github.com/Katatunga/OpenSteganography/releases/tag/v1.0.0-beta) (*.jar) and including it in the project will suffice.

### From command line (Unix):
```
javac -cp .:/path/to/jar.jar:/path/to/myClass MyClass.java
java -cp .:/path/to/jar.jar:/path/to/myClass MyClass
```

### In Eclipse IDE:
please see https://stackoverflow.com/a/9870554
or alternatively http://mathcenter.oxford.emory.edu/site/cs170/externalLib/

### In IntelliJ IDE
please see https://stackoverflow.com/a/54189724
or alternatively https://www.jetbrains.com/help/idea/library.html

## Usage
For the most basic usage of the outer structure (interface `Steganography` and implementing class `ImageSteg`)
please refer to
[these test cases](https://github.com/Katatunga/OpenSteganography/blob/main/src/test/java/steganography/image/outerStructure/TestBasicUsage.java).
They contain some short utility methods which you can implement to provide your own images. If you use the source code, some
[test images](https://github.com/Katatunga/OpenSteganography/tree/main/src/test/resources/steganography/image) are included.

Other [tests](https://github.com/Katatunga/OpenSteganography/tree/main/src/test/java/steganography/image/innerStructure/integrations) 
offer more insight on how to construct and use the inner structure, but if you are considering this,
you should probably download and expand the code itself. There are extensive JavaDoc and comments and the
structure of the library adheres to well known design patterns, so the code should be understandable.
