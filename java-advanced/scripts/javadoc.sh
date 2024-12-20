#!/bin/bash

javadoc -private -link https://docs.oracle.com/en/java/javase/17/docs/api/ \
-d ../java-solutions/info/kgeorgiy/ja/galkin/implementor/javadoc \
--class-path=../../java-advanced-2024/artifacts/info.kgeorgiy.java.advanced.implementor.jar \
../java-solutions/info/kgeorgiy/ja/galkin/implementor/Implementor.java \
../../java-advanced-2024/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Impler.java \
../../java-advanced-2024/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/JarImpler.java \
../../java-advanced-2024/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ImplerException.java