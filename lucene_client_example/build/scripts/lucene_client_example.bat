@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  lucene_client_example startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and LUCENE_CLIENT_EXAMPLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\lucene_client_example-0.0.1.jar;%APP_HOME%\lib\geode-lucene-1.14.0-build.0.jar;%APP_HOME%\lib\geode-gfsh-1.14.0-build.0.jar;%APP_HOME%\lib\geode-core-1.14.0-build.0.jar;%APP_HOME%\lib\geode-membership-1.14.0-build.0.jar;%APP_HOME%\lib\geode-tcp-server-1.14.0-build.0.jar;%APP_HOME%\lib\geode-serialization-1.14.0-build.0.jar;%APP_HOME%\lib\lucene-join-6.6.6.jar;%APP_HOME%\lib\lucene-grouping-6.6.6.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\spring-shell-1.2.0.RELEASE.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\micrometer-core-1.4.1.jar;%APP_HOME%\lib\javax.resource-api-1.7.1.jar;%APP_HOME%\lib\shiro-core-1.5.2.jar;%APP_HOME%\lib\geode-logging-1.14.0-build.0.jar;%APP_HOME%\lib\geode-common-1.14.0-build.0.jar;%APP_HOME%\lib\geode-management-1.14.0-build.0.jar;%APP_HOME%\lib\spring-web-5.2.5.RELEASE.jar;%APP_HOME%\lib\jgroups-3.6.14.Final.jar;%APP_HOME%\lib\jackson-databind-2.10.1.jar;%APP_HOME%\lib\jackson-annotations-2.10.1.jar;%APP_HOME%\lib\commons-validator-1.6.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\jaxb-impl-2.3.2.jar;%APP_HOME%\lib\commons-lang3-3.10.jar;%APP_HOME%\lib\fastutil-8.3.1.jar;%APP_HOME%\lib\jna-platform-5.5.0.jar;%APP_HOME%\lib\jna-5.5.0.jar;%APP_HOME%\lib\jopt-simple-5.0.4.jar;%APP_HOME%\lib\log4j-api-2.13.1.jar;%APP_HOME%\lib\classgraph-4.8.68.jar;%APP_HOME%\lib\rmiio-2.1.2.jar;%APP_HOME%\lib\geode-unsafe-1.14.0-build.0.jar;%APP_HOME%\lib\javax.activation-1.2.0.jar;%APP_HOME%\lib\istack-commons-runtime-3.0.11.jar;%APP_HOME%\lib\lucene-analyzers-phonetic-6.6.6.jar;%APP_HOME%\lib\lucene-analyzers-common-6.6.6.jar;%APP_HOME%\lib\lucene-queryparser-6.6.6.jar;%APP_HOME%\lib\lucene-core-6.6.6.jar;%APP_HOME%\lib\mx4j-3.0.2.jar;%APP_HOME%\lib\lucene-queries-6.6.6.jar;%APP_HOME%\lib\HdrHistogram-2.1.12.jar;%APP_HOME%\lib\LatencyUtils-2.0.3.jar;%APP_HOME%\lib\javax.transaction-api-1.3.jar;%APP_HOME%\lib\shiro-cache-1.5.2.jar;%APP_HOME%\lib\shiro-crypto-hash-1.5.2.jar;%APP_HOME%\lib\shiro-crypto-cipher-1.5.2.jar;%APP_HOME%\lib\shiro-config-ogdl-1.5.2.jar;%APP_HOME%\lib\shiro-config-core-1.5.2.jar;%APP_HOME%\lib\shiro-event-1.5.2.jar;%APP_HOME%\lib\shiro-crypto-core-1.5.2.jar;%APP_HOME%\lib\shiro-lang-1.5.2.jar;%APP_HOME%\lib\jackson-core-2.10.1.jar;%APP_HOME%\lib\httpclient-4.5.12.jar;%APP_HOME%\lib\spring-beans-5.2.5.RELEASE.jar;%APP_HOME%\lib\spring-core-5.2.5.RELEASE.jar;%APP_HOME%\lib\commons-beanutils-1.9.4.jar;%APP_HOME%\lib\commons-digester-1.8.1.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\findbugs-annotations-1.3.9-1.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\slf4j-api-1.7.26.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\spring-jcl-5.2.5.RELEASE.jar;%APP_HOME%\lib\jline-2.12.jar

@rem Execute lucene_client_example
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %LUCENE_CLIENT_EXAMPLE_OPTS%  -classpath "%CLASSPATH%" examples.ClientMain %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable LUCENE_CLIENT_EXAMPLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%LUCENE_CLIENT_EXAMPLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
