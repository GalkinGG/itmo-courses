#!/bin/bash

javac -cp ../../java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar ../java-solutions/info/kgeorgiy/ja/galkin/implementor/Implementor.java
cd ../java-solutions/
jar cfmv ../scripts/Implementor.jar ../scripts/MANIFEST.MF info/kgeorgiy/ja/galkin/implementor/*.class
rm -f info/kgeorgiy/ja/galkin/implementor/*.class