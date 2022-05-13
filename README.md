# OpenSteganography
This repository contains the project of my bachelor thesis and allows the use of steganography in the image
formats PNG, BMP and JPEG. The library offers different techniques which are explained in the thesis,
available [here](someDayAWorkingLink)(german), and in javaDoc and tests.

The library can produce images which keep the hidden information intact after being published
on several social networks.

## Deployment from JAR
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

## Deployment of source
This is a maven repository. It includes one external JAR ([JWave](https://github.com/graetz23/JWave)) which is also a dependency.

### Open in Eclipse IDE
please see https://stackoverflow.com/a/2061645

### Open in IntelliJ IDE
Opening the repository should be enough.

### Tests
If you deployed the source code, you should first run all tests (src/test/java/...) to see if everything works smoothly.
If you use older jdk's, some tests might fail, namely:

`given_TIFF_when_encoding_expect_UnsupportedImageTypeException`
`given_TIFF_when_decoding_expect_UnsupportedImageTypeException`
`given_WBMP_when_encoding_expect_UnsupportedImageTypeException`

They fail because they caught the wrong exception. This is due to `javax.imagio.ImageIO` in some jdk's not producing the
image formats, which are not used in the library anyway. So either ignore or use newer versions of jdk.

## References
Following is a fraction of the references used to implement this project. These are probably the most important
while using the code. Additional references and explanation can be found in my [bachelor thesis](AworkingLinkSomeday).

- DWT - „Projects · graetz23/JWave“, GitHub. https://github.com/graetz23/JWave.
- DCT - „Fast discrete cosine transform algorithms“. https://www.nayuki.io/page/fast-discrete-cosine-transform-algorithms.
- SCTs - T. Filler, J. Judas, und J. Fridrich, „Minimizing embedding impact in steganography using trellis-coded quantization“, in Media Forensics and Security II, Jan. 2010, Bd. 7541, S. 38–51. doi: 10.1117/12.838002.
- DMAS - Y. Zhang, X. Zhu, C. Qin, C. Yang, und X. Luo, „Dither modulation based adaptive steganography resisting jpeg compression and statistic detection“, Multimed Tools Appl, Bd. 77, Nr. 14, S. 17913–17935, Juli 2018, doi: 10.1007/s11042-017-4506-3.
- DCRAS - Y. Zhang, X. Luo, C. Yang, D. Ye, und F. Liu, „A JPEG-Compression Resistant Adaptive Steganography Based on Relative Relationship between DCT Coefficients“, in 2015 10th International Conference on Availability, Reliability and Security, Toulouse, France, Aug. 2015, S. 461–466. doi: 10.1109/ARES.2015.53.
- ZXing („Zebra Crossing“) barcode scanning library for Java, Android. ZXing Project, 2022. Zugegriffen: 25. April 2022. [Online]. Verfügbar unter: https://github.com/zxing/zxing




