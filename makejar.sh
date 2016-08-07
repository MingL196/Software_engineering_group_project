#!/usr/bin/env bash
rm -rf *.class; rm -rf *.jar
javac Main.java
jar cfm triviopoly.jar manifest.mf *.class default_settings.txt README
#jar cfm triviopoly.jar manifest.mf *.class default_settings.txt
rm *.class
