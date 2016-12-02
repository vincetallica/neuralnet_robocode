@REM
@REM Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
@REM All rights reserved. This program and the accompanying materials
@REM are made available under the terms of the Eclipse Public License v1.0
@REM which accompanies this distribution, and is available at
@REM http://robocode.sourceforge.net/license/epl-v10.html
@REM

@echo off

if not exist "%~dp0\lib" (
   mkdir "%~dp0\lib"
)

if not exist "%~dp0\loader\Loader.class" (
    javac "%~dp0\loader\Loader.java"
)

java -cp "%~dp0/loader" Loader http://robocode.sourceforge.net/tools/libs/ "%~dp0/lib/" ant.jar ant-contrib-0.6.jar ant-launcher.jar jacobe.jar junit.jar classworlds-1.1.jar maven-2.2.1-uber.jar
